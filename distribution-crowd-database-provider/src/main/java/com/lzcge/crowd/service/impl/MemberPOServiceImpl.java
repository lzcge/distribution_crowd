package com.lzcge.crowd.service.impl;

import com.lzcge.crowd.entity.MemberCert;
import com.lzcge.crowd.mapper.MemberLaunchInfoPOMapper;
import com.lzcge.crowd.mapper.MemberPOMapper;
import com.lzcge.crowd.pojo.po.MemberLaunchInfoPO;
import com.lzcge.crowd.pojo.po.MemberLaunchInfoPOExample;
import com.lzcge.crowd.pojo.po.MemberPO;
import com.lzcge.crowd.pojo.po.MemberPOExample;
import com.lzcge.crowd.service.MemberPOService;
import com.lzcge.crowd.util.CrowdUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)		//声明式事务
public class MemberPOServiceImpl implements MemberPOService {

	@Autowired
	private MemberPOMapper memberPOMapper;

	@Autowired
	private MemberLaunchInfoPOMapper memberLaunchInfoPOMapper;

	@Override
	public int getLoginAcct(String loginacct) {
		MemberPOExample example = new MemberPOExample();
		// 2.封装查询条件
		example.createCriteria().andLoginacctEqualTo(loginacct);
		// 3.执行查询
		return memberPOMapper.countByExample(example);
	}


	/**
	 * 获取电话数量
	 * @param phoneNum
	 * @return
	 */
	@Override
	public int getPhoneNumAcct(String phoneNum) {
		MemberPOExample example = new MemberPOExample();
		// 2.封装查询条件
		example.createCriteria().andLoginphoneNumEqualTo(phoneNum);
		// 3.执行查询
		return memberPOMapper.countByExample(example);
	}
	/**
	 * 获取邮箱数量
	 * @param email
	 * @return
	 */
	@Override
	public int getEmailCount(String email) {
		MemberPOExample example = new MemberPOExample();
		// 2.封装查询条件
		example.createCriteria().andLoginemailEqualTo(email);
		// 3.执行查询
		return memberPOMapper.countByExample(example);
	}



	@Override
	//设置非只读、传播方式、回滚条件
	@Transactional(readOnly = false,propagation = Propagation.REQUIRES_NEW,rollbackFor = Exception.class)
	public void saveMemberPO(MemberPO memberPO) {
		memberPOMapper.insertSelective(memberPO);
	}

	@Override
	@Transactional(readOnly = false,propagation = Propagation.REQUIRES_NEW,rollbackFor = Exception.class)
	public void updateAcctType(MemberPO memberPO) {
		memberPOMapper.updateByPrimaryKeySelective(memberPO);
	}

	@Override
	@Transactional(readOnly = false,propagation = Propagation.REQUIRES_NEW,rollbackFor = Exception.class)
	public void updateBasicinfo(MemberPO memberPO) {
		memberPOMapper.updateByPrimaryKeySelective(memberPO);
	}

	@Override
	public MemberPO getMemberByAcct(String loginacct) {
		MemberPOExample example = new MemberPOExample();
		example.createCriteria().andLoginacctEqualTo(loginacct);
		List<MemberPO> list = memberPOMapper.selectByExample(example);
		if(CrowdUtils.collectionEffectiveCheck(list)){
			return list.get(0);
		}
		return null;
	}

	@Override
	public MemberLaunchInfoPO getLaunchInfoByMemberId(String memberid) {
		MemberLaunchInfoPOExample example = new MemberLaunchInfoPOExample();
		example.createCriteria().andMemberidEqualTo(Integer.parseInt(memberid));
		List<MemberLaunchInfoPO> memberLaunchInfoPO = memberLaunchInfoPOMapper.selectByExample(example);
		if (CrowdUtils.collectionEffectiveCheck(memberLaunchInfoPO)) {
			return memberLaunchInfoPO.get(0);
		}else {
			return null;
		}
	}

	@Override
	@Transactional(readOnly = false,propagation = Propagation.REQUIRES_NEW,rollbackFor = Exception.class)
	public void delteMemberCert(MemberCert memberCert) {
		memberPOMapper.delteMemberCert(memberCert);

	}

	@Override
	@Transactional(readOnly = false,propagation = Propagation.REQUIRES_NEW,rollbackFor = Exception.class)
	public void insertMemberCert(MemberCert memberCert) {
		memberPOMapper.insertMemberCert(memberCert);
	}
}
