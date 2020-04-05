package com.lzcge.crowd.service;

import com.lzcge.crowd.pojo.po.ProjectDetailPO;
import com.lzcge.crowd.pojo.po.ProjectPO;
import com.lzcge.crowd.pojo.vo.ProjectVO;
import com.lzcge.crowd.util.Page;

import java.util.List;
import java.util.Map;

public interface ProjectService {

	void saveProject(ProjectVO projectVO, String memberId);

	Page<ProjectPO> queryPage(Map<String, Object> projectMap);

	ProjectDetailPO queryProjectDetail(String id);

	ProjectDetailPO updateProject(ProjectVO projectVO);
}