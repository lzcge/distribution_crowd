package com.lzcge.crowd.service.impl;

import com.lzcge.crowd.entity.MemberCert;
import com.lzcge.crowd.mapper.*;
import com.lzcge.crowd.pojo.po.*;
import com.lzcge.crowd.pojo.vo.MemberVO;
import com.lzcge.crowd.pojo.vo.OrderVO;
import com.lzcge.crowd.service.MemberPOService;
import com.lzcge.crowd.service.OrderPOService;
import com.lzcge.crowd.util.CrowdUtils;
import org.aspectj.weaver.ast.Or;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class OrderPOServiceImpl implements OrderPOService {


	@Autowired
	private OrderPOMapper orderPOMapper;

	@Autowired
	ProjectPOMapper projectPOMapper;

	@Autowired
	ReturnPOMapper returnPOMapper;


	@Override
	@Transactional
	public Integer saveOrder(OrderVO orderVO) {
		orderPOMapper.saveOrder(orderVO);
		Integer id = orderVO.getId();
		return id;
	}

	@Override
	public OrderPO queryOrderById(Integer orderid) {
		return orderPOMapper.queryOrderById(orderid);
	}

	@Override
	@Transactional
	public void updateOrder(OrderVO orderVO) {
		//1.保存OrderPO
		OrderPO orderPO = new OrderPO();
		BeanUtils.copyProperties(orderVO,orderPO);
		orderPOMapper.updateOrder(orderPO);
	}


	@Override
	@Transactional
	public void deleteOrderByorderid(Integer orderid) {
		orderPOMapper.deleteByPrimaryKey(orderid);
	}


	@Override
	public List<OrderDetailPO> querySupportOrder(OrderVO orderVO) {
		//1.保存OrderPO
		OrderPO orderPO = new OrderPO();
		BeanUtils.copyProperties(orderVO,orderPO);
		List<OrderPO> orderPOList = new ArrayList<>();
		//查询所有支持订单
		if(orderPO.getStatus().equals("-1")){
			//根据memberid获取订单
			orderPOList = orderPOMapper.queryOrderBymemberid(orderPO.getMemberid());
		}else{  //查询付款/未付款的
			orderPOList =orderPOMapper.querySupportOrderOrPay(orderPO);
		}
		List<OrderDetailPO> orderDetailPOList = new ArrayList<>();

		for (int i = 0; i < orderPOList.size(); i++) {
			OrderDetailPO orderDetailPO = new OrderDetailPO();
			OrderPO orderPO2 = orderPOList.get(i);
			//根据订单中的projectid获取project
			ProjectPO projectPO = projectPOMapper.selectByPrimaryKey(orderPO2.getProjectid());
			//根据订单中的returnid获取return
			ReturnPO returnPO = returnPOMapper.selectByPrimaryKey(orderPO2.getReturnid());
			orderDetailPO.setOrderPO(orderPO2);
			orderDetailPO.setProjectPO(projectPO);
			orderDetailPO.setReturnPO(returnPO);
			orderDetailPOList.add(orderDetailPO);
		}
		return orderDetailPOList;


	}




}
