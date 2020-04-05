package com.lzcge.crowd.controller;

import com.lzcge.crowd.pojo.ResultEntity;
import com.lzcge.crowd.pojo.po.MemberAddressPO;
import com.lzcge.crowd.pojo.po.ProjectDetailPO;
import com.lzcge.crowd.pojo.po.ProjectPO;
import com.lzcge.crowd.pojo.vo.MemberVO;
import com.lzcge.crowd.pojo.vo.ProjectVO;
import com.lzcge.crowd.service.ProjectService;
import com.lzcge.crowd.util.CrowdConstant;
import com.lzcge.crowd.util.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
public class ProjectController {

	@Autowired
	private ProjectService projectService;

	@RequestMapping("save/project/remote/{memberId}")
	ResultEntity<String> saveProjectRemote(@RequestBody ProjectVO projectVO, @PathVariable("memberId")String memberId){
		try {
			projectService.saveProject(projectVO,memberId);
			return ResultEntity.successNoData();
		} catch (Exception e) {
			e.printStackTrace();
			return ResultEntity.failed(e.getMessage());
		}
	}


	/**
	 * 分页查询项目信息
	 * @param projectMap
	 * @return
	 */
	@RequestMapping("retrieve/project/querypage")
	ResultEntity<Page<ProjectPO>> pageQueryProject(@RequestParam Map<String, Object> projectMap){
		try {
			Page<ProjectPO> projectPOPage = projectService.queryPage(projectMap);
			return ResultEntity.successWithData(projectPOPage);
		} catch (Exception e) {
			e.printStackTrace();
			return ResultEntity.failed(e.getMessage());
		}

	}

	/**
	 * 查询项目详细信息
	 * @param id
	 * @return
	 */
	@RequestMapping("project/manager/query/projectdetail")
	public ResultEntity<ProjectDetailPO> queryProjectDetail(@RequestParam("id") String id){
		try {
			ProjectDetailPO projectDetailPO = projectService.queryProjectDetail(id);
			if(projectDetailPO==null){
				return ResultEntity.failed(CrowdConstant.MESSAGE_PROJECTVO_DENIED);
			}else {
				return ResultEntity.successWithData(projectDetailPO);
			}
		}catch (Exception e){
			e.printStackTrace();
			return ResultEntity.failed(e.getMessage());
		}

	}

	/**
	 * 更新项目信息
	 * @param projectVO
	 * @return
	 */
	@RequestMapping("project/manager/update/projectdetail")
	public ResultEntity<ProjectDetailPO> updateProject(@RequestBody ProjectVO projectVO){
		try {
			ProjectDetailPO projectDetailPO =  projectService.updateProject(projectVO);
			if(projectDetailPO==null){
				return ResultEntity.failed(CrowdConstant.MESSAGE_PROJECTVO_DENIED);
			}else {
				return ResultEntity.successWithData(projectDetailPO);
			}
		}catch (Exception e){
			e.printStackTrace();
			return ResultEntity.failed(e.getMessage());
		}


	}





}
