package com.lzcge.crowd.api;


import com.lzcge.crowd.entity.MemberCert;
import com.lzcge.crowd.pojo.ResultEntity;
import com.lzcge.crowd.pojo.po.MemberLaunchInfoPO;
import com.lzcge.crowd.pojo.vo.MemberSignSuccessVO;
import com.lzcge.crowd.pojo.vo.MemberVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient("member-manager")	//调用注册到eureka中的微服务名称
public interface MemberManagerRemoteService {

	@RequestMapping("get/Member/Launch/InfoPO/By/Token")
	public ResultEntity<MemberLaunchInfoPO> getMemberLaunchInfoPOByToken(@RequestParam("token") String token);

	@RequestMapping(value = "member/manager/register",method = RequestMethod.POST)
	public ResultEntity<String> register(@RequestBody() MemberVO memberVO);

	@RequestMapping(value = "member/manager/login" ,method = RequestMethod.POST)
	public ResultEntity<MemberSignSuccessVO> login(@RequestBody MemberVO memberVO	);

	@RequestMapping(value = "member/manager/updateAcctType" ,method = RequestMethod.POST)
	public ResultEntity<String> updateAcctType(@RequestBody MemberVO memberVO	);

	@RequestMapping(value = "member/manager/updateBasicinfo" ,method = RequestMethod.POST)
	public ResultEntity<String> updateBasicinfo(@RequestBody MemberVO memberVO	);

	@RequestMapping(value = "member/manager/saveMemberCert" ,method = RequestMethod.POST)
	public ResultEntity<String> saveMemberCert(@RequestBody List<MemberCert> certimgs	);

	@RequestMapping("member/manager/logout")
	public ResultEntity<String> logout(@RequestParam("token") String token);

	/**
	 *发送手机验证码
	 * @param phoneNum
	 * @return
	 */
	@RequestMapping("member/manager/send/code")
	public ResultEntity<String> sendCode(@RequestParam("phoneNum") String phoneNum);


	/**
	 * 发送邮箱验证码
	 * @param memberVO
	 * @return
	 */
	@RequestMapping(value = "member/manager/send/emailcode",method = RequestMethod.POST)
	public ResultEntity<String> sendEmailCode(@RequestBody MemberVO memberVO);

	/**
	 * 验证邮箱验证码
	 * @param memberVO
	 * @return
	 */
	@RequestMapping(value = "member/manager/finish/emailcode",method = RequestMethod.POST)
	public ResultEntity<String> finishEmailApply(@RequestBody MemberVO memberVO);

}
