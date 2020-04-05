package com.lzcge.crowd.pojo.po;

import com.lzcge.crowd.pojo.vo.TokenVO;

import java.io.Serializable;

public class MemberAddressPO extends TokenVO implements Serializable{

	private static final long serialVersionUID = 1L;
    private Integer id;

    private Integer memberid;

    private String address;

    private String username;

    private String phoneNum;

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPhoneNum() {
		return phoneNum;
	}

	public void setPhoneNum(String phoneNum) {
		this.phoneNum = phoneNum;
	}

	public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getMemberid() {
        return memberid;
    }

    public void setMemberid(Integer memberid) {
        this.memberid = memberid;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address == null ? null : address.trim();
    }
}