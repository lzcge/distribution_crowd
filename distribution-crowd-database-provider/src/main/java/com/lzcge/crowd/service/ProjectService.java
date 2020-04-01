package com.lzcge.crowd.service;

import com.lzcge.crowd.pojo.vo.ProjectVO;

public interface ProjectService {

	void saveProject(ProjectVO projectVO, String memberId);
}