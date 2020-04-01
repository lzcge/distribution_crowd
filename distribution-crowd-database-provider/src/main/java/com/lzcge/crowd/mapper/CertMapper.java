package com.lzcge.crowd.mapper;




import com.lzcge.crowd.entity.Cert;
import com.lzcge.crowd.entity.MemberCert;
import com.lzcge.crowd.pojo.po.CertPO;

import java.util.List;
import java.util.Map;

public interface CertMapper {

	List<CertPO> queryCertByAccttype(String accttype);






}