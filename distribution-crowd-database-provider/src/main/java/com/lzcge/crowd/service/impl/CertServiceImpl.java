package com.lzcge.crowd.service.impl;

import com.lzcge.crowd.entity.Cert;
import com.lzcge.crowd.entity.MemberCert;
import com.lzcge.crowd.mapper.CertMapper;
import com.lzcge.crowd.pojo.po.CertPO;
import com.lzcge.crowd.service.CertService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;


@Service
public class CertServiceImpl implements CertService {

	@Autowired
	private CertMapper certDao;


	public List<CertPO> queryCertByAccttype(String accttype) {
		return certDao.queryCertByAccttype(accttype);
	}



}
