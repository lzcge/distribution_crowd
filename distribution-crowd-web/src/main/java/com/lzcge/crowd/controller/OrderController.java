package com.lzcge.crowd.controller;

import com.alibaba.fastjson.JSON;
import com.google.gson.annotations.JsonAdapter;
import com.lzcge.crowd.api.MemberManagerRemoteService;
import com.lzcge.crowd.api.ProjectOperationRemoteService;
import com.lzcge.crowd.api.RedisOperationRemoteService;
import com.lzcge.crowd.pojo.ResultEntity;
import com.lzcge.crowd.pojo.po.MemberAddressPO;
import com.lzcge.crowd.pojo.po.OrderDetailPO;
import com.lzcge.crowd.pojo.po.ProjectDetailPO;
import com.lzcge.crowd.pojo.vo.MemberSignSuccessVO;
import com.lzcge.crowd.pojo.vo.MemberVO;
import com.lzcge.crowd.pojo.vo.OrderVO;
import com.lzcge.crowd.pojo.vo.ProjectVO;
import com.lzcge.crowd.util.CrowdConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * @description:
 * @author: lzcge
 * @create: 2020-04-04
 **/

@Controller
public class OrderController {

	@Autowired
	private MemberManagerRemoteService memmberRemoteService;

	@Autowired
	private ProjectOperationRemoteService projectRemoteService;

	@Autowired
	private RedisOperationRemoteService redisService;


	/**
	 * 微信端
	 *确认订单界面，查询收获地址信息
	 * @param memberAddressPO
	 * @return
	 */
	@RequestMapping("/wx/pay/do/pay-ConfirmOrder")
	@ResponseBody
	public ResultEntity WXconfirmOrder(MemberAddressPO memberAddressPO){
		//获取当前用户收获地址
		ResultEntity<List<MemberAddressPO>> resultEntity =  memmberRemoteService.queryAddress(memberAddressPO);
		if(ResultEntity.FAILED.equals(resultEntity.getResult())){
			return ResultEntity.failed(resultEntity.getMessage());
		}
		return ResultEntity.successWithData(resultEntity.getData());
	}


	/**
	 *确认订单界面，查询收获地址信息
	 * @param model
	 * @param session
	 * @return
	 */
	@RequestMapping("/pay/do/pay-ConfirmOrder.html")
	public String confirmOrder(Model model, HttpSession session){
		//获取当前登录的用户
		MemberSignSuccessVO signSuccessVO = (MemberSignSuccessVO) session.getAttribute(CrowdConstant.ATTR_NAME_LOGIN_MEMBER);
		//检查是否登录
		if (signSuccessVO == null) {
			model.addAttribute(CrowdConstant.ATTR_NAME_MESSAGE,CrowdConstant.MESSAGE_ACCESS_DENIED);
			return "member/login";
		}

		MemberAddressPO memberAddressPO = new MemberAddressPO();
		memberAddressPO.setMemberid(signSuccessVO.getId());
		memberAddressPO.setMemberSignToken(signSuccessVO.getToken());
		//获取当前用户收获地址
		ResultEntity<List<MemberAddressPO>> resultEntity =  memmberRemoteService.queryAddress(memberAddressPO);
		if(ResultEntity.FAILED.equals(resultEntity.getResult())){
			model.addAttribute(CrowdConstant.ATTR_NAME_MESSAGE,resultEntity.getMessage());
			return "pay/pay-ConfirmReturn";
		}

		model.addAttribute("memberAddressPOList",resultEntity.getData());
		return "pay/pay-ConfirmOrder";
	}

