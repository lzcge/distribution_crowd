package com.lzcge.crowd.controller;


import com.lzcge.crowd.api.DataBaseOperationRemoteService;
import com.lzcge.crowd.api.RedisOperationRemoteService;
import com.lzcge.crowd.entity.Cert;
import com.lzcge.crowd.entity.MemberCert;
import com.lzcge.crowd.pojo.ResultEntity;
import com.lzcge.crowd.pojo.po.CertPO;
import com.lzcge.crowd.pojo.po.MemberLaunchInfoPO;
import com.lzcge.crowd.pojo.po.MemberPO;
import com.lzcge.crowd.pojo.vo.MemberSignSuccessVO;
import com.lzcge.crowd.pojo.vo.MemberVO;
import com.lzcge.crowd.util.CrowdConstant;
import com.lzcge.crowd.util.CrowdUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;


import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;
import java.util.*;

@RestController
public class MemberController {

	@Autowired
	RedisOperationRemoteService redisRemoteService;		//装配操作Redis的类


	//使用SpringSecurity中的BCryptPasswordEncoder加密工具进行密码加密和密码比较。
	@Autowired
	BCryptPasswordEncoder passwordEncoder;

	@Autowired
	DataBaseOperationRemoteService databasesRemoteService;

	@Autowired
	private JavaMailSender javaMailSender;

	@Value("${spring.mail.username}")
	private String username;

//	//Spring会根据@Value注解中的表达式读取yml/properties配置文件给成员变量设置对应的值
//	@Value("${crowd.short.message.appCode}")
//	private String appcode;

	@RequestMapping("get/Member/Launch/InfoPO/By/Token")
	public ResultEntity<MemberLaunchInfoPO> getMemberLaunchInfoPOByToken(@RequestParam("token") String token){
		//根据token查询memberID
		ResultEntity<String> resultEntity = redisRemoteService.retrieveStringValueByStringKey(token);
		String memberId = resultEntity.getData();
		if (memberId == null) {
			return ResultEntity.failed(CrowdConstant.MESSAGE_ACCESS_DENIED);
		}
		//根据memberID查询项目发起人信息
		return databasesRemoteService.getLaunchInfoByMemberId(memberId);
	}


	/**
	 * 退出，根据key删除Redis中对应的value
	 * @param token
	 * @return
	 */
	@RequestMapping("member/manager/logout")
	public ResultEntity<String> logout(@RequestParam("token") String token){
		return redisRemoteService.removeByKey(token);
	}

	/**
	 * 登录
	 * @return
	 */
	@RequestMapping(value = "member/manager/login",method = RequestMethod.POST)
	public ResultEntity<MemberSignSuccessVO> login(@RequestBody MemberVO memberVO){
		String loginacct = memberVO.getLoginacct();
		String userpswd = memberVO.getUserpswd();
		String phoneNum = memberVO.getPhoneNum();
		String randomCode = memberVO.getRandomCode();

		ResultEntity<MemberSignSuccessVO> result = new ResultEntity<>();
		//根据登录账号从数据库查询MemberPO对象
		ResultEntity<MemberPO> resultEntity = databasesRemoteService.retrieveMemberByLoginAcct(loginacct);
		if(ResultEntity.FAILED.equals(resultEntity.getResult())){
			return ResultEntity.failed(resultEntity.getMessage());
		}

		//获取MemberPO对象
		MemberPO memberPO = resultEntity.getData();
		//判断MemberPO对象是否为空
		if (memberPO == null) {
			return ResultEntity.failed(CrowdConstant.MESSAGE_LOGIN_FAILED);
		}

		//判断手机号是否正确
		if(!memberPO.getPhoneNum().equals(phoneNum)){
			return ResultEntity.failed(CrowdConstant.MESSAGE_LOGIN_PHONE_FAILED);
		}


//		//判断验证码是否正确
//
//		//检查验证码是否有效
//		if(!CrowdUtils.strEffectiveCheck(randomCode)){
//			return ResultEntity.failed(CrowdConstant.MESSAGE_CODE_INVALID);
//		}
//
//		//拼接验证码的key
//		String codeKey = CrowdConstant.REDIS_RANDOM_CODE_PREFIX+phoneNum;
//
//		//远程调用Redis_provider的方法获取key对应的值
//		ResultEntity<String> resultEntity2 = redisRemoteService.retrieveStringValueByStringKey(codeKey);
//		if(ResultEntity.FAILED.equals(resultEntity2.getResult())){
//			return ResultEntity.failed(resultEntity2.getMessage());
//		}
//
//		//获取Redis中key对应的value(验证码)
//		String randomCodeRedis = resultEntity2.getData();
//		if(!CrowdUtils.strEffectiveCheck(randomCodeRedis)){
//			return ResultEntity.failed(CrowdConstant.MESSAGE_CODE_NOT_EXISTS);
//		}

		//判断前台传入的值和Redis中的值是否相等
		String randomCodeRedis = "123456";  //手动构造验证码，调试结束后删除
		if(!Objects.equals(randomCode,randomCodeRedis)){
			return ResultEntity.failed(CrowdConstant.MESSAGE_CODE_NOT_MATCH);
		}

//		//如果验证码匹配需要删除当前key对应的验证码
//		ResultEntity<String> resultEntity1 = redisRemoteService.removeByKey(codeKey);
//		if(ResultEntity.FAILED.equals(resultEntity1.getResult())){
//			return ResultEntity.failed(resultEntity1.getMessage());
//		}


		//获取从数据库中查询到的密码
		String userpswdByDataBase = memberPO.getUserpswd();

		//比较数据库加密后的密码和前台传入的密码
		boolean matches = passwordEncoder.matches(userpswd, userpswdByDataBase);
		if (!matches) {
			return ResultEntity.failed(CrowdConstant.MESSAGE_LOGIN_FAILED);
		}



		//对比成功，生成token
		String token = CrowdUtils.generateRedisKey(CrowdConstant.REDIS_MEMBER_SING_TOKEN_PREFIX);
		//token作为key，userid作为value，过期时间30分钟存入Redis
		String id = memberPO.getId()+"";
		ResultEntity<String> resultEntitySaveToken = redisRemoteService.saveNormalStringKeyValue(token, id, 30);
		if(ResultEntity.FAILED.equals(resultEntitySaveToken.getResult())){
			return ResultEntity.failed(resultEntitySaveToken.getMessage());
		}
		//账号密码验证成功，封装对象
		MemberSignSuccessVO signSuccessVO = new MemberSignSuccessVO();
		//将memberPO复制到signSuccessVO对象中
		BeanUtils.copyProperties(memberPO,signSuccessVO);
		signSuccessVO.setToken(token);
		return ResultEntity.successWithData(signSuccessVO);

	}


