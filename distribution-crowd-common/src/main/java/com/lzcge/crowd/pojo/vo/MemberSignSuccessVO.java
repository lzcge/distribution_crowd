package com.lzcge.crowd.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MemberSignSuccessVO implements Serializable {
	private Integer id;

	private String username;

	private String email;

	private String token;

	private String loginacct;

	private String userpswd;

	private String authstatus;

	private String usertype;

	private String realname;

	private String cardnum;

	private String accttype;

	private String phoneNum;
}
