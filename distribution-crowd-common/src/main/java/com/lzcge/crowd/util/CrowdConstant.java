package com.lzcge.crowd.util;

import java.util.HashMap;
import java.util.Map;

public class CrowdConstant {

	//短信验证码短信前缀
	public static final String REDIS_RANDOM_CODE_PREFIX = "RANDOM_CODE_";

	//邮箱验证码短信前缀
	public static final String REDIS_EMAIL_RANDOM_CODE_PREFIX = "MailVerifyCode_";
	//登录用户token
	public static final String REDIS_MEMBER_SING_TOKEN_PREFIX = "SIGNED_MEMBER_";
	//工程对象放入redis，后缀通过UUID创建
	public static final String REDIS_PROJECT_TEMP_TOKEN_PREFIX = "PROJECT_TEMP_TOKEN_";

	public static final String ATTR_NAME_MESSAGE = "MESSAGE";
	public static final String ATTR_NAME_LOGIN_ADMIN = "LOGIN-ADMIN";
	public static final String ATTR_NAME_LOGIN_MEMBER = "LOGIN-MEMBER";
	public static final String ATTR_NAME_PAGE_INFO = "PAGE-INFO";
	public static final String ATTR_NAME_INIT_PROJECT = "INIT-PROJECT";

	public static final String MESSAGE_ORDER_FAILED = "订单获取失败";
	public static final String MESSAGE_ORDER_NOT_EXISTS = "订单已失效";
	public static final String MESSAGE_ORDER_UPDATE_FAILED = "订单更新失败";


	public static final String MESSAGE_ADDRESS_FAILED = "收获地址已存在";
	public static final String MESSAGE_LOGIN_FAILED = "登录账号或密码不正确！请核对后再登录！";
	public static final String MESSAGE_LOGIN_PHONE_FAILED = "登录账号与手机号不匹配！";
	public static final String MESSAGE_CODE_INVALID = "明文不是有效字符串，请核对后再操作！";
	public static final String MESSAGE_ACCESS_DENIED = "请登录后再操作！";
	public static final String MESSAGE_LOGIN_ACCT_ALREADY_IN_USE = "登录账号被占用，请重新设定！";
	public static final String MESSAGE_LOGIN_PHONENUM_ALREADY_IN_USE = "登录手机号被占用，请重新设定！";
	public static final String MESSAGE_LOGIN_EMAIL_ALREADY_IN_USE = "登录邮箱被占用，请重新设定！";

	public static final Map<String, String> EXCEPTION_MESSAGE_MAP = new HashMap<>();
	public static final String MESSAGE_RANDOM_CODE_LENGTH_INVALID = "验证码长度不合法！";
	public static final String MESSAGE_REDIS_KEY_OR_VALUE_INVALID = "待存入Redis中的key或value不是有效字符串";
	public static final String MESSAGE_REDIS_KEY_TIME_OUT_INVALID = "请明确指定Redis中key的过期时间";
	public static final String MESSAGE_PHONE_NUM_INVALID = "手机号不符合要求";
	public static final String MESSAGE_LOGINACCT_INVALID = "账号无效";
	public static final String MESSAGE_EMAIL_INVALID = "邮箱无效";
	public static final String MESSAGE_CODE_NOT_MATCH = "验证码不匹配";
	public static final String MESSAGE_CODE_NOT_EXISTS = "验证码不存在或已过期";
	public static final String MESSAGE_UPLOAD_FILE_EMPTY = "未检测到上传的文件";
	public static final String MESSAGE_PROJECTVO_DENIED = "未检测到项目数据";
	public static final String MESSAGE_RETURN_DENIED = "未检测到回报数据";
	public static final String MESSAGE_CONFIRM_DENIED = "未检测到确认法人数据";

	static {
		EXCEPTION_MESSAGE_MAP.put("java.lang.ArithmeticException", "系统在进行数学运算时发生错误");
		EXCEPTION_MESSAGE_MAP.put("java.lang.RuntimeException", "系统在运行时发生错误");
		EXCEPTION_MESSAGE_MAP.put("com.atguigu.crowd.funding.exception.LoginException", "登录过程中运行错误");
		EXCEPTION_MESSAGE_MAP.put("org.springframework.security.access.AccessDeniedException", "尊敬的用户，您现在不具备访问当前功能的权限！请联系超级管理员。");
	}

}