	/**
	 * 注册
	 * @param memberVO
	 * @return
	 */
	@RequestMapping(value = "member/manager/register",method = RequestMethod.POST)
	public ResultEntity<String> register(@RequestBody() MemberVO memberVO){
		//获取前台的输入验证码
		String randomCode = memberVO.getRandomCode();
		//检查验证码是否有效
		if(!CrowdUtils.strEffectiveCheck(randomCode)){
			return ResultEntity.failed(CrowdConstant.MESSAGE_CODE_INVALID);
		}

		//检查手机号是否有效
		String phoneNum = memberVO.getPhoneNum();
		if(!CrowdUtils.strEffectiveCheck(phoneNum)){
			return ResultEntity.failed(CrowdConstant.MESSAGE_PHONE_NUM_INVALID);
		}

		//远程调用database_provider检查账号是否存在
		String loginacct = memberVO.getLoginacct();
		ResultEntity<Integer> integerResultEntity = databasesRemoteService.retrieveLoginAcctCount(loginacct);
		if(ResultEntity.FAILED.equals(integerResultEntity.getResult())){
			return ResultEntity.failed(integerResultEntity.getMessage());
		}

		Integer count = integerResultEntity.getData();
		//账号被占用，返回失败信息
		if(count>0){
			return ResultEntity.failed(CrowdConstant.MESSAGE_LOGIN_ACCT_ALREADY_IN_USE);
		}

		//远程调用database_provider检查手机号是否存在
		ResultEntity<Integer> phoneNumIntegerResultEntity = databasesRemoteService.retrieveLoginPhoneNumCount(phoneNum);
		if(ResultEntity.FAILED.equals(phoneNumIntegerResultEntity.getResult())){
			return ResultEntity.failed(phoneNumIntegerResultEntity.getMessage());
		}

		Integer phoneNumcount = phoneNumIntegerResultEntity.getData();
		//手机号被占用，返回失败信息
		if(phoneNumcount>0){
			return ResultEntity.failed(CrowdConstant.MESSAGE_LOGIN_PHONENUM_ALREADY_IN_USE);
		}

		//远程调用database_provider检查邮箱是否存在
		String email = memberVO.getEmail();
		ResultEntity<Integer> emailIntegerResultEntity = databasesRemoteService.retrieveLoginEmailCount(email);
		if(ResultEntity.FAILED.equals(emailIntegerResultEntity.getResult())){
			return ResultEntity.failed(emailIntegerResultEntity.getMessage());
		}

		Integer emailcount = emailIntegerResultEntity.getData();
		//手机号被占用，返回失败信息
		if(emailcount>0){
			return ResultEntity.failed(CrowdConstant.MESSAGE_LOGIN_EMAIL_ALREADY_IN_USE);
		}


		//拼接验证码的key
		String codeKey = CrowdConstant.REDIS_RANDOM_CODE_PREFIX+phoneNum;

		//远程调用Redis_provider的方法获取key对应的值
		ResultEntity<String> resultEntity = redisRemoteService.retrieveStringValueByStringKey(codeKey);
		if(ResultEntity.FAILED.equals(resultEntity.getResult())){
			return resultEntity;
		}

		//获取Redis中key对应的value(验证码)
		String randomCodeRedis = resultEntity.getData();
		if(!CrowdUtils.strEffectiveCheck(randomCodeRedis)){
			return ResultEntity.failed(CrowdConstant.MESSAGE_CODE_NOT_EXISTS);
		}

		//判断前台传入的值和Redis中的值是否相等
		if(!Objects.equals(randomCode,randomCodeRedis)){
			return ResultEntity.failed(CrowdConstant.MESSAGE_CODE_NOT_MATCH);
		}

		//如果验证码匹配需要删除当前key对应的验证码
		ResultEntity<String> resultEntity1 = redisRemoteService.removeByKey(codeKey);
		if(ResultEntity.FAILED.equals(resultEntity1.getResult())){
			return resultEntity1;
		}


		//密码加密
		String userpswd = memberVO.getUserpswd();
		String encode = passwordEncoder.encode(userpswd);
		System.out.println(encode);
		memberVO.setUserpswd(encode);

		//设置账号默认认证状态（0）
		memberVO.setAuthstatus("0");
		//设置账号类型
		String statu = memberVO.getUsertype().equals("个人")?"0":"1";
		memberVO.setUsertype(statu);

		MemberPO memberPO = new MemberPO();
		//将memberVO对象中的值复制到memberPO对象中
		BeanUtils.copyProperties(memberVO,memberPO);
		//远程调用database_provider保存注册信息
		return databasesRemoteService.saveMemberRemote(memberPO);
	}

