package com.lzcge.crowd.service.impl;

import com.lzcge.crowd.mapper.*;
import com.lzcge.crowd.pojo.ResultEntity;
import com.lzcge.crowd.pojo.po.*;
import com.lzcge.crowd.pojo.vo.MemberConfirmInfoVO;
import com.lzcge.crowd.pojo.vo.MemberLaunchInfoVO;
import com.lzcge.crowd.pojo.vo.ProjectVO;
import com.lzcge.crowd.pojo.vo.ReturnVO;
import com.lzcge.crowd.service.ProjectService;
import com.lzcge.crowd.util.CrowdConstant;
import com.lzcge.crowd.util.CrowdUtils;
import com.lzcge.crowd.util.Page;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.*;

@Service
@Transactional(readOnly = true)
public class ProjectServiceImpl implements ProjectService {

	@Autowired
	MemberConfirmInfoPOMapper memberConfirmInfoPOMapper;

	@Autowired
	MemberLaunchInfoPOMapper memberLaunchInfoPOMapper;

	@Autowired
	private MemberPOMapper memberPOMapper;

	@Autowired

	ProjectItemPicPOMapper projectItemPicPOMapper;

	@Autowired
	ProjectPOMapper projectPOMapper;

	@Autowired
	ReturnPOMapper returnPOMapper;

	@Autowired
	TagPOMapper tagPOMapper;

	@Autowired
	TypePOMapper typePOMapper;

	@Override
	@Transactional(readOnly = false,propagation = Propagation.REQUIRES_NEW,rollbackFor = Exception.class)
	public void saveProject(ProjectVO projectVO,String memberid) {
		//1.保存ProjectPO
		ProjectPO projectPO = new ProjectPO();
		BeanUtils.copyProperties(projectVO,projectPO);
		projectPO.setMemberid(Integer.parseInt(memberid));
		//设置创建时间
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		Date createDate = new Date();
		projectPO.setDeploydate(format.format(createDate));

		Calendar cal = Calendar.getInstance();
		cal.setTime(createDate);
		cal.add(Calendar.DAY_OF_MONTH, projectVO.getDay());// 24小时制
		createDate = cal.getTime();
		//设置截止日期
		projectPO.setDeploydate(format.format(createDate));
		//mapper文件insert方法需要设置"useGeneratedKeys="true" keyProperty="主键属性名""
		projectPOMapper.insertSelective(projectPO);
		//2.获取保存ProjectPO后的自增主键
		Integer projectPOId = projectPO.getId();
		//3.保存typeIdList
		List<Integer> typeIdList = projectVO.getTypeIdList();
		if(CrowdUtils.collectionEffectiveCheck(typeIdList)){
			typePOMapper.insertRelationBatch(projectPOId,typeIdList);
		}
		//4.保存tagIdList
		List<Integer> tagIdList = projectVO.getTagIdList();
		if(CrowdUtils.collectionEffectiveCheck(tagIdList)){
			tagPOMapper.insertRelationBatch(projectPOId,tagIdList);
		}
		//5.保存detailPicturePathList
		List<String> detailPicturePathList = projectVO.getDetailPicturePathList();
		if(CrowdUtils.collectionEffectiveCheck(detailPicturePathList)){
			projectItemPicPOMapper.insertBatch(projectPOId,detailPicturePathList);
		}
		//6.保存MemberLaunchInfoPO
		MemberLaunchInfoVO memberLaunchInfoVO = projectVO.getMemberLaunchInfoVO();
		if (memberLaunchInfoVO != null) {
			//删除旧的发起人信息
			MemberLaunchInfoPOExample example = new MemberLaunchInfoPOExample();
			example.createCriteria().andMemberidEqualTo(Integer.parseInt(memberid));
			memberLaunchInfoPOMapper.deleteByExample(example);

			//添加新的发起人信息
			MemberLaunchInfoPO memberLaunchInfoPO = new MemberLaunchInfoPO();
			BeanUtils.copyProperties(memberLaunchInfoVO,memberLaunchInfoPO);
			memberLaunchInfoPO.setMemberid(Integer.parseInt(memberid));
			memberLaunchInfoPOMapper.insertSelective(memberLaunchInfoPO);
		}
		//7.根据ReturnVO的List保存ReturnPO
		List<ReturnVO> returnVOList = projectVO.getReturnVOList();
		if(CrowdUtils.collectionEffectiveCheck(returnVOList)){
			List<ReturnPO> returnPOList = new ArrayList<>();
			for (ReturnVO returnVO : returnVOList) {
				ReturnPO returnPO = new ReturnPO();
				BeanUtils.copyProperties(returnVO,returnPO);
				returnPO.setProjectid(projectPOId);
				returnPOList.add(returnPO);
			}
			returnPOMapper.insertBatch(returnPOList);
		}
		//8.保存法人信息MemberConfirmInfoPO
		MemberConfirmInfoVO memberConfirmInfoVO = projectVO.getMemberConfirmInfoVO();
		if (memberConfirmInfoVO != null) {
			MemberConfirmInfoPO memberConfirmInfoPO = new MemberConfirmInfoPO(null,
					Integer.parseInt(memberid),
					memberConfirmInfoVO.getPaynum(),
					memberConfirmInfoVO.getCardnum());
			memberConfirmInfoPOMapper.insertSelective(memberConfirmInfoPO);
		}
	}

