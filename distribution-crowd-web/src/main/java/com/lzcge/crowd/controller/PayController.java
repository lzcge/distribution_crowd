package com.lzcge.crowd.controller;

import com.alibaba.fastjson.JSON;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.lzcge.crowd.api.MemberManagerRemoteService;
import com.lzcge.crowd.api.ProjectOperationRemoteService;
import com.lzcge.crowd.api.RedisOperationRemoteService;
import com.lzcge.crowd.config.AlipayConfig;
import com.lzcge.crowd.pojo.ResultEntity;
import com.lzcge.crowd.pojo.po.OrderPO;
import com.lzcge.crowd.pojo.po.ProjectDetailPO;
import com.lzcge.crowd.pojo.vo.OrderVO;
import com.lzcge.crowd.pojo.vo.ProjectVO;
import com.lzcge.crowd.util.CrowdConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@Controller
public class PayController {

	@Autowired
	private MemberManagerRemoteService memmberRemoteService;

	@Autowired
	private ProjectOperationRemoteService projectRemoteService;

	@Autowired
	private RedisOperationRemoteService redisService;

	@RequestMapping("/pay/paymoney.html")
	public String paymoneyhtml(@RequestParam("orderid") Integer orderid, Model model){
		//根据订单id获取
		ResultEntity<OrderPO> resultEntity = memmberRemoteService.queryOrderById(orderid);
		if(ResultEntity.FAILED.equals(resultEntity.getResult())){
			model.addAttribute(CrowdConstant.ATTR_NAME_MESSAGE,resultEntity.getMessage());
			return "error";
		}
		OrderPO orderPO = resultEntity.getData();
		//订单还存在，没有过期
		if(orderPO.getStatus().equals("0")){
			//写入model域中
			model.addAttribute("orderInfo",resultEntity.getData());
			//返回页面
			return "pay/pay-paymoney";

		}else{
			//写入model域中
			model.addAttribute(CrowdConstant.ATTR_NAME_MESSAGE,"订单已过期");
			return "/member/minecrowdfunding.html";
		}
	}


	//支付方法
	@RequestMapping("/pay/do/pay")
	@ResponseBody
	public String pay(HttpServletRequest request) throws Exception {
		//获得初始化的AlipayClient
		AlipayClient alipayClient =
				new DefaultAlipayClient(
						AlipayConfig.gatewayUrl,
						AlipayConfig.app_id,
						AlipayConfig.merchant_private_key,
						"json",
						AlipayConfig.charset,
						AlipayConfig.alipay_public_key,
						AlipayConfig.sign_type);

		//设置请求参数
		AlipayTradePagePayRequest alipayRequest = new AlipayTradePagePayRequest();
		alipayRequest.setReturnUrl(AlipayConfig.return_url);
		alipayRequest.setNotifyUrl(AlipayConfig.notify_url);

		//商户订单号，商户网站订单系统中唯一订单号，必填
		String ordernum = new String(request.getParameter("WIDout_trade_no").getBytes("ISO-8859-1"),"UTF-8");
		//付款金额，必填
		String money = new String(request.getParameter("WIDtotal_amount").getBytes("ISO-8859-1"),"UTF-8");
		//订单名称，必填
		String subject = new String(request.getParameter("WIDsubject").getBytes("ISO-8859-1"),"UTF-8");
		//备注
		String remark = new String(request.getParameter("WIDbody").getBytes("ISO-8859-1"),"UTF-8");

		alipayRequest.setBizContent("{\"out_trade_no\":\""+ ordernum +"\","
				+ "\"total_amount\":\""+ money +"\","
				+ "\"subject\":\""+ subject +"\","
				+ "\"body\":\""+ remark +"\","
				+ "\"product_code\":\"FAST_INSTANT_TRADE_PAY\"}");

		//若想给BizContent增加其他可选请求参数，以增加自定义超时时间参数timeout_express来举例说明
		//alipayRequest.setBizContent("{\"out_trade_no\":\""+ out_trade_no +"\","
		//      + "\"total_amount\":\""+ total_amount +"\","
		//      + "\"subject\":\""+ subject +"\","
		//      + "\"body\":\""+ body +"\","
		//      + "\"timeout_express\":\"10m\","
		//      + "\"product_code\":\"FAST_INSTANT_TRADE_PAY\"}");
		//请求参数可查阅【电脑网站支付的API文档-alipay.trade.page.pay-请求参数】章节

		//给支付宝发送请求进行支付操作
		return alipayClient.pageExecute(alipayRequest).getBody();
	}

