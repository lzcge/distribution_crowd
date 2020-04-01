package com.lzcge.crowd.controller;


import com.lzcge.crowd.entity.MemberCert;
import com.lzcge.crowd.pojo.ResultEntity;
import com.lzcge.crowd.pojo.po.CertPO;
import com.lzcge.crowd.service.CertService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Controller
public class CertController {

	@Autowired
	private CertService certService;


	/**
	 * 获取用户类型需要上传的资质
	 * @param accttype
	 * @return
	 */
	@RequestMapping(value = "/retrieve/member/cert",method = RequestMethod.POST)
	@ResponseBody
	ResultEntity<List<CertPO>> queryCertByAccttype(@RequestParam("accttype") String accttype){
		try {
			List<CertPO> certPO = certService.queryCertByAccttype(accttype);
			return ResultEntity.successWithData(certPO);
		} catch (Exception e) {
			e.printStackTrace();
			return ResultEntity.failed(e.getMessage());
		}
	}




}
