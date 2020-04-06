package com.lzcge.crowd.pojo.po;

import com.lzcge.crowd.pojo.vo.TokenVO;
import lombok.Data;

import java.io.Serializable;

/**
 * @description:
 * @author: lzcge
 * @create: 2020-04-06
 **/
@Data
public class OrderDetailPO extends TokenVO implements Serializable {
	private static final long serialVersionUID = 1L;

	private ProjectPO projectPO;

	private OrderPO orderPO;

	private ReturnPO returnPO;
}
