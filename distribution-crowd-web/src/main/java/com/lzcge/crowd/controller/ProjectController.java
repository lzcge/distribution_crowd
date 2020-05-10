package com.lzcge.crowd.controller;

import com.alibaba.fastjson.JSON;
import com.lzcge.crowd.api.MemberManagerRemoteService;
import com.lzcge.crowd.api.ProjectOperationRemoteService;
import com.lzcge.crowd.api.RedisOperationRemoteService;
import com.lzcge.crowd.pojo.ResultEntity;
import com.lzcge.crowd.pojo.po.*;
import com.lzcge.crowd.pojo.vo.*;
import com.lzcge.crowd.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class ProjectController {

	@Autowired
	private ProjectOperationRemoteService projectRemoteService;

	@Autowired
	private MemberManagerRemoteService managerRemoteService;

	@Autowired
	private RedisOperationRemoteService redisService;

	@Value(value="${oss.project.parent.folder}")
	private String ossProjectParentFolder;

	@Value(value="${oss.endpoint}")
	private String endpoint;

	@Value(value="${oss.accessKeyId}")
	private String accessKeyId;

	@Value(value="${oss.accessKeySecret}")
	private String accessKeySecret;

	@Value(value="${oss.bucketName}")
	private String bucketName;

	@Value(value="${oss.bucket.domain}")
	private String bucketDomain;

	//抽取上传文件冗余代码
	public String uploadFile(MultipartFile file) throws IOException {
		//准备上传
		String originalFilename = file.getOriginalFilename();
		//生成新的文件名
		String newFileName = UploadUtil.generateFileName(originalFilename);
		//文件夹名称
		String foldName = UploadUtil.generateFoldNameByDate(ossProjectParentFolder);
		//获取文件输入流
		InputStream inputStream = file.getInputStream();
		//上传到oss服务器
		UploadUtil.uploadSingleFile(endpoint,accessKeyId,accessKeySecret,newFileName,foldName,bucketName,inputStream);
		//拼接图片在服务器的路径
		String detailPicPath = bucketDomain+"/"+foldName+"/"+newFileName;
		return detailPicPath;
	}


	//保存所有完整信息到数据库
	@RequestMapping("/save/whole/project")
	public String saveWholeProject(Model model,HttpSession httpSession){
		//登录检查从现有session中获取已登录的member对象
		MemberSignSuccessVO signSuccessVO = (MemberSignSuccessVO) httpSession.getAttribute(CrowdConstant.ATTR_NAME_LOGIN_MEMBER);
		//如果对象为null，提示登录
		if (signSuccessVO == null) {
			model.addAttribute(CrowdConstant.ATTR_NAME_MESSAGE,CrowdConstant.MESSAGE_ACCESS_DENIED);
			return "/login.html";
		}
		String memberSignToken = signSuccessVO.getToken();
		ProjectVO project = (ProjectVO) httpSession.getAttribute(CrowdConstant.ATTR_NAME_INIT_PROJECT);
		String projectToken = project.getProjectTempToken();
		//令牌
		TokenVO tokenVO = new TokenVO();
		tokenVO.setMemberSignToken(memberSignToken);
		tokenVO.setProjectTempToken(projectToken);
		ResultEntity<Integer> resultEntity = projectRemoteService.saveWholeProject(tokenVO);
		if (ResultEntity.FAILED.equals(resultEntity.getResult())){
			model.addAttribute(CrowdConstant.ATTR_NAME_MESSAGE,resultEntity.getMessage());
			return "/project/start-step-3.html";
		}



//		//重新查询出项目信息，并放入redis用于定时任务监控众筹状态
//		Integer id = resultEntity.getData();
//		ResultEntity<ProjectPO> projectPOResultEntity = projectRemoteService.queryProjectById(id);
//		if (ResultEntity.FAILED.equals(projectPOResultEntity.getResult())){
//			model.addAttribute(CrowdConstant.ATTR_NAME_MESSAGE,projectPOResultEntity.getMessage());
//			return "/project/start-step-3.html";
//		}
//		//redis中去获取存放所有发布项目的list信息
//		ResultEntity<Object> projecListRestult = redisService.retrieveObjectValueByObjectKey(CrowdConstant.REDIS_PROJECT_STATUS_PREFIX);
//		if (ResultEntity.FAILED.equals(projecListRestult.getResult())){
//			model.addAttribute(CrowdConstant.ATTR_NAME_MESSAGE,projecListRestult.getMessage());
//			return "/project/start-step-3.html";
//		}
//		List<ProjectPO> redisProjectPOList = (List<ProjectPO>) projecListRestult.getData();
//		if(redisProjectPOList==null){
//			redisProjectPOList = new ArrayList<>();
//		}
//		redisProjectPOList.add(projectPOResultEntity.getData());
//		//放入redis
//		ResultEntity<String> stringResultEntity = redisService.saveNormalObjectKeyValue(CrowdConstant.REDIS_PROJECT_STATUS_PREFIX,redisProjectPOList);
//		if (ResultEntity.FAILED.equals(stringResultEntity.getResult())){
//			model.addAttribute(CrowdConstant.ATTR_NAME_MESSAGE,stringResultEntity.getMessage());
//			return "/project/start-step-3.html";
//		}


		return "/project/start-step-4.html";
	}


	//保存确认法人信息到Redis
	@RequestMapping("/save/confirm/info")
	@ResponseBody
	public ResultEntity<String> saveConfirmInfo(@RequestBody MemberConfirmInfoVO memberConfirmInfoVO, HttpSession httpSession){
		//登录检查从现有session中获取已登录的member对象
		MemberSignSuccessVO signSuccessVO = (MemberSignSuccessVO) httpSession.getAttribute(CrowdConstant.ATTR_NAME_LOGIN_MEMBER);
		//如果对象为null，提示登录
		if (signSuccessVO == null) {
			return ResultEntity.failed(CrowdConstant.MESSAGE_ACCESS_DENIED);
		}
		String memberSignToken = signSuccessVO.getToken();
		ProjectVO project = (ProjectVO) httpSession.getAttribute(CrowdConstant.ATTR_NAME_INIT_PROJECT);
		String projectToken = project.getProjectTempToken();
		if (memberConfirmInfoVO == null) {
			return ResultEntity.failed(CrowdConstant.MESSAGE_CONFIRM_DENIED);
		}
		//令牌
		memberConfirmInfoVO.setMemberSignToken(memberSignToken);
		memberConfirmInfoVO.setProjectTempToken(projectToken);
		ResultEntity<String> resultEntity = projectRemoteService.saveConfirmInfo(memberConfirmInfoVO);
		if (ResultEntity.FAILED.equals(resultEntity.getResult())){
			return ResultEntity.failed(resultEntity.getMessage());
		}


		return ResultEntity.successNoData();
	}



	//保存回报信息到Redis
	@RequestMapping("/save/return/info")
	@ResponseBody
	public ResultEntity<String> saveReturnInfo(@RequestBody ReturnVO returnVO, HttpSession httpSession){
		//登录检查从现有session中获取已登录的member对象
		MemberSignSuccessVO signSuccessVO = (MemberSignSuccessVO) httpSession.getAttribute(CrowdConstant.ATTR_NAME_LOGIN_MEMBER);
		//如果对象为null，提示登录
		if (signSuccessVO == null) {
			return ResultEntity.failed(CrowdConstant.MESSAGE_ACCESS_DENIED);
		}
		String memberSignToken = signSuccessVO.getToken();
		ProjectVO project = (ProjectVO) httpSession.getAttribute(CrowdConstant.ATTR_NAME_INIT_PROJECT);
		String projectToken = project.getProjectTempToken();
		if (returnVO == null) {
			return ResultEntity.failed(CrowdConstant.MESSAGE_RETURN_DENIED);
		}
		//令牌
		returnVO.setMemberSignToken(memberSignToken);
		returnVO.setProjectTempToken(projectToken);
		ResultEntity<String> resultEntity = projectRemoteService.saveReturnInfo(returnVO);
		if (ResultEntity.FAILED.equals(resultEntity.getResult())){
			return ResultEntity.failed(resultEntity.getMessage());
		}
		return ResultEntity.successNoData();
	}


	//保存项目信息到Redis
	@RequestMapping("save/project/info")
	@ResponseBody
	public ResultEntity<String> saveProjectInfo(@RequestBody ProjectVO projectVO, HttpSession httpSession){
		//登录检查从现有session中获取已登录的member对象
		MemberSignSuccessVO signSuccessVO = (MemberSignSuccessVO) httpSession.getAttribute(CrowdConstant.ATTR_NAME_LOGIN_MEMBER);
		//如果对象为null，提示登录
		if (signSuccessVO == null) {
			return ResultEntity.failed(CrowdConstant.MESSAGE_ACCESS_DENIED);
		}
		String memberSignToken = signSuccessVO.getToken();
		ProjectVO project = (ProjectVO) httpSession.getAttribute(CrowdConstant.ATTR_NAME_INIT_PROJECT);
		String projectToken = project.getProjectTempToken();
		if (projectVO == null) {
			return ResultEntity.failed(CrowdConstant.MESSAGE_PROJECTVO_DENIED);
		}
		//项目对象的令牌
		projectVO.setMemberSignToken(memberSignToken);
		projectVO.setProjectTempToken(projectToken);
		//项目发起人对象的令牌
		projectVO.getMemberLaunchInfoVO().setMemberSignToken(memberSignToken);
		projectVO.getMemberLaunchInfoVO().setProjectTempToken(projectToken);
		ResultEntity<String> resultEntity = projectRemoteService.saveProjectInfo(projectVO);
		if (ResultEntity.FAILED.equals(resultEntity.getResult())){
			return ResultEntity.failed(resultEntity.getMessage());
		}
		return ResultEntity.successNoData();
	}

	//上传详情图片,一个键多个值，用list接收文件数据
	@RequestMapping("project/upload/detailPicture")
	@ResponseBody
	public ResultEntity<String> uploadDetailPic(
			@RequestParam("detailPicList") List<MultipartFile> detailPicList
			, HttpSession httpSession) throws IOException {
		//登录检查从现有session中获取已登录的member对象
		MemberSignSuccessVO signSuccessVO = (MemberSignSuccessVO) httpSession.getAttribute(CrowdConstant.ATTR_NAME_LOGIN_MEMBER);
		//如果对象为null，提示登录
		if (signSuccessVO == null) {
			return ResultEntity.failed(CrowdConstant.MESSAGE_ACCESS_DENIED);
		}
		//判断用户上传的文件是否有效
		if(!CrowdUtils.collectionEffectiveCheck(detailPicList)){
			return ResultEntity.failed(CrowdConstant.MESSAGE_UPLOAD_FILE_EMPTY);
		}
		//存放文件名
		//List<String> pathList = new ArrayList<>();
		StringBuilder sb = new StringBuilder();
		//遍历用户上传的文件
		for (MultipartFile detailPic : detailPicList) {
			//如果其中一个文件为空，就结束本次循环，执行下一个
			if(detailPic.isEmpty()){
				continue;
			}
			String detailPicPath = uploadFile(detailPic);
			sb.append(detailPicPath).append(",");
		}
		/*保存头图相关信息
		原方法：图片单独上传，并存入Redis
		现在改为： 把上传图片的路径返回到前台，保存到隐藏域，和其他数据一起提交
		String memberSignToken = signSuccessVO.getToken();
		ProjectVO projectVO = (ProjectVO) httpSession.getAttribute(CrowdConstant.ATTR_NAME_INIT_PROJECT);
		String projectToken = projectVO.getProjectTempToken();
		return projectRemoteService.saveDetailPictureListPath(memberSignToken,projectToken,pathList);*/
		return ResultEntity.successWithData(sb.toString());
	}

	//上传头图
	@RequestMapping("project/upload/headPicture")
	@ResponseBody
	public ResultEntity<String> uploadHeadPic(
			@RequestParam("headPicPath") MultipartFile headPic
			,HttpSession httpSession) throws IOException {
		//登录检查从现有session中获取已登录的member对象
		MemberSignSuccessVO signSuccessVO = (MemberSignSuccessVO) httpSession.getAttribute(CrowdConstant.ATTR_NAME_LOGIN_MEMBER);
		//如果对象为null，提示登录
		if (signSuccessVO == null) {
			return ResultEntity.failed(CrowdConstant.MESSAGE_ACCESS_DENIED);
		}
		//排除上传文件为空的情况
		if (headPic.isEmpty()) {
			return ResultEntity.failed(CrowdConstant.MESSAGE_UPLOAD_FILE_EMPTY);
		}
		String headPicPath = uploadFile(headPic);
		/*保存头图相关信息
		原方法：图片单独上传，并存入Redis
		现在改为： 把上传图片的路径返回到前台，保存到隐藏域，和其他数据一起提交
		String memberSignToken = signSuccessVO.getToken();
		ProjectVO projectVO = (ProjectVO) httpSession.getAttribute(CrowdConstant.ATTR_NAME_INIT_PROJECT);
		String projectToken = projectVO.getProjectTempToken();
		return projectRemoteService.saveHeadPicturePath(memberSignToken,projectToken,headPicPath);*/
		return ResultEntity.successWithData(headPicPath);
	}


	//上传回报信息说明头图
	@RequestMapping("/return/upload/describPic")
	@ResponseBody
	public ResultEntity<String> describPic(
			@RequestParam("describPicPath") MultipartFile describPic
			,HttpSession httpSession) throws IOException {
		//登录检查从现有session中获取已登录的member对象
		MemberSignSuccessVO signSuccessVO = (MemberSignSuccessVO) httpSession.getAttribute(CrowdConstant.ATTR_NAME_LOGIN_MEMBER);
		//如果对象为null，提示登录
		if (signSuccessVO == null) {
			return ResultEntity.failed(CrowdConstant.MESSAGE_ACCESS_DENIED);
		}
		//排除上传文件为空的情况
		if (describPic.isEmpty()) {
			return ResultEntity.failed(CrowdConstant.MESSAGE_UPLOAD_FILE_EMPTY);
		}
		String describPicPath = uploadFile(describPic);
		return ResultEntity.successWithData(describPicPath);
	}

	//点击同意协议按钮，初始化项目
	@RequestMapping("project/agree/protocol")
	public String agreeProtocol(HttpSession httpSession, Model model){
		//登录检查从现有session中获取已登录的member对象
		MemberSignSuccessVO signSuccessVO = (MemberSignSuccessVO) httpSession.getAttribute(CrowdConstant.ATTR_NAME_LOGIN_MEMBER);
		//如果对象为null，跳转到登录页面
		if (signSuccessVO == null) {
			model.addAttribute(CrowdConstant.ATTR_NAME_MESSAGE,CrowdConstant.MESSAGE_ACCESS_DENIED);
			return "member/login";
		}
		//从member对象中获取token
		String token = signSuccessVO.getToken();
		//调用远程方法初始化项目
		ResultEntity<ProjectVO> projectVOResultEntity = projectRemoteService.initCreation(token);
		//初始化失败抛出异常
		if(ResultEntity.FAILED.equals(projectVOResultEntity.getResult())){
			throw new RuntimeException(projectVOResultEntity.getMessage());
		}
		//获取初始化项目的对象存入session
		ProjectVO data = projectVOResultEntity.getData();
		httpSession.setAttribute(CrowdConstant.ATTR_NAME_INIT_PROJECT,data);
		//查询项目发起人信息并跳转页面
		return "redirect:/to/create/project/page";
	}

	@RequestMapping("to/create/project/page")
	public String toCreateProjectPage(HttpSession httpSession,Model model){
		//获取当前登录的用户
		MemberSignSuccessVO signSuccessVO = (MemberSignSuccessVO) httpSession.getAttribute(CrowdConstant.ATTR_NAME_LOGIN_MEMBER);
		//检查是否登录
		if (signSuccessVO == null) {
			model.addAttribute(CrowdConstant.ATTR_NAME_MESSAGE,CrowdConstant.MESSAGE_ACCESS_DENIED);
			return "member/login";
		}
		//获取登录成功用户的token
		String token = signSuccessVO.getToken();
		//通过token在Redis中获取用户ID查询发起人信息
		ResultEntity<MemberLaunchInfoPO> memberLaunchInfoPOByToken = managerRemoteService.getMemberLaunchInfoPOByToken(token);
		if(ResultEntity.FAILED.equals(memberLaunchInfoPOByToken.getResult())){
			throw new RuntimeException(memberLaunchInfoPOByToken.getMessage());
		}
		//将发起人信息存放到作用域传递到前台显示
		model.addAttribute("memberLaunchInfoPO",memberLaunchInfoPOByToken.getData());
		return "project/start-step-1";
	}


	/**
	 * 查询所有项目类别标签信息
	 * @return
	 */
	@ResponseBody
	@GetMapping("/project/queryProjectType")
	public ResultEntity queryProjectType() {
		// 查询
		ResultEntity<List<TypePO>> typeList = projectRemoteService.queryProjectType();
		if (ResultEntity.FAILED.equals(typeList.getResult())){
			return ResultEntity.failed(typeList.getMessage());
		}
		return ResultEntity.successWithData(typeList.getData());

	}



	/**
	 * 分页查询项目信息
	 * @param queryIndexVo
	 * @return
	 */
	@ResponseBody
	@PostMapping("/project/pageQuery")
	public ResultEntity pageQuery(@RequestBody QueryIndexVo queryIndexVo) {
		String pagetext = queryIndexVo.getQueryText();
		Integer pageno = queryIndexVo.getPageNo();
		Integer pagesize = queryIndexVo.getPageSize();

		List<Byte> status=queryIndexVo.getStatus();
		List<Integer> typeIds = queryIndexVo.getTypeIds();

		Map<String, Object> projectMap = new HashMap<String, Object>();
		projectMap.put("pageno", pageno);
		projectMap.put("pagesize", pagesize);
		if ( StringUtil.isNotEmpty(pagetext) ) {
			pagetext = pagetext.replaceAll("%", "\\\\%");
		}
		projectMap.put("pagetext", pagetext);

		if(status !=null && status.size()>0){
			String statuslist = JSON.toJSONString(status);
			projectMap.put("statuslist",statuslist);
		}
		if(typeIds !=null && typeIds.size()>0){
			String typeIdlist = JSON.toJSONString(typeIds);
			projectMap.put("typeIdlist",typeIdlist);
		}


		// 分页查询
		ResultEntity<Page<ProjectPO>> page = projectRemoteService.pageQuery(projectMap);
		if (ResultEntity.FAILED.equals(page.getResult())){
			return ResultEntity.failed(page.getMessage());
		}
		return ResultEntity.successWithData(page.getData());

	}


	/**
	 * 查看项目详情
	 * @param
	 * @return
	 */
	@ResponseBody
	@PostMapping("/project/do/projectdetail")
	public ResultEntity projectdetail(String id,HttpSession session) {
		ResultEntity<ProjectDetailPO> resultEntity = projectRemoteService.queryProjectDetail(id);
		if (ResultEntity.FAILED.equals(resultEntity.getResult())){
			return ResultEntity.failed(resultEntity.getMessage());
		}
		ProjectDetailPO projectDetailPO = resultEntity.getData();
		//用户支持时需要支付的总金额
		Integer totalMoney = projectDetailPO.getReturnPO().getSupportmoney()+projectDetailPO.getReturnPO().getFreight();
		projectDetailPO.setTotalMoney(totalMoney);

		session.setAttribute("ProjectDetailPO",projectDetailPO);
		return ResultEntity.successWithData(resultEntity.getData());

	}

	/**
	 * 更新项目信息
	 * @param
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/project/do/update/project")
	public ResultEntity updateProject(ProjectVO projectVO,Model model,HttpSession session) {
		ResultEntity<ProjectDetailPO> resultEntity = projectRemoteService.updateProject(projectVO);
		if (ResultEntity.FAILED.equals(resultEntity.getResult())){
			return ResultEntity.failed(resultEntity.getMessage());
		}
		ProjectDetailPO projectDetailPO = resultEntity.getData();
		//用户支持时需要支付的总金额
		Integer totalMoney = projectDetailPO.getReturnPO().getSupportmoney()+projectDetailPO.getReturnPO().getFreight();
		projectDetailPO.setTotalMoney(totalMoney);
		session.setAttribute("ProjectDetailPO",projectDetailPO);
		return ResultEntity.successWithData(projectDetailPO);

	}


	/**
	 * 查找企业用户发布的项目
	 * @param projectVO
	 * @return
	 */
	@RequestMapping("/query/publishProject")
	@ResponseBody
	public ResultEntity<List<ProjectPO>> querypublishProject(ProjectVO projectVO){
		ResultEntity<List<ProjectPO>> querypublishProject = projectRemoteService.querypublishProject(projectVO);
		if(ResultEntity.FAILED.equals(querypublishProject.getResult())){
			return ResultEntity.failed(querypublishProject.getMessage());
		}

		return ResultEntity.successWithData(querypublishProject.getData());
	}


	/**
	 * 查找企业用户发布的项目
	 * @param projectVO
	 * @return
	 */
	@RequestMapping("/delete/project")
	@ResponseBody
	public ResultEntity<String> deleteProject(ProjectVO projectVO){
		ResultEntity<String> resultEntity = projectRemoteService.deleteProject(projectVO);
		if(ResultEntity.FAILED.equals(resultEntity.getResult())){
			return ResultEntity.failed(resultEntity.getMessage());
		}

		return ResultEntity.successNoData();
	}


}
