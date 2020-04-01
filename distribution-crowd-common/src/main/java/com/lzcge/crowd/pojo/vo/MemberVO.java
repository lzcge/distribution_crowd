package com.lzcge.crowd.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MemberVO  extends TokenVO implements Serializable{
	private String loginacct;

	private String userpswd;

	private String phoneNum;

	private String randomCode;

	private Integer id;

	private String username;

	private String email;

	private String authstatus;

	private String usertype;

	private String realname;

	private String cardnum;

	private String accttype;

}
