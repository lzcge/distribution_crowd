package com.lzcge.crowd.controller;


import com.lzcge.crowd.entity.MemberCert;
import com.lzcge.crowd.pojo.ResultEntity;
import com.lzcge.crowd.pojo.po.MemberAddressPO;
import com.lzcge.crowd.pojo.po.MemberLaunchInfoPO;
import com.lzcge.crowd.pojo.po.MemberPO;
import com.lzcge.crowd.pojo.po.OrderPO;
import com.lzcge.crowd.pojo.vo.OrderVO;
import com.lzcge.crowd.service.MemberPOService;
import com.lzcge.crowd.service.OrderPOService;
import com.lzcge.crowd.util.CrowdConstant;
import com.lzcge.crowd.util.CrowdUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController			//服务端返回的都是json数据
public class OrderController {

	@Autowired
	private OrderPOService orderPOService;


	/**
	 * 保存订单
	 * @param orderVO
	 * @return
	 */
	@RequestMapping(value = "member/order/save/order")
	public ResultEntity<Integer> saveOrder(@RequestBody OrderVO orderVO){

		try {
			Integer id = orderPOService.saveOrder(orderVO);
			return ResultEntity.successWithData(id);
		} catch (Exception e) {
			e.printStackTrace();
			return ResultEntity.failed(e.getMessage());
		}

	}

	/**
	 * 根据订单id获取订单
	 * @param orderid
	 * @return
	 */
	@RequestMapping(value = "member/order/query/order/by/orderid")
	public ResultEntity<OrderPO> queryOrderById(@RequestParam("orderid") Integer orderid){
		try {
			OrderPO orderPO = orderPOService.queryOrderById(orderid);
			if(orderPO==null){
				return ResultEntity.failed(CrowdConstant.MESSAGE_ORDER_FAILED);
			}else{
				return ResultEntity.successWithData(orderPO);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return ResultEntity.failed(e.getMessage());
		}

	}

}
