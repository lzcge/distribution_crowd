package com.lzcge.crowd.service;



import com.lzcge.crowd.entity.Cert;
import com.lzcge.crowd.entity.MemberCert;
import com.lzcge.crowd.pojo.po.CertPO;

import java.util.List;
import java.util.Map;


public interface CertService {


	public List<CertPO> queryCertByAccttype(String accttype);





}
