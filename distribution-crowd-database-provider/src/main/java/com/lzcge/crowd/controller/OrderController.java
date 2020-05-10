package com.lzcge.crowd.controller;


import com.lzcge.crowd.entity.MemberCert;
import com.lzcge.crowd.pojo.ResultEntity;
import com.lzcge.crowd.pojo.po.*;
import com.lzcge.crowd.pojo.vo.MemberVO;
import com.lzcge.crowd.pojo.vo.OrderVO;
import com.lzcge.crowd.service.MemberPOService;
import com.lzcge.crowd.service.OrderPOService;
import com.lzcge.crowd.util.CrowdConstant;
import com.lzcge.crowd.util.CrowdUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

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

	/**
	 * 根据订单状态查询订单
	 * @param statuMap
	 * @return
	 */
	@RequestMapping(value = "member/order/query/order/by/status")
	public ResultEntity<List<OrderPO>> queryOrderByStatus(@RequestParam Map<String,Object> statuMap){
		try {

			List<OrderPO> orderPOList = orderPOService.queryByStatus(statuMap);
			return ResultEntity.successWithData(orderPOList);
		} catch (Exception e) {
			e.printStackTrace();
			return ResultEntity.failed(e.getMessage());
		}

	}

	/**
	 * 更新订单
	 * @param orderVO
	 * @return
	 */
	@RequestMapping(value = "member/order/update/order")
	public ResultEntity<String> updateOrder(@RequestBody OrderVO orderVO){
		try {
			orderPOService.updateOrder(orderVO);
			return ResultEntity.successNoData();
		} catch (Exception e) {
			e.printStackTrace();
			return ResultEntity.failed(e.getMessage());
		}


	}


	/**
	 * 删除订单
	 * @param orderid
	 * @return
	 */
	@RequestMapping(value = "member/delete/order/by/orderid")
	public ResultEntity<String> deleteOrderByorderid(@RequestParam("orderid") Integer orderid){
		try {
			orderPOService.deleteOrderByorderid(orderid);
			return ResultEntity.successNoData();
		} catch (Exception e) {
			e.printStackTrace();
			return ResultEntity.failed(e.getMessage());
		}
	}

	/**
	 * 查询用户支持的项目
	 * @param orderVO
	 * @return
	 */
	@RequestMapping(value = "member/support/order")
	public ResultEntity<List<OrderDetailPO>> querySupportOrder(@RequestBody OrderVO orderVO){
		try {

			List<OrderDetailPO> resultEntity = orderPOService.querySupportOrder(orderVO);
			return ResultEntity.successWithData(resultEntity);
		} catch (Exception e) {
			e.printStackTrace();
			return ResultEntity.failed(e.getMessage());
		}

	}



}
