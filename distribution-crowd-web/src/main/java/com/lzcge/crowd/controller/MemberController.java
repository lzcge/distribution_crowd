package com.lzcge.crowd.controller;

import com.lzcge.crowd.api.DataBaseOperationRemoteService;
import com.lzcge.crowd.api.MemberManagerRemoteService;
import com.lzcge.crowd.entity.MemberCert;
import com.lzcge.crowd.pojo.ResultEntity;
import com.lzcge.crowd.pojo.po.CertPO;
import com.lzcge.crowd.pojo.po.MemberPO;
import com.lzcge.crowd.pojo.po.OrderDetailPO;
import com.lzcge.crowd.pojo.vo.*;
import com.lzcge.crowd.util.CrowdConstant;
import com.lzcge.crowd.util.UploadUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import javax.xml.transform.Result;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

@Controller
public class MemberController {

	@Autowired
	private MemberManagerRemoteService memmberRemoteService;

	@Autowired
	private DataBaseOperationRemoteService dataBaseOperationRemoteService;



	@Autowired
	BCryptPasswordEncoder passwordEncoder;

	@Value(value = "${oss.project.parent.folder}")
	private String ossProjectParentFolder;

	@Value(value = "${oss.endpoint}")
	private String endpoint;

	@Value(value = "${oss.accessKeyId}")
	private String accessKeyId;

	@Value(value = "${oss.accessKeySecret}")
	private String accessKeySecret;

	@Value(value = "${oss.bucketName}")
	private String bucketName;

	@Value(value = "${oss.bucket.domain}")
	private String bucketDomain;

	//登录
	@PostMapping(value = "member/do/login.html", consumes = "application/json")
	@ResponseBody
	public Object doLogin(@RequestBody MemberVO memberVO, Model model, HttpSession httpSession) {
		String loginacct = memberVO.getLoginacct();
		String userpswd = memberVO.getUserpswd();
		String phoneNum = memberVO.getPhoneNum();
		String randomCode = memberVO.getRandomCode();
		//调用远程方法执行登录操作
		ResultEntity<MemberSignSuccessVO> resultEntity = memmberRemoteService.login(memberVO);
		//检查远程方法执行结果
		if (ResultEntity.FAILED.equals(resultEntity.getResult())) {
			//如果登录失败，返回失败信息到前端
			return ResultEntity.failed(resultEntity.getMessage());
//			model.addAttribute(CrowdConstant.ATTR_NAME_MESSAGE,resultEntity.getMessage());
//			return "login";
		}
		//如果登录成功，则获取对象，并放入到httpSession中，存入Redis
		MemberSignSuccessVO memberSignSuccessVO = resultEntity.getData();
		if (memberSignSuccessVO != null) {
			httpSession.setAttribute(CrowdConstant.ATTR_NAME_LOGIN_MEMBER, memberSignSuccessVO);
			return ResultEntity.successWithData(memberSignSuccessVO);
		}
		return ResultEntity.failed("false");

	}


	/**
	 * 注册
	 *
	 * @param memberVO
	 * @param model
	 * @param httpSession
	 * @return
	 */
	@PostMapping(value = "/member/do/reg.html", consumes = "application/json")
	@ResponseBody
	public Object doReg(@RequestBody MemberVO memberVO, Model model, HttpSession httpSession) {
		//调用远程方法执行登录操作
		ResultEntity<String> resultEntity = memmberRemoteService.register(memberVO);
		//检查远程方法执行结果
		if (ResultEntity.FAILED.equals(resultEntity.getResult())) {
			//如果登录失败，返回失败信息到前端
			return ResultEntity.failed(resultEntity.getMessage());
		} else {
			return ResultEntity.successNoData();
		}

	}

	//退出，用户带着session信息访问该方法，通过携带的session获取用户token
	@RequestMapping("member/logout.html")
	public String logout(HttpSession httpSession) {
		//从现有session中获取已登录的member对象
		MemberSignSuccessVO signSuccessVO = (MemberSignSuccessVO) httpSession.getAttribute(CrowdConstant.ATTR_NAME_LOGIN_MEMBER);
		//如果对象为null，则已经退出
		if (signSuccessVO == null) {
			return "redirect:/";
		}
		//从member对象中获取token
		String token = signSuccessVO.getToken();
		//调用远程方法执行退出操作
		ResultEntity<String> resultEntity = memmberRemoteService.logout(token);
		//远程方法当用失败则抛出异常
		if (ResultEntity.FAILED.equals(resultEntity.getResult())) {
			throw new RuntimeException(resultEntity.getMessage());
		}
		//销毁session
		httpSession.invalidate();
		return "redirect:/";
	}