	/**
	 *添加新地址,并返回
	 * @param model
	 * @param session
	 * @return
	 */
	@RequestMapping(value="/pay/do/pay-ConfirmOrder/confirmOrderAddAddress")
	public String confirmOrderAddAddress(String username,String phoneNum,String address,
										 Model model, HttpSession session){
		//获取当前登录的用户
		MemberSignSuccessVO signSuccessVO = (MemberSignSuccessVO) session.getAttribute(CrowdConstant.ATTR_NAME_LOGIN_MEMBER);
		//检查是否登录
		if (signSuccessVO == null) {
			model.addAttribute(CrowdConstant.ATTR_NAME_MESSAGE,CrowdConstant.MESSAGE_ACCESS_DENIED);
			return "member/login";
		}
		MemberAddressPO memberAddressPO = new MemberAddressPO();
		memberAddressPO.setUsername(username);
		memberAddressPO.setMemberid(signSuccessVO.getId());
		memberAddressPO.setAddress(address);
		memberAddressPO.setPhoneNum(phoneNum);
		memberAddressPO.setMemberSignToken(signSuccessVO.getToken());
		//查看地址是否已经存在
		ResultEntity<List<MemberAddressPO>> resultEntity2 = memmberRemoteService.selectMemberAddressByadress(memberAddressPO);
		if(ResultEntity.FAILED.equals(resultEntity2.getResult())){
			model.addAttribute(CrowdConstant.ATTR_NAME_MESSAGE,resultEntity2.getMessage());
			return "pay/pay-ConfirmOrder";
		}
		if (resultEntity2.getData().size()>0){
			model.addAttribute(CrowdConstant.MESSAGE_ADDRESS_FAILED);
			return "pay/pay-ConfirmOrder";
		}
		//添加收获地址
		ResultEntity<List<MemberAddressPO>> resultEntity =  memmberRemoteService.addMemberAddress(memberAddressPO);
		if(ResultEntity.FAILED.equals(resultEntity.getResult())){
			model.addAttribute(CrowdConstant.ATTR_NAME_MESSAGE,resultEntity.getMessage());
			return "pay/pay-ConfirmOrder";
		}
		model.addAttribute("memberAddressPOList",resultEntity.getData());
		return "redirect:/pay/do/pay-ConfirmOrder.html";
	}


	/**
	 *微信端 添加新地址,并返回
	 * @param
	 * @return
	 */
	@RequestMapping(value="/wx/pay/do/pay-ConfirmOrder/confirmOrderAddAddress")
	@ResponseBody
	public ResultEntity WXconfirmOrderAddAddress(String username,String phoneNum,String address,Integer memberid,String token
										 ){
		MemberAddressPO memberAddressPO = new MemberAddressPO();
		memberAddressPO.setUsername(username);
		memberAddressPO.setMemberid(memberid);
		memberAddressPO.setAddress(address);
		memberAddressPO.setPhoneNum(phoneNum);
		memberAddressPO.setMemberSignToken(token);
		//查看地址是否已经存在
		ResultEntity<List<MemberAddressPO>> resultEntity2 = memmberRemoteService.selectMemberAddressByadress(memberAddressPO);
		if(ResultEntity.FAILED.equals(resultEntity2.getResult())){
			return ResultEntity.failed(resultEntity2.getMessage());
		}
		if (resultEntity2.getData().size()>0){
			return ResultEntity.failed(CrowdConstant.MESSAGE_ADDRESS_FAILED);
		}
		//添加收获地址
		ResultEntity<List<MemberAddressPO>> resultEntity =  memmberRemoteService.addMemberAddress(memberAddressPO);
		if(ResultEntity.FAILED.equals(resultEntity.getResult())){
			return ResultEntity.failed(resultEntity.getMessage());
		}
		return ResultEntity.successNoData();
	}



	//生成订单信息
	@RequestMapping("/order/do/createorder")
	@ResponseBody
	public ResultEntity saveOrder(OrderVO orderVO,HttpSession session){
		//获取当前登录的用户
		MemberSignSuccessVO signSuccessVO = (MemberSignSuccessVO) session.getAttribute(CrowdConstant.ATTR_NAME_LOGIN_MEMBER);
		//检查是否登录
		if (signSuccessVO == null) {
			return ResultEntity.failed(CrowdConstant.MESSAGE_ACCESS_DENIED);
		}
		//设置创建时间
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date createDate = new Date();
		orderVO.setCreatedate(simpleDateFormat.format(createDate));
		ResultEntity<Integer> resultEntity = memmberRemoteService.saveOrder(orderVO);
		if(ResultEntity.FAILED.equals(resultEntity.getResult())){
			return ResultEntity.failed(resultEntity.getMessage());
		}

		//放入redis缓存，后续取出更新订单状态
		//时间30分钟，比订单过期时间15分钟多15分钟
		//将保存好回报信息的项目对象序列化存入redis,订单号作为key
		orderVO.setId(resultEntity.getData());
		String orderJson = JSON.toJSONString(orderVO);
		ResultEntity<String> stringResultEntity = redisService.saveNormalStringKeyValue(orderVO.getOrdernum(),orderJson,CrowdConstant.REDIS_ORDER_WAIT_PAY_TIME);
		if(ResultEntity.FAILED.equals(stringResultEntity.getResult())){
			return ResultEntity.failed(stringResultEntity.getMessage());
		}


		return ResultEntity.successWithData(resultEntity.getData());
	}