	/**
	 * 发送短信验证码
	 * @param phoneNum
	 * @return
	 */
	@RequestMapping("member/manager/send/code")
	public ResultEntity<String> sendCode(@RequestParam("phoneNum")String phoneNum){
		//验证手机号
		if(!CrowdUtils.strEffectiveCheck(phoneNum)){
			return ResultEntity.failed(CrowdConstant.MESSAGE_PHONE_NUM_INVALID);
		}
		//生成验证码
		int length = 4;
		String code = CrowdUtils.randomCode(length);

		//保存到Redis
		int timeoutMinute = 15;		//验证码默认过期时间15分钟
		//Redis的key是固定前缀+手机号
		String normalKey = CrowdConstant.REDIS_RANDOM_CODE_PREFIX+phoneNum;
		ResultEntity<String> resultEntity = redisRemoteService.saveNormalStringKeyValue(normalKey, code, timeoutMinute);
		//判断key是否保存成功，如果失败就直接返回
		if(ResultEntity.FAILED.equals(resultEntity.getResult())){
			return resultEntity;
		}
		//验证码成功添加到Redis后发送到用户手机
		try {
			String appcode = "";
			CrowdUtils.sendMessage(appcode,code,phoneNum);
			return ResultEntity.successNoData();
		} catch (Exception e) {
			e.printStackTrace();
			return ResultEntity.failed(e.getMessage());
		}
	}

	/**
	 * 发送邮箱验证码
	 * @param memberVO
	 * @return
	 */
	@RequestMapping(value = "member/manager/send/emailcode",method = RequestMethod.POST)
	public ResultEntity<String> sendEmailCode(@RequestBody MemberVO memberVO){

		try {
			SimpleMailMessage message = new SimpleMailMessage();
			String MailVerifyCode = String.valueOf(new Random().nextInt(899999) + 100000);
			message.setFrom(username);
			message.setTo(memberVO.getEmail());
			message.setSubject("验证码：");
			message.setText(MailVerifyCode);
			javaMailSender.send(message);
			int timeoutMinute = 1;  //1分钟
			//放入redis
			ResultEntity<String> resultEntity = redisRemoteService.saveNormalStringKeyValue(CrowdConstant.REDIS_EMAIL_RANDOM_CODE_PREFIX + memberVO.getEmail(), MailVerifyCode, timeoutMinute);
			//判断key是否保存成功，如果失败就直接返回
			if (ResultEntity.FAILED.equals(resultEntity.getResult())) {
				return resultEntity;
			}
			return ResultEntity.successNoData();
		}catch (Exception e){
			e.printStackTrace();
			return ResultEntity.failed(e.getMessage());
		}
	}