	//分页查询项目信息
	@Override
	public Page<ProjectPO> queryPage(Map<String, Object> projectMap) {
		Integer pageno = Integer.parseInt((String)projectMap.get("pageno"));
		Integer pagesize = Integer.parseInt((String)projectMap.get("pagesize"));
		Page<ProjectPO> projectPOPage = new Page<ProjectPO>(pageno,pagesize);
		projectMap.put("startIndex", projectPOPage.getStartIndex());
		projectMap.put("pageno",pageno);
		projectMap.put("pagesize",pagesize);
		List<ProjectPO> projectPOList = projectPOMapper.queryPage(projectMap);

		//查询总条数
		int count = projectPOMapper.queryCount(projectMap);

		projectPOPage.setDatas(projectPOList);
		projectPOPage.setTotalSize(count);

		return projectPOPage;
	}

	//查询项目详细信息
	@Override
	public ProjectDetailPO queryProjectDetail(String id) {
		Integer projectId = Integer.parseInt(id);
		//查询工程
		ProjectPO projectPO = projectPOMapper.selectByPrimaryKey(projectId);
		//查询工程详细图片
		ProjectItemPicPOExample projectItemPicPOExample = new ProjectItemPicPOExample();
		projectItemPicPOExample.createCriteria().andProjectidEqualTo(projectId);
		List<ProjectItemPicPO> projectItemPicPOS = projectItemPicPOMapper.selectByExample(projectItemPicPOExample);
		List<String> detailPicturePathList = new ArrayList<>();
		for (ProjectItemPicPO item:projectItemPicPOS ) {
			detailPicturePathList.add(item.getItemPicPath());
		}
		projectPO.setDetailPicturePathList(detailPicturePathList);

		//查询关联的会员信息
		MemberPO memberPO = memberPOMapper.selectByPrimaryKey(projectPO.getMemberid());

		//查询发布人发布时详细信息
		MemberLaunchInfoPOExample memberLaunchInfoPOExample = new MemberLaunchInfoPOExample();
		memberLaunchInfoPOExample.createCriteria().andMemberidEqualTo(projectPO.getMemberid());
		List<MemberLaunchInfoPO> memberLaunchInfoPOList = memberLaunchInfoPOMapper.selectByExample(memberLaunchInfoPOExample);
		MemberLaunchInfoPO memberLaunchInfoPO = new MemberLaunchInfoPO();
		if (CrowdUtils.collectionEffectiveCheck(memberLaunchInfoPOList)) {
			memberLaunchInfoPO = memberLaunchInfoPOList.get(0);
		}

		//查询回报信息
		ReturnPOExample example = new ReturnPOExample();
		example.createCriteria().andProjectidEqualTo(projectId);
		List<ReturnPO> returnPOList = returnPOMapper.selectByExample(example);
		ReturnPO returnPO = new ReturnPO();
		if (CrowdUtils.collectionEffectiveCheck(returnPOList)) {
			returnPO = returnPOList.get(0);
		}
		ProjectDetailPO projectDetailPO = new ProjectDetailPO();
		projectDetailPO.setProjectPO(projectPO);
		projectDetailPO.setMemberLaunchInfoPO(memberLaunchInfoPO);
		projectDetailPO.setReturnPO(returnPO);
		projectDetailPO.setMemberPO(memberPO);

		return projectDetailPO;
	}


	@Override
	@Transactional(readOnly = false,propagation = Propagation.REQUIRES_NEW,rollbackFor = Exception.class)
	public ProjectDetailPO updateProject(ProjectVO projectVO) {
		//1.保存ProjectPO
		ProjectPO projectPO = new ProjectPO();
		BeanUtils.copyProperties(projectVO,projectPO);
		//更新项目
		projectPOMapper.updateByPrimaryKeySelective(projectPO);
		//获取更新后的项目信息
		ProjectDetailPO projectDetailPO = queryProjectDetail(projectPO.getId().toString());
		return projectDetailPO;
	}
}