	//微信---生成订单信息并完成支付更新工程订单信息
	@RequestMapping("/wx/order/do/createorder")
	@ResponseBody
	public ResultEntity WXsaveOrder(OrderVO orderVO,HttpSession session){
		//设置创建时间
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date createDate = new Date();
		orderVO.setCreatedate(simpleDateFormat.format(createDate));
		ResultEntity<Integer> resultEntity = memmberRemoteService.saveOrder(orderVO);
		if(ResultEntity.FAILED.equals(resultEntity.getResult())){
			return ResultEntity.failed(resultEntity.getMessage());
		}

		//放入redis缓存，后续取出更新订单状态
		//时间30分钟，比订单过期时间15分钟多15分钟
		//将保存好回报信息的项目对象序列化存入redis,订单号作为key
		orderVO.setId(resultEntity.getData());
		String orderJson = JSON.toJSONString(orderVO);
		ResultEntity<String> stringResultEntity = redisService.saveNormalStringKeyValue(orderVO.getOrdernum(),orderJson,-1);
		if(ResultEntity.FAILED.equals(stringResultEntity.getResult())){
			return ResultEntity.failed(stringResultEntity.getMessage());
		}


		//下单付款成功后
		//更新工程信息
		ResultEntity<ProjectDetailPO> ProjectDetailPOresultEntity = projectRemoteService.queryProjectDetail(orderVO.getProjectid().toString());
		if (ResultEntity.FAILED.equals(ProjectDetailPOresultEntity.getResult())){
			return ResultEntity.failed(ProjectDetailPOresultEntity.getMessage());
		}
		ProjectDetailPO projectDetailPO = ProjectDetailPOresultEntity.getData();
		//设置更新信息
		ProjectVO projectVO = new ProjectVO();
		projectVO.setId(projectDetailPO.getProjectPO().getId());
		//判断是否众筹成功
		//众筹已结束
		if(projectDetailPO.getProjectPO().getSupportmoney()>=projectDetailPO.getProjectPO().getMoney()){
			projectVO.setStatus(CrowdConstant.PROJECT_STATUS_SUCCESS_CROWD);
		}else{    //没结束
			//刚好结束
			//去掉小数点
			Long money = Long.valueOf(orderVO.getMoney().toString().split("\\.")[0]);
			if(projectDetailPO.getProjectPO().getSupportmoney()+money>=projectDetailPO.getProjectPO().getMoney()){
				projectVO.setStatus(CrowdConstant.PROJECT_STATUS_SUCCESS_CROWD);
			}
			projectVO.setSupporter(projectDetailPO.getProjectPO().getSupporter()+1);
			projectVO.setSupportmoney(projectDetailPO.getProjectPO().getSupportmoney()+money);
		}
		//更新工程信息
		ResultEntity<ProjectDetailPO> resultEntity2 = projectRemoteService.updateProject(projectVO);
		if(ResultEntity.FAILED.equals(resultEntity2.getResult())){
			return ResultEntity.failed(resultEntity2.getMessage());
		}


		//更新订单状态
		//redis中取出订单信息
		ResultEntity<String> resultEntity3 = redisService.retrieveStringValueByStringKey(orderVO.getOrdernum());
		if(ResultEntity.FAILED.equals(resultEntity3.getResult())){
			return ResultEntity.failed(resultEntity2.getMessage());
		}
		if(resultEntity3.getData()==null){
			return ResultEntity.failed(CrowdConstant.MESSAGE_ORDER_NOT_EXISTS);
		}
		//从Redis中查询到对象字符串
		String orderVOString = resultEntity3.getData();
		//json转对象
		OrderVO orderVO2 = JSON.parseObject(orderVOString, OrderVO.class);
		//更新订单状态
		orderVO2.setStatus(CrowdConstant.ORDER_STATUS_SUCCESS_PAY.toString());
		ResultEntity<String> resultEntity4 = memmberRemoteService.updateOrder(orderVO2);
		if(ResultEntity.FAILED.equals(resultEntity4.getResult())){
			return ResultEntity.failed(CrowdConstant.MESSAGE_ORDER_UPDATE_FAILED);
		}
		//redis中取消该订单
		redisService.removeByKey(orderVO.getOrdernum());
		session.setAttribute("ProjectDetailPO",resultEntity2.getData());
		return ResultEntity.successNoData();
	}



