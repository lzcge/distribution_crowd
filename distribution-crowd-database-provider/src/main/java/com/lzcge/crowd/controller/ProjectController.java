package com.lzcge.crowd.controller;

import com.alibaba.fastjson.JSON;
import com.lzcge.crowd.pojo.ResultEntity;
import com.lzcge.crowd.pojo.po.MemberAddressPO;
import com.lzcge.crowd.pojo.po.ProjectDetailPO;
import com.lzcge.crowd.pojo.po.ProjectPO;
import com.lzcge.crowd.pojo.po.TypePO;
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
	ResultEntity<Integer> saveProjectRemote(@RequestBody ProjectVO projectVO, @PathVariable("memberId")String memberId){
		try {
			int project = projectService.saveProject(projectVO,memberId);
			return ResultEntity.successWithData(project);
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
			String statuslist = (String)projectMap.get("statuslist");
			String typeIdlist = (String)projectMap.get("typeIdlist");
			if(statuslist!=null){
				List<Byte> bytes = JSON.parseArray(statuslist,Byte.class);
				projectMap.put("statuslist",bytes);
			}
			if(typeIdlist != null){
				List<Integer> ids = JSON.parseArray(typeIdlist,Integer.class);
				projectMap.put("typeIdlist",ids);
			}
			Page<ProjectPO> projectPOPage = projectService.queryPage(projectMap);
			return ResultEntity.successWithData(projectPOPage);
		} catch (Exception e) {
			e.printStackTrace();
			return ResultEntity.failed(e.getMessage());
		}

	}


	/**
	 * 根据项目状态查询项目信息
	 * @param statusmap
	 * @return
	 */
	@RequestMapping("retrieve/project/by/status")
	public ResultEntity<List<ProjectPO>> queryByStatus(@RequestParam Map<String, Object> statusmap) {
		try {
			String statulist = (String)statusmap.get("statulist");
			List<Byte> bytes = JSON.parseArray(statulist,Byte.class);
			statusmap.put("statulist",bytes);
			List<ProjectPO> projectPOList =  projectService.queryByStatus(statusmap);
			return ResultEntity.successWithData(projectPOList);
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
			projectService.updateProject(projectVO);
			//获取更新后的项目信息
			ProjectDetailPO projectDetailPO = projectService.queryProjectDetail(projectVO.getId().toString());
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
	 * 查询发布的项目信息
	 * @param projectVO
	 * @return
	 */
	@RequestMapping("project/manager/query/publishProject")
	public ResultEntity<List<ProjectPO>> querypublishProject(@RequestBody ProjectVO projectVO){
		try {
			List<ProjectPO> projectDetailPO =  projectService.querypublishProject(projectVO);
			return ResultEntity.successWithData(projectDetailPO);
		}catch (Exception e){
			e.printStackTrace();
			return ResultEntity.failed(e.getMessage());
		}
	}


	/**
	 * 查询项目信息根据id
	 * @param id
	 * @return
	 */
	@RequestMapping("project/manager/query/project/by/id")
	public ResultEntity<ProjectPO>  queryProjectById(@RequestParam("id") Integer id){
		try {
			ProjectPO projectPO = projectService.queryProjectById(id);
			return ResultEntity.successWithData(projectPO);
		}catch (Exception e){
			e.printStackTrace();
			return ResultEntity.failed(e.getMessage());
		}
	}

	/**
	 * 删除项目信息
	 * @param projectVO
	 * @return
	 */
	@RequestMapping("project/manager/delete/publishProject")
	public ResultEntity<String> deleteProject(@RequestBody ProjectVO projectVO){
		try {
			projectService.deleteProject(projectVO);
			return ResultEntity.successNoData();
		}catch (Exception e){
			e.printStackTrace();
			return ResultEntity.failed(e.getMessage());
		}
	}





}
