package com.lzcge.crowd.mapper;

import com.lzcge.crowd.entity.MemberCert;
import com.lzcge.crowd.pojo.po.MemberAddressPO;
import com.lzcge.crowd.pojo.po.MemberPO;
import com.lzcge.crowd.pojo.po.MemberPOExample;
import com.lzcge.crowd.pojo.po.OrderPO;
import com.lzcge.crowd.pojo.vo.OrderVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface OrderPOMapper {


	Integer saveOrder( OrderVO orderVO);

	OrderPO queryOrderById(@Param("id") Integer orderid);

	void updateOrder( OrderPO orderVO);

	List<OrderPO> queryOrderBymemberid(Integer memberid);

	List<OrderPO> querySupportOrderOrPay(OrderPO orderPO);

	List<OrderPO> queryByStatus(Map<String,Object> statuMap);

	void deleteByPrimaryKey(Integer id);




}