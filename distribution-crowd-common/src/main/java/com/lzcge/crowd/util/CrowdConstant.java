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
	//发布项目时工程对象放入redis临时存储，后缀通过UUID创建
	public static final String REDIS_PROJECT_TEMP_TOKEN_PREFIX = "PROJECT_TEMP_TOKEN_";

	//项目状态
	public static final Byte PROJECT_STATUS_NOT_CROWD = 0;//即将开始
	public static final Byte PROJECT_STATUS_ING_CROWD = 1;//众筹中
	public static final Byte PROJECT_STATUS_SUCCESS_CROWD = 2;//众筹成功
	public static final Byte PROJECT_STATUS_FAILED_CROWD = 3;//众筹失败
	public static final Byte PROJECT_STATUS_AUTH_FAILED_CROWD = 4;//审核失败

	//投诉内容处理状态、
	public static final Integer COMPLAIN_STATUS_NOT_HANDLE = 0;//未处理
	public static final Integer COMPLAIN_STATUS_SUCCESS_HANDLE = 1;//处理完毕


	//项目发布成功后放入到list中然后放入redis，用于监控项目众筹状态
	public static final String REDIS_PROJECT_STATUS_PREFIX = "PROJECT_LIST_STATUS";

	public static final String ATTR_NAME_MESSAGE = "MESSAGE";
	public static final String ATTR_NAME_LOGIN_ADMIN = "LOGIN-ADMIN";
	public static final String ATTR_NAME_LOGIN_MEMBER = "LOGIN-MEMBER";
	public static final String ATTR_NAME_PAGE_INFO = "PAGE-INFO";
	public static final String ATTR_NAME_INIT_PROJECT = "INIT-PROJECT";

	//订单生成成功后放入到list中然后放入redis，用于监控订单是否完成付款
	public static final String REDIS_ORDER_STATUS_PREFIX = "ORDER_LIST_STATUS";
	public static final String MESSAGE_ORDER_FAILED = "订单获取失败";
	public static final String MESSAGE_ORDER_NOT_EXISTS = "订单已失效";
	public static final String MESSAGE_ORDER_UPDATE_FAILED = "订单更新失败";
	//订单过期时间(15分钟)
	public static final Long ORDER_PAST_TIME = 15L;
	//下单成功后在redis中缓存等待支付时间(30分钟)
	public static final Integer REDIS_ORDER_WAIT_PAY_TIME = 30;
	//订单状态信息
	//待支付
	public static final Character ORDER_STATUS_WAIT_PAY = '0';
	//支付完成
	public static final Character ORDER_STATUS_SUCCESS_PAY = '1';
	//支付失败，交易关闭
	public static final Character ORDER_STATUS_FAILED_PAY = '2';


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