	//微信 退出，销毁redis中的token信息
	@RequestMapping("/wx/member/logout")
	@ResponseBody
	public ResultEntity<String> wxlogout(String memberSignToken) {
		//调用远程方法执行退出操作
		ResultEntity<String> resultEntity = memmberRemoteService.logout(memberSignToken);
		//远程方法当用失败则抛出异常
		if (ResultEntity.FAILED.equals(resultEntity.getResult())) {
			throw new RuntimeException(resultEntity.getMessage());
		}

		return ResultEntity.successNoData();
	}


	//获取短信验证码
	@ResponseBody
	@PostMapping("member/do/sendCode")
	public ResultEntity getCode(@RequestParam("phoneNum") String phoneNum) {
		ResultEntity<String> resultEntity = memmberRemoteService.sendCode(phoneNum);
		if (ResultEntity.FAILED.equals(resultEntity.getResult())) {
			throw new RuntimeException(resultEntity.getMessage());
		}
		resultEntity.setData("success");
		return resultEntity;

	}

	/**
	 * 实名认证-更新账户类型
	 *
	 * @param memberVO
	 * @param session
	 * @return
	 */
	@ResponseBody
	@PostMapping("/member/do/updateAcctType")
	public ResultEntity updateAcctType(MemberVO memberVO, HttpSession session) {
		MemberSignSuccessVO loginMember = (MemberSignSuccessVO) session.getAttribute(CrowdConstant.ATTR_NAME_LOGIN_MEMBER);
		// 获取登录会员信息
		memberVO.setId(loginMember.getId());
		//带上token
		memberVO.setMemberSignToken(loginMember.getToken());
		ResultEntity<String> resultEntity = memmberRemoteService.updateAcctType(memberVO);
		//远程方法当用失败则抛出异常
		if (ResultEntity.FAILED.equals(resultEntity.getResult())) {
			return ResultEntity.failed(resultEntity.getMessage());
		}
		loginMember.setAccttype(memberVO.getAccttype());

		session.setAttribute(CrowdConstant.ATTR_NAME_LOGIN_MEMBER, loginMember);
		return ResultEntity.successNoData();

	}

	/**
	 * 实名认证-更新用户实名信息
	 *
	 * @param memberVO
	 * @param session
	 * @return
	 */
	@PostMapping("/member/do/updateBasicinfo")
	public String updateBasicinfo(MemberVO memberVO, Model model, HttpSession session) {

		// 获取登录会员信息
		MemberSignSuccessVO loginMember = (MemberSignSuccessVO) session.getAttribute(CrowdConstant.ATTR_NAME_LOGIN_MEMBER);
		memberVO.setId(loginMember.getId());
		memberVO.setAccttype(loginMember.getAccttype());
		//带上token
		memberVO.setMemberSignToken(loginMember.getToken());
		ResultEntity<String> resultEntity = memmberRemoteService.updateBasicinfo(memberVO);
		//远程方法当用失败则抛出异常
		if (ResultEntity.FAILED.equals(resultEntity.getResult())) {
			model.addAttribute(CrowdConstant.ATTR_NAME_MESSAGE, resultEntity.getMessage());
			return "member/basicinfo";
		}
		//查询出下一步中需要上传的资质
		//根据当前用户查询账户类型,然后根据账户类型查找需要上传的资质
		ResultEntity<List<CertPO>> certResultEntity = dataBaseOperationRemoteService.queryCertByAccttype(memberVO.getAccttype());
		//远程方法当用失败则抛出异常
		if (ResultEntity.FAILED.equals(certResultEntity.getResult())) {
			model.addAttribute(CrowdConstant.ATTR_NAME_MESSAGE, certResultEntity.getMessage());
			return "member/basicinfo";
		}
		List<CertPO> queryCertByAccttypeList = certResultEntity.getData();
		model.addAttribute("queryCertByAccttypeList", queryCertByAccttypeList);
		session.setAttribute("queryCertByAccttypeList", queryCertByAccttypeList);
		System.out.println(queryCertByAccttypeList.toString());
		return "member/uploadCert";

	}


