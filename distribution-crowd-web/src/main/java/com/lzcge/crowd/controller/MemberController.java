package com.lzcge.crowd.controller;

import com.lzcge.crowd.api.DataBaseOperationRemoteService;
import com.lzcge.crowd.api.MemberManagerRemoteService;
import com.lzcge.crowd.entity.MemberCert;
import com.lzcge.crowd.pojo.ResultEntity;
import com.lzcge.crowd.pojo.po.CertPO;
import com.lzcge.crowd.pojo.po.OrderDetailPO;
import com.lzcge.crowd.pojo.vo.Data;
import com.lzcge.crowd.pojo.vo.MemberSignSuccessVO;
import com.lzcge.crowd.pojo.vo.MemberVO;
import com.lzcge.crowd.pojo.vo.OrderVO;
import com.lzcge.crowd.util.CrowdConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.util.*;

@Controller
public class MemberController {

	@Autowired
	private MemberManagerRemoteService memmberRemoteService;

	@Autowired
	private DataBaseOperationRemoteService dataBaseOperationRemoteService;

	//登录
	@PostMapping(value = "member/do/login.html",consumes = "application/json")
	@ResponseBody
	public Object doLogin(@RequestBody MemberVO memberVO, Model model, HttpSession httpSession){
		String loginacct = memberVO.getLoginacct();
		String userpswd = memberVO.getUserpswd();
		String phoneNum = memberVO.getPhoneNum();
		String randomCode = memberVO.getRandomCode();
		//调用远程方法执行登录操作
		ResultEntity<MemberSignSuccessVO> resultEntity = memmberRemoteService.login(memberVO);
		//检查远程方法执行结果
		if(ResultEntity.FAILED.equals(resultEntity.getResult())){
			//如果登录失败，返回失败信息到前端
			return ResultEntity.failed(resultEntity.getMessage());
//			model.addAttribute(CrowdConstant.ATTR_NAME_MESSAGE,resultEntity.getMessage());
//			return "login";
		}
		//如果登录成功，则获取对象，并放入到httpSession中，存入Redis
		MemberSignSuccessVO memberSignSuccessVO = resultEntity.getData();
		if (memberSignSuccessVO != null) {
			httpSession.setAttribute(CrowdConstant.ATTR_NAME_LOGIN_MEMBER,memberSignSuccessVO);
			return  ResultEntity.successWithData(memberSignSuccessVO);
		}
		return  ResultEntity.failed("false");

	}


	/**
	 * 注册
	 * @param memberVO
	 * @param model
	 * @param httpSession
	 * @return
	 */
	@PostMapping(value = "/member/do/reg.html",consumes = "application/json")
	@ResponseBody
	public Object doReg(@RequestBody MemberVO memberVO, Model model, HttpSession httpSession){
		//调用远程方法执行登录操作
		ResultEntity<String> resultEntity = memmberRemoteService.register(memberVO);
		//检查远程方法执行结果
		if(ResultEntity.FAILED.equals(resultEntity.getResult())){
			//如果登录失败，返回失败信息到前端
			return ResultEntity.failed(resultEntity.getMessage());
		}else{
			return ResultEntity.successNoData();
		}

	}

	//退出，用户带着session信息访问该方法，通过携带的session获取用户token
	@RequestMapping("member/logout.html")
	public String logout(HttpSession httpSession){
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
		if(ResultEntity.FAILED.equals(resultEntity.getResult())){
			throw new RuntimeException(resultEntity.getMessage());
		}
		//销毁session
		httpSession.invalidate();
		return "redirect:/";
	}

	//获取短信验证码
	@ResponseBody
	@PostMapping("member/do/sendCode")
	public ResultEntity getCode(@RequestParam("phoneNum") String phoneNum){
		ResultEntity<String> resultEntity = memmberRemoteService.sendCode(phoneNum);
		if(ResultEntity.FAILED.equals(resultEntity.getResult())){
			throw new RuntimeException(resultEntity.getMessage());
		}
		resultEntity.setData("success");
		return resultEntity;

	}

	/**
	 * 实名认证-更新账户类型
	 * @param memberVO
	 * @param session
	 * @return
	 */
	@ResponseBody
	@PostMapping("/member/do/updateAcctType")
	public ResultEntity updateAcctType( MemberVO memberVO,HttpSession session){
		MemberSignSuccessVO  loginMember = (MemberSignSuccessVO)session.getAttribute(CrowdConstant.ATTR_NAME_LOGIN_MEMBER);
		// 获取登录会员信息
		memberVO.setId(loginMember.getId());
		//带上token
		memberVO.setMemberSignToken(loginMember.getToken());
		ResultEntity<String> resultEntity = memmberRemoteService.updateAcctType(memberVO);
		//远程方法当用失败则抛出异常
		if(ResultEntity.FAILED.equals(resultEntity.getResult())){
			return ResultEntity.failed(resultEntity.getMessage());
		}
		loginMember.setAccttype(memberVO.getAccttype());

		session.setAttribute(CrowdConstant.ATTR_NAME_LOGIN_MEMBER,loginMember);
		return ResultEntity.successNoData();

	}

