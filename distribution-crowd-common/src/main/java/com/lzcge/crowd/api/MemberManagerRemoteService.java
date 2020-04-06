package com.lzcge.crowd.api;


import com.lzcge.crowd.entity.MemberCert;
import com.lzcge.crowd.pojo.ResultEntity;
import com.lzcge.crowd.pojo.po.MemberAddressPO;
import com.lzcge.crowd.pojo.po.MemberLaunchInfoPO;
import com.lzcge.crowd.pojo.po.OrderDetailPO;
import com.lzcge.crowd.pojo.po.OrderPO;
import com.lzcge.crowd.pojo.vo.MemberSignSuccessVO;
import com.lzcge.crowd.pojo.vo.MemberVO;
import com.lzcge.crowd.pojo.vo.OrderVO;
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

	/**
	 * 查询用户收获地址
	 * @param memberAddressPO
	 * @return
	 */
	@RequestMapping(value = "member/order/get/address")
	public ResultEntity<List<MemberAddressPO>> queryAddress(@RequestBody MemberAddressPO memberAddressPO);


	/**
	 * 查询用户收获地址根据收获地址
	 * @param memberAddressPO
	 * @return
	 */
	@RequestMapping(value = "member/order/get/address/by/address")
	public ResultEntity<List<MemberAddressPO>> selectMemberAddressByadress(@RequestBody MemberAddressPO memberAddressPO);



	/**
	 * 新增用户收获地址
	 * @param memberAddressPO
	 * @return
	 */
	@RequestMapping(value = "member/order/add/address")
	public ResultEntity<List<MemberAddressPO>> addMemberAddress(@RequestBody MemberAddressPO memberAddressPO);

	/**
	 * 保存订单
	 * @param orderVO
	 * @return
	 */
	@RequestMapping(value = "member/order/save/order")
	public ResultEntity<Integer> saveOrder(@RequestBody OrderVO orderVO);

	/**
	 * 根据订单id获取订单
	 * @param orderid
	 * @return
	 */
	@RequestMapping(value = "member/order/query/order/by/orderid")
	public ResultEntity<OrderPO> queryOrderById(@RequestParam("orderid") Integer orderid);

	/**
	 * 更新订单
	 * @param orderVO
	 * @return
	 */
	@RequestMapping(value = "member/order/update/order")
	public ResultEntity<String> updateOrder(@RequestBody OrderVO orderVO);


	/**
	 * 查询用户支持的项目
	 * @param orderVO
	 * @return
	 */
	@RequestMapping(value = "member/support/order")
	public ResultEntity<List<OrderDetailPO>> querySupportOrder(@RequestBody OrderVO orderVO);


	/**
	 * 删除订单
	 * @param orderid
	 * @return
	 */
	@RequestMapping(value = "member/delete/order/by/orderid")
	public ResultEntity<String> deleteOrderByorderid(@RequestParam("orderid") Integer orderid);






}