	/**
	 * 实名认证-更新图片资质
	 *
	 * @param session
	 * @param ds
	 * @return
	 */
	@ResponseBody
	@PostMapping("/member/do/doUploadCert")
	public ResultEntity doUploadCert(HttpSession session, Data ds) {

		try {
			// 获取登录会员信息
			MemberSignSuccessVO loginMember = (MemberSignSuccessVO) session.getAttribute(CrowdConstant.ATTR_NAME_LOGIN_MEMBER);
//			String realPath = session.getServletContext().getRealPath("/pics");
			List<MemberCert> certimgs = ds.getCertimgs();
			for (MemberCert memberCert : certimgs) {
				MultipartFile fileImg = memberCert.getImgfile();
				//文件重命名
				String extName = fileImg.getOriginalFilename().substring(fileImg.getOriginalFilename().lastIndexOf("."));
				String tmpName = UUID.randomUUID().toString() + extName;

				//上传到oss服务器

				//文件夹名称
				String foldName = UploadUtil.generateFoldNameByDate(ossProjectParentFolder);
				//获取文件输入流
				InputStream inputStream = fileImg.getInputStream();
				//上传到oss服务器
				UploadUtil.uploadSingleFile(endpoint, accessKeyId, accessKeySecret, tmpName, foldName, bucketName, inputStream);
				//拼接图片在服务器的路径
				String detailPicPath = bucketDomain + "/" + foldName + "/" + tmpName;

//				String filename = realPath + "\\cert\\" + tmpName;
//
//				fileImg.transferTo(new File(filename));    //资质文件上传.
				//准备数据
				memberCert.setIconpath(detailPicPath); //封装数据,保存数据库
				memberCert.setMemberid(loginMember.getId());
			}
			for (MemberCert memberCert : certimgs) {
				memberCert.setImgfile(null);
			}

			ResultEntity<String> resultEntity = memmberRemoteService.saveMemberCert(certimgs);
			//远程方法当用失败则抛出异常
			if (ResultEntity.FAILED.equals(resultEntity.getResult())) {
				return ResultEntity.failed(resultEntity.getMessage());
			}
			return ResultEntity.successNoData();
		} catch (Exception E) {
			E.printStackTrace();
			return ResultEntity.failed(E.getMessage());
		}

	}


	/**
	 * 实名认证-邮箱发送验证码
	 *
	 * @return
	 */
	@ResponseBody
	@PostMapping("/member/do/sendEmailCode")
	public ResultEntity sendEmailCode(MemberVO memberVO) {
		ResultEntity<String> resultEntity = memmberRemoteService.sendEmailCode(memberVO);
		if (ResultEntity.FAILED.equals(resultEntity.getResult())) {
			return ResultEntity.failed(resultEntity.getMessage());
		}
		return ResultEntity.successNoData();

	}

	/**
	 * 实名认证-验证发送验证码
	 *
	 * @return
	 */
	@ResponseBody
	@PostMapping(value = "/member/do/finishApply")
	public ResultEntity finishEmailApply(MemberVO memberVO, HttpSession session) {
		ResultEntity<String> resultEntity = memmberRemoteService.finishEmailApply(memberVO);
		if (ResultEntity.FAILED.equals(resultEntity.getResult())) {
			return ResultEntity.failed(resultEntity.getMessage());
		}
		//更新用户认证状态
		// 获取登录会员信息
		MemberSignSuccessVO loginMember = (MemberSignSuccessVO) session.getAttribute(CrowdConstant.ATTR_NAME_LOGIN_MEMBER);
		MemberVO memberVO1 = new MemberVO();
		memberVO1.setId(loginMember.getId());
		memberVO1.setAuthstatus("1");
		memberVO1.setMemberSignToken(loginMember.getToken());
		ResultEntity<String> stringResultEntity = memmberRemoteService.updateBasicinfo(memberVO1);
		if (ResultEntity.FAILED.equals(stringResultEntity.getResult())) {
			return ResultEntity.failed(stringResultEntity.getMessage());
		}
		loginMember.setAuthstatus("1");
		session.setAttribute(CrowdConstant.ATTR_NAME_LOGIN_MEMBER, loginMember);
		return ResultEntity.successNoData();

	}


