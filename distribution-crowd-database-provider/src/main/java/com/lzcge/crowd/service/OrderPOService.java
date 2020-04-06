package com.lzcge.crowd.service;

import com.lzcge.crowd.entity.MemberCert;
import com.lzcge.crowd.pojo.po.*;
import com.lzcge.crowd.pojo.vo.OrderVO;
import org.apache.ibatis.annotations.Param;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

public interface OrderPOService {



	Integer saveOrder(OrderVO orderVO);

	OrderPO queryOrderById( Integer orderid);

	void updateOrder( OrderVO orderVO);

	void deleteOrderByorderid(Integer orderid);


	List<OrderDetailPO> querySupportOrder(OrderVO orderVO);


}