	//服务器异步通知页面路径
	@RequestMapping("pay/notify.html")
	@ResponseBody
	public String payNotify(HttpServletRequest request) throws Exception {
		// 获取支付宝POST过来反馈信息
		Map<String, String> params = new HashMap<String, String>();
		Map<String, String[]> requestParams = request.getParameterMap();
		for (Iterator<String> iter = requestParams.keySet().iterator(); iter.hasNext();) {
			String name = (String) iter.next();
			String[] values = (String[]) requestParams.get(name);
			String valueStr = "";
			for (int i = 0; i < values.length; i++) {
				valueStr = (i == values.length - 1) ? valueStr + values[i] : valueStr + values[i] + ",";
			}
			// 乱码解决，这段代码在出现乱码时使用
			valueStr = new String(valueStr.getBytes("ISO-8859-1"), "utf-8");
			params.put(name, valueStr);
		}
		boolean signVerified = AlipaySignature.rsaCheckV1(params, AlipayConfig.alipay_public_key, AlipayConfig.charset,
				AlipayConfig.sign_type); // 调用SDK验证签名
		// ——请在这里编写您的程序（以下代码仅作参考）——
		/*
		 * 实际验证过程建议商户务必添加以下校验： 1、需要验证该通知数据中的out_trade_no是否为商户系统中创建的订单号，
		 * 2、判断total_amount是否确实为该订单的实际金额（即商户订单创建时的金额）， 3、校验通知中的seller_id（或者seller_email)
		 * 是否为out_trade_no这笔单据的对应的操作方（有的时候，一个商户可能有多个seller_id/seller_email）
		 * 4、验证app_id是否为该商户本身。
		 */
		if (signVerified) {// 验证成功
			// 商户订单号
			// String out_trade_no = new
			// String(request.getParameter("out_trade_no").getBytes("ISO-8859-1"),"UTF-8");

			// 支付宝交易号
			// String trade_no = new
			// String(request.getParameter("trade_no").getBytes("ISO-8859-1"),"UTF-8");

			// 交易状态
			String trade_status = new String(request.getParameter("trade_status").getBytes("ISO-8859-1"), "UTF-8");
			if (trade_status.equals("TRADE_FINISHED")) {
				// 判断该笔订单是否在商户网站中已经做过处理
				// 如果没有做过处理，根据订单号（out_trade_no）在商户网站的订单系统中查到该笔订单的详细，并执行商户的业务程序
				// 如果有做过处理，不执行商户的业务程序
				// 注意：
				// 退款日期超过可退款期限后（如三个月可退款），支付宝系统发送该交易状态通知
			} else if (trade_status.equals("TRADE_SUCCESS")) {
				// 判断该笔订单是否在商户网站中已经做过处理
				// 如果没有做过处理，根据订单号（out_trade_no）在商户网站的订单系统中查到该笔订单的详细，并执行商户的业务程序
				// 如果有做过处理，不执行商户的业务程序
				// 注意：
				// 付款完成后，支付宝系统发送该交易状态通知
			}
			return "success";
		} else {// 验证失败
			return "fail";
			// 调试用，写文本函数记录程序运行情况是否正常
			// String sWord = AlipaySignature.getSignCheckContentV1(params);
			// AlipayConfig.logResult(sWord);
		}
	}

