package com.lzcge.crowd.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MemberConfirmInfoVO extends TokenVO {

	// 支付宝账号
	private String paynum;

	// 法人身份证号
	private String cardnum;
}
