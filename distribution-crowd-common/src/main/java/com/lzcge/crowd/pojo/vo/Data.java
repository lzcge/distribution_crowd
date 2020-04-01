package com.lzcge.crowd.pojo.vo;



import com.lzcge.crowd.entity.MemberCert;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @description:
 * @author: lzcge
 * @create: 2019-09-04
 **/
public class Data implements Serializable{

	private List<Integer> ids ;

	private List<MemberCert> certimgs = new ArrayList<MemberCert>();


	public List<Integer> getIds() {
		return ids;
	}

	public void setIds(List<Integer> ids) {
		this.ids = ids;
	}

	public List<MemberCert> getCertimgs() {
		return certimgs;
	}

	public void setCertimgs(List<MemberCert> certimgs) {
		this.certimgs = certimgs;
	}

}
