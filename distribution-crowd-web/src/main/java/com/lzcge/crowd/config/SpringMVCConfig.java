package com.lzcge.crowd.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
@Configuration
public class SpringMVCConfig implements WebMvcConfigurer {

	/**
	 * 根据路径映射到页面，适合直接跳转页面的路径
	 * 为什么不直接访问页面？？？
	 * 真的不能直接访问页面啊
	 * @param registry
	 */
	@Override
	public void addViewControllers(ViewControllerRegistry registry) {
		/*
		访问toLogin，跳转到login页面，相当于以前在mvc中配置的
		<mvc:view-controller path="/toLogin" viewName="login"/>
		* */
		//跳转到用户登录页面
		String urlPath="login.html";
		String viewName="login";
		registry.addViewController(urlPath).setViewName(viewName);//跳转到用户登录页面

		//跳转到用户注册页面
		urlPath="member/reg.html";
		viewName="member/reg";
		registry.addViewController(urlPath).setViewName(viewName);//跳转到用户注册页面


		urlPath="ManagerLogin.html";
		viewName="ManagerLogin";
		registry.addViewController(urlPath).setViewName(viewName);
		//跳转到个人中心页面
		urlPath="member/center.html";
		viewName="member/center";
		registry.addViewController(urlPath).setViewName(viewName);

		//跳转到投诉反馈页面
		urlPath="member/complain.html";
		viewName="member/complain";
		registry.addViewController(urlPath).setViewName(viewName);

		//实名认证-跳转到账号类型更新页面
		urlPath="member/accttype.html";
		viewName="member/accttype";
		registry.addViewController(urlPath).setViewName(viewName);

		//实名认证-跳转到实名信息更新页面
		urlPath="/member/basicinfo.html";
		viewName="member/basicinfo";
		registry.addViewController(urlPath).setViewName(viewName);

		//实名认证-跳转到邮箱确认页面
		urlPath="/member/checkemail.html";
		viewName="member/checkemail";
		registry.addViewController(urlPath).setViewName(viewName);

		//实名认证-跳转到邮箱确认页面
		urlPath="/member/checkauthcode.html";
		viewName="member/checkauthcode";
		registry.addViewController(urlPath).setViewName(viewName);

		/**===========发布项目部分==============**/

		//跳转到同意协议页面
		urlPath = "/project/to/agree/page";
		viewName = "project/start";
		registry.addViewController(urlPath).setViewName(viewName);


		urlPath = "/project/start-step-1.html";
		viewName = "project/start-step-1";
		registry.addViewController(urlPath).setViewName(viewName);

		urlPath = "/project/start-step-2.html";
		viewName = "project/start-step-2";
		registry.addViewController(urlPath).setViewName(viewName);

		urlPath = "/project/start-step-3.html";
		viewName = "project/start-step-3";
		registry.addViewController(urlPath).setViewName(viewName);

		urlPath = "/project/start-step-4.html";
		viewName = "project/start-step-4";
		registry.addViewController(urlPath).setViewName(viewName);

		urlPath = "/pay/pay.html";
		viewName = "pay/pay";
		registry.addViewController(urlPath).setViewName(viewName);

		/**======项目查看，支持========**/
		urlPath = "/project/displayAllProjects.html";
		viewName = "project/displayAllProjects";
		registry.addViewController(urlPath).setViewName(viewName);

		urlPath = "/project/projectdetail.html";
		viewName = "project/projectdetail";
		registry.addViewController(urlPath).setViewName(viewName);

		/**======项目支持========**/
		urlPath = "/pay/pay-ConfirmReturn.html";
		viewName = "pay/pay-ConfirmReturn";
		registry.addViewController(urlPath).setViewName(viewName);

		urlPath = "/member/minecrowdfunding.html";
		viewName = "member/minecrowdfunding";
		registry.addViewController(urlPath).setViewName(viewName);

		urlPath = "/member/selfinfo.html";
		viewName = "member/selfinfo";
		registry.addViewController(urlPath).setViewName(viewName);



	}
}
