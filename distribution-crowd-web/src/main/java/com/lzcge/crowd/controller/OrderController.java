package com.lzcge.crowd.controller;

import com.lzcge.crowd.api.MemberManagerRemoteService;
import com.lzcge.crowd.api.ProjectOperationRemoteService;
import com.lzcge.crowd.pojo.ResultEntity;
import com.lzcge.crowd.pojo.po.MemberAddressPO;
import com.lzcge.crowd.pojo.vo.MemberSignSuccessVO;
import com.lzcge.crowd.pojo.vo.OrderVO;
import com.lzcge.crowd.util.CrowdConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		Date createDate = new Date();
		orderVO.setCreatedate(format.format(createDate));
		ResultEntity<Integer> resultEntity = memmberRemoteService.saveOrder(orderVO);
		if(ResultEntity.FAILED.equals(resultEntity.getResult())){
			return ResultEntity.failed(resultEntity.getMessage());
		}

		return ResultEntity.successWithData(resultEntity.getData());
	}
}