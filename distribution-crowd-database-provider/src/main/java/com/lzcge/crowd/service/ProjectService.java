package com.lzcge.crowd.service;

import com.lzcge.crowd.pojo.ResultEntity;
import com.lzcge.crowd.pojo.po.ProjectDetailPO;
import com.lzcge.crowd.pojo.po.ProjectPO;
import com.lzcge.crowd.pojo.po.TypePO;
import com.lzcge.crowd.pojo.vo.ProjectVO;
import com.lzcge.crowd.util.Page;

import java.util.List;
import java.util.Map;

public interface ProjectService {

	int saveProject(ProjectVO projectVO, String memberId);


	Page<ProjectPO> queryPage(Map<String, Object> projectMap);

	ProjectDetailPO queryProjectDetail(String id);

	List<ProjectPO> queryByStatus(Map<String, Object> map);

	void updateProject(ProjectVO projectVO);

	List<ProjectPO> querypublishProject(ProjectVO projectVO);

	void deleteProject(ProjectVO projectVO);

	ProjectPO queryProjectById(Integer id);

}