	/**
	 * 验证邮箱验证码
	 * @param memberVO
	 * @return
	 */
	@RequestMapping(value = "member/manager/finish/emailcode",method = RequestMethod.POST)
	public ResultEntity<String> finishEmailApply(@RequestBody MemberVO memberVO){

		//从redis中获取系统发送验证码
		ResultEntity<String> resultEntity = redisRemoteService.retrieveStringValueByStringKey(CrowdConstant.REDIS_EMAIL_RANDOM_CODE_PREFIX+memberVO.getEmail());
		if(ResultEntity.FAILED.equals(resultEntity.getResult())){
			return ResultEntity.failed(resultEntity.getMessage());
		}
		String randomCode = resultEntity.getData();
		//验证验证码是否存在或者过期
		if(!CrowdUtils.strEffectiveCheck(randomCode)){
			return ResultEntity.failed(CrowdConstant.MESSAGE_CODE_NOT_EXISTS);

		}
		//验证是否相等
		if(!randomCode.equals(memberVO.getRandomCode())){
			return ResultEntity.failed(CrowdConstant.MESSAGE_CODE_NOT_MATCH);
		}
		ResultEntity<String> resultEntity1 = redisRemoteService.removeByKey(CrowdConstant.REDIS_EMAIL_RANDOM_CODE_PREFIX+memberVO.getEmail());
		if(ResultEntity.FAILED.equals(resultEntity1.getResult())){
			return ResultEntity.failed(resultEntity1.getMessage());
		}

		return ResultEntity.successNoData();


	}



	/**
	 * 实名认证-更新账户类型
	 * @param memberVO
	 * @return
	 */
	@RequestMapping(value = "member/manager/updateAcctType" ,method = RequestMethod.POST)
	public ResultEntity<String> updateAcctType(@RequestBody MemberVO memberVO){
		//检查是否登录(Redis中memberSignToken对应的key是否有值)
		String memberSignToken = memberVO.getMemberSignToken();
		ResultEntity<String> tokenResultEntity = redisRemoteService.retrieveStringValueByStringKey(memberSignToken);
		if(ResultEntity.FAILED.equals(tokenResultEntity.getResult())){
			return ResultEntity.failed(tokenResultEntity.getMessage());
		}
		if(!CrowdUtils.strEffectiveCheck(tokenResultEntity.getData())){
			return ResultEntity.failed(CrowdConstant.MESSAGE_ACCESS_DENIED);

		}

		MemberPO memberPO = new MemberPO();
		//将memberVO对象中的值复制到memberPO对象中
		BeanUtils.copyProperties(memberVO,memberPO);
		//远程调用database_provider更新账号类型信息
		ResultEntity<String> resultEntity = databasesRemoteService.updateAcctType(memberPO);
		if(ResultEntity.FAILED.equals(resultEntity.getResult())){
			return resultEntity;
		}

		return ResultEntity.successNoData();
	}

	/**
	 * 实名认证-更新用户实名信息
	 * @param memberVO
	 * @return
	 */
	@RequestMapping(value = "member/manager/updateBasicinfo" ,method = RequestMethod.POST)
	public ResultEntity<String> updateBasicinfo(@RequestBody MemberVO memberVO){
		//检查是否登录(Redis中memberSignToken对应的key是否有值)
		String memberSignToken = memberVO.getMemberSignToken();
		ResultEntity<String> tokenResultEntity = redisRemoteService.retrieveStringValueByStringKey(memberSignToken);
		if(ResultEntity.FAILED.equals(tokenResultEntity.getResult())){
			return ResultEntity.failed(tokenResultEntity.getMessage());
		}
		if(!CrowdUtils.strEffectiveCheck(tokenResultEntity.getData())){
			return ResultEntity.failed(CrowdConstant.MESSAGE_ACCESS_DENIED);

		}

		MemberPO memberPO = new MemberPO();
		//将memberVO对象中的值复制到memberPO对象中
		BeanUtils.copyProperties(memberVO,memberPO);
		//远程调用database_provider更新用户实名信息
		ResultEntity<String> resultEntity = databasesRemoteService.updateBasicinfo(memberPO);
		if(ResultEntity.FAILED.equals(resultEntity.getResult())){
			return resultEntity;
		}
		return ResultEntity.successNoData();
	}


	/**
	 * 保存用户上传的资质图片
	 * @param certimgs
	 * @return
	 */
	@RequestMapping(value = "member/manager/saveMemberCert" ,method = RequestMethod.POST)
	public ResultEntity<String> saveMemberCert(@RequestBody List<MemberCert> certimgs	){

		//删除原有的关系
		ResultEntity<String> delResultEntity = databasesRemoteService.delteMemberCert(certimgs.get(0));
		if(ResultEntity.FAILED.equals(delResultEntity.getResult())){
			return delResultEntity;
		}
		// 保存会员与资质关系数据.
		for (MemberCert memberCert:certimgs ) {
			ResultEntity<String> insetResultEntity = databasesRemoteService.insertMemberCert(memberCert);
			if(ResultEntity.FAILED.equals(insetResultEntity.getResult())){
				return insetResultEntity;
			}
		}

		return ResultEntity.successNoData();
	}


}
