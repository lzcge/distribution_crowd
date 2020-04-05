package com.lzcge.crowd.service.impl;

import com.lzcge.crowd.entity.MemberCert;
import com.lzcge.crowd.mapper.MemberLaunchInfoPOMapper;
import com.lzcge.crowd.mapper.MemberPOMapper;
import com.lzcge.crowd.mapper.OrderPOMapper;
import com.lzcge.crowd.pojo.po.*;
import com.lzcge.crowd.pojo.vo.OrderVO;
import com.lzcge.crowd.service.MemberPOService;
import com.lzcge.crowd.service.OrderPOService;
import com.lzcge.crowd.util.CrowdUtils;
import org.aspectj.weaver.ast.Or;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class OrderPOServiceImpl implements OrderPOService {


	@Autowired
	private OrderPOMapper orderPOMapper;


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
}
