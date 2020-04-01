package com.lzcge.crowd.service;

import com.lzcge.crowd.entity.MemberCert;
import com.lzcge.crowd.pojo.po.MemberLaunchInfoPO;
import com.lzcge.crowd.pojo.po.MemberPO;

public interface MemberPOService {

	int getLoginAcct(String loginacct);

	int getPhoneNumAcct(String phoneNum);

	int getEmailCount(String email);

	void saveMemberPO(MemberPO memberPO);

	void updateAcctType(MemberPO memberPO);

	void updateBasicinfo(MemberPO memberPO);

	MemberPO getMemberByAcct(String loginacct);

	MemberLaunchInfoPO getLaunchInfoByMemberId(String memberid);

	void delteMemberCert(MemberCert memberCert);

	void insertMemberCert(MemberCert memberCert);
}