	/**
	 * 删除用户订单
	 *
	 * @param orderid
	 * @return
	 */
	@PostMapping("/delete/order/by/orderid")
	@ResponseBody
	public ResultEntity<String> deleteOrderByorderid(Integer orderid) {
		ResultEntity<String> resultEntity = memmberRemoteService.deleteOrderByorderid(orderid);
		if (ResultEntity.FAILED.equals(resultEntity.getResult())) {
			return ResultEntity.failed(resultEntity.getMessage());
		}

		return ResultEntity.successNoData();
	}


	/**
	 * 删除收获地址
	 *
	 * @param id
	 * @return
	 */
	@PostMapping("/delete/address/by/addressid")
	@ResponseBody
	public ResultEntity<String> deleteAddress(Integer id) {
		ResultEntity<String> resultEntity = memmberRemoteService.deleteAddress(id);
		if (ResultEntity.FAILED.equals(resultEntity.getResult())) {
			return ResultEntity.failed(resultEntity.getMessage());
		}

		return ResultEntity.successNoData();
	}


	/**
	 * 查询个人信息
	 *
	 * @return
	 */
	@PostMapping("/member/do/query/myinfo")
	@ResponseBody
	public ResultEntity queryMyinfo(MemberVO memberVO) {
		if (memberVO.getId() == null) {
			return ResultEntity.failed("id为空");
		}
		if (memberVO.getMemberSignToken() == null) {
			return ResultEntity.failed("登录信息失效，请重新登录");
		}
		ResultEntity<MemberPO> memberPOResultEntity = memmberRemoteService.queryByid(memberVO);
		if (ResultEntity.FAILED.equals(memberPOResultEntity.getResult())) {
			return ResultEntity.failed(memberPOResultEntity.getMessage());
		}

		return ResultEntity.successWithData(memberPOResultEntity.getData());

	}


	/**
	 * 修改个人资料
	 *
	 * @return
	 */
	@PostMapping("/member/do/update/myinfo")
	@ResponseBody
	public ResultEntity updateMyinfo(Integer id, String username, String loginacct, String phoneNum, String email, String oldpwd,
									 String newpwd, String memberSignToken) {

		MemberVO memberVO = new MemberVO();
		if (id == null) {
			return ResultEntity.failed("id为空");
		}
		memberVO.setId(id);
		if (memberSignToken == null || memberSignToken.equals("")) {
			return ResultEntity.failed("登录信息失效，请重新登录");
		}
		//带上token
		memberVO.setMemberSignToken(memberSignToken);
		if (username != null && !username.equals("")) {
			memberVO.setUsername(username);
		}
		if (loginacct != null && !loginacct.equals("")) {
			memberVO.setLoginacct(loginacct);
		}
		if (phoneNum != null && !phoneNum.equals("")) {
			memberVO.setPhoneNum(phoneNum);
		}
		if (email != null && !email.equals("")) {
			memberVO.setEmail(email);
		}
		if (oldpwd != null && !oldpwd.equals("")) {
			ResultEntity<MemberPO> memberPOResultEntity = memmberRemoteService.queryByid(memberVO);
			if (ResultEntity.FAILED.equals(memberPOResultEntity.getResult())) {
				return ResultEntity.failed(memberPOResultEntity.getMessage());
			}
			MemberPO memberPO = memberPOResultEntity.getData();
			//判断输入的原密码
			//比较数据库加密后的密码和前台传入的密码
			boolean matches = passwordEncoder.matches(oldpwd, memberPO.getUserpswd());
			if (!matches) {
				return ResultEntity.failed("原密码输入有误");
			}
			memberVO.setUserpswd(passwordEncoder.encode(newpwd));

		}


		ResultEntity<String> resultEntity = memmberRemoteService.updateBasicinfo(memberVO);
		//远程方法当用失败则抛出异常
		if (ResultEntity.FAILED.equals(resultEntity.getResult())) {
			return ResultEntity.failed(resultEntity.getMessage());
		}

		return ResultEntity.successNoData();

	}

	/**
	 * 用户创建投诉反馈
	 * @param complainVO
	 * @return
	 */
	@RequestMapping("/member/complain")
	@ResponseBody
	public ResultEntity complain(ComplainVO complainVO){
		ResultEntity<String > resultEntity = memmberRemoteService.saveComplain(complainVO);
		if(ResultEntity.FAILED.equals(resultEntity.getResult())){
			return ResultEntity.failed(resultEntity.getMessage());
		}
		return ResultEntity.successNoData();

	}


}