	//页面跳转同步通知页面路径
	@RequestMapping("pay/return.html")
	public String payReturn(HttpServletRequest request,Model model) throws Exception {
		// 获取支付宝GET过来反馈信息
		Map<String, String> params = new HashMap<String, String>();
		Map<String, String[]> requestParams = request.getParameterMap();
		for (Iterator<String> iter = requestParams.keySet().iterator(); iter.hasNext();) {
			String name = (String) iter.next();
			String[] values = (String[]) requestParams.get(name);
			String valueStr = "";
			for (int i = 0; i < values.length; i++) {
				valueStr = (i == values.length - 1) ? valueStr + values[i] : valueStr + values[i] + ",";
			}
//			// 乱码解决，这段代码在出现乱码时使用
//			valueStr = new String(valueStr.getBytes("ISO-8859-1"), "utf-8");
			params.put(name, valueStr);
		}
		boolean signVerified = AlipaySignature.rsaCheckV1(params, AlipayConfig.alipay_public_key, AlipayConfig.charset,
				AlipayConfig.sign_type); // 调用SDK验证签名
		// ——请在这里编写您的程序（以下代码仅作参考）——
		if (signVerified) {
			// 商户订单号
			String out_trade_no = new String(request.getParameter("out_trade_no").getBytes("ISO-8859-1"), "UTF-8");
			// 支付宝交易号
			String trade_no = new String(request.getParameter("trade_no").getBytes("ISO-8859-1"), "UTF-8");
			// 付款金额
			String total_amount = new String(request.getParameter("total_amount").getBytes("ISO-8859-1"), "UTF-8");
			//去掉小数点
			Long money = Long.valueOf(total_amount.split("\\.")[0]);
			HttpSession session = request.getSession();
			//设置更新信息

			ProjectVO projectVO = new ProjectVO();
			ProjectDetailPO projectDetailPO = (ProjectDetailPO) session.getAttribute("ProjectDetailPO");
			projectVO.setId(projectDetailPO.getProjectPO().getId());
			//判断是否众筹成功
			//众筹已结束
			if(projectDetailPO.getProjectPO().getSupportmoney()>=projectDetailPO.getProjectPO().getMoney()){
				projectVO.setStatus(CrowdConstant.PROJECT_STATUS_SUCCESS_CROWD);
			}else{    //没结束
				//刚好结束
				if(projectDetailPO.getProjectPO().getSupportmoney()+money>=projectDetailPO.getProjectPO().getMoney()){
					projectVO.setStatus(CrowdConstant.PROJECT_STATUS_SUCCESS_CROWD);
				}
				projectVO.setSupporter(projectDetailPO.getProjectPO().getSupporter()+1);
				projectVO.setSupportmoney(projectDetailPO.getProjectPO().getSupportmoney()+money);
			}

			//更新工程信息
			ResultEntity<ProjectDetailPO> resultEntity = projectRemoteService.updateProject(projectVO);
			if(ResultEntity.FAILED.equals(resultEntity.getResult())){
				model.addAttribute(CrowdConstant.ATTR_NAME_MESSAGE,resultEntity.getMessage());
				return "error";
			}


			//更新订单状态
			//redis中取出订单信息
			ResultEntity<String> resultEntity1 = redisService.retrieveStringValueByStringKey(out_trade_no);
			if(ResultEntity.FAILED.equals(resultEntity1.getResult())){
				model.addAttribute(CrowdConstant.ATTR_NAME_MESSAGE,resultEntity1.getMessage());
				return "error";
			}
			if(resultEntity1.getData()==null){
				model.addAttribute(CrowdConstant.ATTR_NAME_MESSAGE,CrowdConstant.MESSAGE_ORDER_NOT_EXISTS);
				return "error";
			}
			//从Redis中查询到对象字符串
			String orderVOString = resultEntity1.getData();
			//json转对象
			OrderVO orderVO = JSON.parseObject(orderVOString, OrderVO.class);
			//更新订单状态
			orderVO.setStatus(CrowdConstant.ORDER_STATUS_SUCCESS_PAY.toString());
			ResultEntity<String> resultEntity2 = memmberRemoteService.updateOrder(orderVO);
			if(ResultEntity.FAILED.equals(resultEntity2.getResult())){
				model.addAttribute(CrowdConstant.ATTR_NAME_MESSAGE,CrowdConstant.MESSAGE_ORDER_UPDATE_FAILED);
				return "error";
			}
			//redis中取消该订单
			redisService.removeByKey(out_trade_no);

			session.setAttribute("ProjectDetailPO",resultEntity.getData());
			return "redirect:/member/minecrowdfunding.html";
//			return "trade_no:" + trade_no + "<br/>out_trade_no:" + out_trade_no + "<br/>total_amount:" + total_amount;
		} else {
			return "验签失败";
		}
	}



}
