package com.lzcge.crowd.api;

import com.lzcge.crowd.entity.Cert;
import com.lzcge.crowd.entity.MemberCert;
import com.lzcge.crowd.pojo.ResultEntity;
import com.lzcge.crowd.pojo.po.*;
import com.lzcge.crowd.pojo.vo.MemberVO;
import com.lzcge.crowd.pojo.vo.OrderVO;
import com.lzcge.crowd.pojo.vo.ProjectVO;
import com.lzcge.crowd.util.Page;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;


//定义feign客户端 ;
@FeignClient("database-provider")	//调用注册到eureka中的微服务名称
public interface DataBaseOperationRemoteService {

	//根据memberid获取项目发起人信息
	@RequestMapping("get/member/launch/info/po")
	ResultEntity<MemberLaunchInfoPO> getLaunchInfoByMemberId(@RequestParam("memberid") String memberid);

	/**
	 * 查询账户个数
	 * @return
	 */
	@RequestMapping(value = "/retrieve/login/acct/count" ,method = RequestMethod.POST)
	ResultEntity<Integer> retrieveLoginAcctCount(@RequestParam("loginacct") String loginacct);

	/**
	 * 查询手机号个数
	 * @return
	 */
	@RequestMapping(value = "/retrieve/login/phoneNum/count",method = RequestMethod.POST)
	ResultEntity<Integer> retrieveLoginPhoneNumCount(@RequestParam("phoneNum") String phoneNum);


	/**
	 * 查询手机号个数
	 * @return
	 */
	@RequestMapping(value = "/retrieve/login/email/count",method = RequestMethod.POST)
	ResultEntity<Integer> retrieveLoginEmailCount(@RequestParam("email") String email);

	/**
	 * 保存用户信息
	 * @RequestBody：将传入的json字符串转换为对象
	 * @param memberPO
	 * @return
	 */
	@RequestMapping(value = "/save/member/remote",method = RequestMethod.POST)
	ResultEntity<String> saveMemberRemote(@RequestBody MemberPO memberPO);

	/**
	 * 更新用户类型信息
	 * @RequestBody：将传入的json字符串转换为对象
	 * @param memberPO
	 * @return
	 */
	@RequestMapping(value = "/update/member/accttype",method = RequestMethod.POST)
	ResultEntity<String> updateAcctType(@RequestBody MemberPO memberPO);


	/**
	 * 更新用户实名信息
	 * @RequestBody：将传入的json字符串转换为对象
	 * @param memberPO
	 * @return
	 */
	@RequestMapping(value = "/update/member/basicinfo",method = RequestMethod.POST)
	ResultEntity<String> updateBasicinfo(@RequestBody MemberPO memberPO);

	/**
	 * 获取用户类型需要上传的资质
	 * @param accttype
	 * @return
	 */
	@RequestMapping(value = "/retrieve/member/cert",method = RequestMethod.POST)
	ResultEntity<List<CertPO>> queryCertByAccttype(@RequestParam("accttype") String accttype);

	/**
	 *  删除用户上传的资质图片
	 * @param memberCert
	 * @return
	 */
	@RequestMapping(value = "/delete/member/cert",method = RequestMethod.POST)
	ResultEntity<String> delteMemberCert(@RequestBody MemberCert memberCert);

	/**
	 * 新增用户上传的资质图片
	 * @param memberCert
	 * @return
	 */
	@RequestMapping(value = "/insert/member/cert",method = RequestMethod.POST)
	ResultEntity<String> insertMemberCert(@RequestBody MemberCert memberCert);


	/**
	 * 登录
	 * @return
	 */
	@RequestMapping(value = "/retrieve/member/by/login/acct",method = RequestMethod.POST)
	ResultEntity<MemberPO> retrieveMemberByLoginAcct(@RequestParam("loginacct") String loginacct);

	/**
	 * 保存项目信息
	 * @param projectVO
	 * @param memberId
	 * @return
	 */
	@RequestMapping("save/project/remote/{memberId}")
	ResultEntity<String> saveProjectRemote(@RequestBody ProjectVO projectVO, @PathVariable("memberId") String memberId);


	/**
	 * 分页查询项目信息
	 * @param projectMap
	 * @return
	 */
	@RequestMapping("retrieve/project/querypage")
	ResultEntity<Page<ProjectPO>> pageQueryProject(@RequestParam Map<String, Object> projectMap);

	/**
	 * 查询项目详细信息
	 * @param id
	 * @return
	 */
	@RequestMapping("project/manager/query/projectdetail")
	public ResultEntity<ProjectDetailPO> queryProjectDetail(@RequestParam("id") String id);


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
	 * 更新项目信息
	 * @param projectVO
	 * @return
	 */
	@RequestMapping("project/manager/update/projectdetail")
	public ResultEntity<ProjectDetailPO> updateProject(@RequestBody ProjectVO projectVO);


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