	/**
	 * 实名认证-更新用户实名信息
	 * @param memberVO
	 * @param session
	 * @return
	 */
	@PostMapping("/member/do/updateBasicinfo")
	public String updateBasicinfo( MemberVO memberVO,Model model,HttpSession session){

		// 获取登录会员信息
		MemberSignSuccessVO  loginMember = (MemberSignSuccessVO)session.getAttribute(CrowdConstant.ATTR_NAME_LOGIN_MEMBER);
		memberVO.setId(loginMember.getId());
		memberVO.setAccttype(loginMember.getAccttype());
		//带上token
		memberVO.setMemberSignToken(loginMember.getToken());
		ResultEntity<String> resultEntity = memmberRemoteService.updateBasicinfo(memberVO);
		//远程方法当用失败则抛出异常
		if(ResultEntity.FAILED.equals(resultEntity.getResult())){
			model.addAttribute(CrowdConstant.ATTR_NAME_MESSAGE,resultEntity.getMessage());
			return "member/basicinfo";
		}
		//查询出下一步中需要上传的资质
		//根据当前用户查询账户类型,然后根据账户类型查找需要上传的资质
		ResultEntity<List<CertPO>> certResultEntity = dataBaseOperationRemoteService.queryCertByAccttype(memberVO.getAccttype());
		//远程方法当用失败则抛出异常
		if(ResultEntity.FAILED.equals(certResultEntity.getResult())){
			model.addAttribute(CrowdConstant.ATTR_NAME_MESSAGE,certResultEntity.getMessage());
			return "member/basicinfo";
		}
		List<CertPO> queryCertByAccttypeList = certResultEntity.getData();
		model.addAttribute("queryCertByAccttypeList",queryCertByAccttypeList);
		session.setAttribute("queryCertByAccttypeList",queryCertByAccttypeList);
		System.out.println(queryCertByAccttypeList.toString());
		return "member/uploadCert";

	}


	/**
	 * 实名认证-更新图片资质
	 * @param session
	 * @param ds
	 * @return
	 */
	@ResponseBody
	@PostMapping("/member/do/doUploadCert")
	public ResultEntity doUploadCert( HttpSession session, Data ds) {

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
//				String filename = realPath + "\\cert\\" + tmpName;
//
//				fileImg.transferTo(new File(filename));    //资质文件上传.
				//准备数据
				memberCert.setIconpath(tmpName); //封装数据,保存数据库
				memberCert.setMemberid(loginMember.getId());
			}
			for(MemberCert memberCert : certimgs){
				memberCert.setImgfile(null);
			}

			ResultEntity<String> resultEntity = memmberRemoteService.saveMemberCert(certimgs);
			//远程方法当用失败则抛出异常
			if (ResultEntity.FAILED.equals(resultEntity.getResult())) {
				return ResultEntity.failed(resultEntity.getMessage());
			}
			return ResultEntity.successNoData();
		}catch (Exception E){
			E.printStackTrace();
			return ResultEntity.failed(E.getMessage());
		}

	}


	/**
	 * 实名认证-邮箱发送验证码
	 * @return
	 */
	@ResponseBody
	@PostMapping("/member/do/sendEmailCode")
	public ResultEntity sendEmailCode( MemberVO memberVO) {
		ResultEntity<String> resultEntity = memmberRemoteService.sendEmailCode(memberVO);
		if(ResultEntity.FAILED.equals(resultEntity.getResult())){
			return ResultEntity.failed(resultEntity.getMessage());
		}
		return ResultEntity.successNoData();

	}

	/**
	 * 实名认证-验证发送验证码
	 * @return
	 */
	@ResponseBody
	@PostMapping(value = "/member/do/finishApply")
	public ResultEntity finishEmailApply( MemberVO memberVO,HttpSession session ) {
		ResultEntity<String> resultEntity = memmberRemoteService.finishEmailApply(memberVO);
		if(ResultEntity.FAILED.equals(resultEntity.getResult())){
			return ResultEntity.failed(resultEntity.getMessage());
		}
		//更新用户认证状态
		// 获取登录会员信息
		MemberSignSuccessVO loginMember = (MemberSignSuccessVO) session.getAttribute(CrowdConstant.ATTR_NAME_LOGIN_MEMBER);
		MemberVO memberVO1 = new MemberVO();
		memberVO1.setId(loginMember.getId());
		memberVO1.setAuthstatus("1");
		memberVO1.setMemberSignToken(loginMember.getToken());
		ResultEntity<String> stringResultEntity =  memmberRemoteService.updateBasicinfo(memberVO1);
		if(ResultEntity.FAILED.equals(stringResultEntity.getResult())){
			return ResultEntity.failed(stringResultEntity.getMessage());
		}
		loginMember.setAuthstatus("1");
		session.setAttribute(CrowdConstant.ATTR_NAME_LOGIN_MEMBER,loginMember);
		return ResultEntity.successNoData();

	}




	/**
	 * 删除用户订单
	 * @param orderid
	 * @return
	 */
	@PostMapping("/delete/order/by/orderid")
	@ResponseBody
	public ResultEntity<String> deleteOrderByorderid(Integer orderid){
		ResultEntity<String> resultEntity = memmberRemoteService.deleteOrderByorderid(orderid);
		if(ResultEntity.FAILED.equals(resultEntity.getResult())){
			return ResultEntity.failed(resultEntity.getMessage());
		}

		return ResultEntity.successNoData();
	}



}