	/**
	 * 微信端重新付款成功后更新订单和项目信息
	 * @param orderVO
	 * @param session
	 * @return
	 */
	@RequestMapping("/wx/order/do/updatePay")
	@ResponseBody
	public ResultEntity wxUpdatePay(OrderVO orderVO,HttpSession session){
		//下单付款成功后
		//更新工程信息
		ResultEntity<ProjectDetailPO> ProjectDetailPOresultEntity = projectRemoteService.queryProjectDetail(orderVO.getProjectid().toString());
		if (ResultEntity.FAILED.equals(ProjectDetailPOresultEntity.getResult())){
			return ResultEntity.failed(ProjectDetailPOresultEntity.getMessage());
		}
		ProjectDetailPO projectDetailPO = ProjectDetailPOresultEntity.getData();
		//设置更新信息

		ProjectVO projectVO = new ProjectVO();

		projectVO.setId(projectDetailPO.getProjectPO().getId());
		//判断是否众筹成功
		//众筹已结束
		if(projectDetailPO.getProjectPO().getSupportmoney()>=projectDetailPO.getProjectPO().getMoney()){
			projectVO.setStatus(CrowdConstant.PROJECT_STATUS_SUCCESS_CROWD);
		}else{    //没结束
			//刚好结束
			//去掉小数点
			Long money = Long.valueOf(orderVO.getMoney().toString().split("\\.")[0]);
			if(projectDetailPO.getProjectPO().getSupportmoney()+money>=projectDetailPO.getProjectPO().getMoney()){
				projectVO.setStatus(CrowdConstant.PROJECT_STATUS_SUCCESS_CROWD);
			}
			projectVO.setSupporter(projectDetailPO.getProjectPO().getSupporter()+1);
			projectVO.setSupportmoney(projectDetailPO.getProjectPO().getSupportmoney()+money);
		}

		//更新工程信息
		ResultEntity<ProjectDetailPO> resultEntity2 = projectRemoteService.updateProject(projectVO);
		if(ResultEntity.FAILED.equals(resultEntity2.getResult())){
			return ResultEntity.failed(resultEntity2.getMessage());
		}


		//更新订单状态
		//redis中取出订单信息
		ResultEntity<String> resultEntity3 = redisService.retrieveStringValueByStringKey(orderVO.getOrdernum());
		if(ResultEntity.FAILED.equals(resultEntity3.getResult())){
			return ResultEntity.failed(resultEntity2.getMessage());
		}
		if(resultEntity3.getData()==null){
			return ResultEntity.failed(CrowdConstant.MESSAGE_ORDER_NOT_EXISTS);
		}
		//从Redis中查询到对象字符串
		String orderVOString = resultEntity3.getData();
		//json转对象
		OrderVO orderVO2 = JSON.parseObject(orderVOString, OrderVO.class);
		//更新订单状态
		orderVO2.setStatus(CrowdConstant.ORDER_STATUS_SUCCESS_PAY.toString());
		ResultEntity<String> resultEntity4 = memmberRemoteService.updateOrder(orderVO2);
		if(ResultEntity.FAILED.equals(resultEntity4.getResult())){
			return ResultEntity.failed(CrowdConstant.MESSAGE_ORDER_UPDATE_FAILED);
		}
		//redis中取消该订单
		redisService.removeByKey(orderVO.getOrdernum());

		session.setAttribute("ProjectDetailPO",resultEntity2.getData());

		return ResultEntity.successNoData();


	}


	/**
	 * 查询用户支持的众筹项目
	 * @param orderVO
	 * @return
	 */
	@PostMapping("/query/orderSupport")
	@ResponseBody
	public ResultEntity<List<OrderDetailPO>> querySupportOrder(OrderVO orderVO){
		ResultEntity<List<OrderDetailPO>> ordersupportRestult = memmberRemoteService.querySupportOrder(orderVO);
		if(ResultEntity.FAILED.equals(ordersupportRestult.getResult())){
			return ResultEntity.failed(ordersupportRestult.getMessage());
		}

		return ResultEntity.successWithData(ordersupportRestult.getData());
	}


}
