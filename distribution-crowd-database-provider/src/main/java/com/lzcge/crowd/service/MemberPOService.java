package com.lzcge.crowd.service;

import com.lzcge.crowd.entity.MemberCert;
import com.lzcge.crowd.pojo.po.MemberAddressPO;
import com.lzcge.crowd.pojo.po.MemberLaunchInfoPO;
import com.lzcge.crowd.pojo.po.MemberPO;
import com.lzcge.crowd.pojo.po.OrderDetailPO;
import com.lzcge.crowd.pojo.vo.MemberVO;
import com.lzcge.crowd.pojo.vo.OrderVO;
import org.apache.ibatis.annotations.Param;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

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

	List<MemberAddressPO> queryAddress(MemberAddressPO memberAddressPO);

	List<MemberAddressPO> selectMemberAddressByadress(MemberAddressPO memberAddressPO);

	List<MemberAddressPO> addMemberAddress(MemberAddressPO memberAddressPO);







}