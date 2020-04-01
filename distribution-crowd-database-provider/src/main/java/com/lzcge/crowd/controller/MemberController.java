package com.lzcge.crowd.controller;


import com.lzcge.crowd.entity.MemberCert;
import com.lzcge.crowd.pojo.ResultEntity;
import com.lzcge.crowd.pojo.po.MemberLaunchInfoPO;
import com.lzcge.crowd.pojo.po.MemberPO;
import com.lzcge.crowd.service.MemberPOService;
import com.lzcge.crowd.util.CrowdConstant;
import com.lzcge.crowd.util.CrowdUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController			//服务端返回的都是json数据
public class MemberController {

	@Autowired
	private MemberPOService memberPOService;

	//根据memberid获取项目发起人信息
	@RequestMapping("get/member/launch/info/po")
	public ResultEntity<MemberLaunchInfoPO> getLaunchInfoByMemberId(@RequestParam("memberid") String memberid){
		MemberLaunchInfoPO memberLaunchInfoPO = memberPOService.getLaunchInfoByMemberId(memberid);
		return ResultEntity.successWithData(memberLaunchInfoPO);
	}

	/**
	 * 查找登录账号数量
	 * @param loginacct
	 * @return
	 */
	@RequestMapping(value = "/retrieve/login/acct/count",method = RequestMethod.POST)
	public ResultEntity<Integer> retrieveLoginAcctCount(@RequestParam("loginacct") String loginacct){
		if(!CrowdUtils.strEffectiveCheck(loginacct)){
			return ResultEntity.failed(CrowdConstant.MESSAGE_LOGINACCT_INVALID);
		}
		try {
			int count = memberPOService.getLoginAcct(loginacct);
			return ResultEntity.successWithData(count);
		} catch (Exception e) {
			e.printStackTrace();
			return ResultEntity.failed(e.getMessage());
		}
	}

	/**
	 * 查找登录手机号数量
	 * @param phoneNum
	 * @return
	 */
	@RequestMapping(value = "/retrieve/login/phoneNum/count",method = RequestMethod.POST)
	public ResultEntity<Integer> retrieveLoginPhoneNumCount(@RequestParam("phoneNum") String phoneNum){
		if(!CrowdUtils.strEffectiveCheck(phoneNum)){
			return ResultEntity.failed(CrowdConstant.MESSAGE_PHONE_NUM_INVALID);
		}
		try {
			int count = memberPOService.getPhoneNumAcct(phoneNum);
			return ResultEntity.successWithData(count);
		} catch (Exception e) {
			e.printStackTrace();
			return ResultEntity.failed(e.getMessage());
		}
	}

	/**
	 * 查找登录邮箱数量
	 * @param email
	 * @return
	 */
	@RequestMapping(value = "/retrieve/login/email/count",method = RequestMethod.POST)
	public ResultEntity<Integer> retrieveLoginEmailCount(@RequestParam("email") String email){
		if(!CrowdUtils.strEffectiveCheck(email)){
			return ResultEntity.failed(CrowdConstant.MESSAGE_EMAIL_INVALID);
		}
		try {
			int count = memberPOService.getEmailCount(email);
			return ResultEntity.successWithData(count);
		} catch (Exception e) {
			e.printStackTrace();
			return ResultEntity.failed(e.getMessage());
		}
	}

	@RequestMapping(value = "/save/member/remote",method = RequestMethod.POST)
	public ResultEntity<String> saveMemberRemote(@RequestBody MemberPO memberPO){
		try {
			memberPOService.saveMemberPO(memberPO);
			return ResultEntity.successNoData();
		} catch (Exception e) {
			e.printStackTrace();
			return ResultEntity.failed(e.getMessage());
		}
	}

	/**
	 * 更新用户类型信息
	 * @param memberPO
	 * @return
	 */
	@RequestMapping(value = "/update/member/accttype",method = RequestMethod.POST)
	ResultEntity<String> updateAcctType(@RequestBody MemberPO memberPO){
		try {
			memberPOService.updateAcctType(memberPO);
			return ResultEntity.successNoData();
		}catch (Exception e){
			return ResultEntity.failed(e.getMessage());
		}

	}

	/**
	 * 更新用户实名信息
	 * @RequestBody：将传入的json字符串转换为对象
	 * @param memberPO
	 * @return
	 */
	@RequestMapping(value = "/update/member/basicinfo",method = RequestMethod.POST)
	ResultEntity<String> updateBasicinfo(@RequestBody MemberPO memberPO){
		try {
			memberPOService.updateBasicinfo(memberPO);
			return ResultEntity.successNoData();
		}catch (Exception e){
			return ResultEntity.failed(e.getMessage());
		}
	}

	/**
	 *  删除用户上传的资质图片
	 * @param memberCert
	 * @return
	 */
	@RequestMapping(value = "/delete/member/cert",method = RequestMethod.POST)
	ResultEntity<String> delteMemberCert(@RequestBody MemberCert memberCert){
		try{
			memberPOService.delteMemberCert(memberCert);
			return ResultEntity.successNoData();
		}catch (Exception e){
			e.printStackTrace();
			return ResultEntity.failed(e.getMessage());
		}
	}


	/**
	 * 新增用户上传的资质图片
	 * @param memberCert
	 * @return
	 */
	@RequestMapping(value = "/insert/member/cert",method = RequestMethod.POST)
	ResultEntity<String> insertMemberCert(@RequestBody MemberCert memberCert){
		try{
			memberPOService.insertMemberCert(memberCert);
			return ResultEntity.successNoData();
		}catch (Exception e){
			e.printStackTrace();
			return ResultEntity.failed(e.getMessage());
		}
	}



	@RequestMapping(value = "/retrieve/member/by/login/acct",method = RequestMethod.POST)
	public ResultEntity<MemberPO> retrieveMemberByLoginAcct(@RequestParam("loginacct") String loginacct){
		try {
			MemberPO memberPO = memberPOService.getMemberByAcct(loginacct);
			return ResultEntity.successWithData(memberPO);
		} catch (Exception e) {
			e.printStackTrace();
			return ResultEntity.failed(e.getMessage());
		}
	}
}
