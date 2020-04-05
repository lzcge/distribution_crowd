package com.lzcge.crowd.mapper;

import com.lzcge.crowd.entity.MemberCert;
import com.lzcge.crowd.pojo.po.MemberAddressPO;
import com.lzcge.crowd.pojo.po.MemberPO;
import com.lzcge.crowd.pojo.po.MemberPOExample;
import com.lzcge.crowd.pojo.po.OrderPO;
import com.lzcge.crowd.pojo.vo.OrderVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface OrderPOMapper {


	Integer saveOrder( OrderVO orderVO);

	OrderPO queryOrderById(@Param("id") Integer orderid);
}