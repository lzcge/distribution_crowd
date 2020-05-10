package com.lzcge.crowd.config;

/* *
 *类名：AlipayConfig
 *功能：基础配置类
 *详细：设置帐户有关信息及返回路径
 *修改日期：2017-04-05
 *说明：
 *以下代码只是为了方便商户测试而提供的样例代码，商户可以根据自己网站的需要，按照技术文档编写,并非一定要使用该代码。
 *该代码仅供学习和研究支付宝接口使用，只是提供一个参考。
 */

public class AlipayConfig {
	
//↓↓↓↓↓↓↓↓↓↓请在这里配置您的基本信息↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓

	// 应用ID,您的APPID，收款账号既是您的APPID对应支付宝账号
	public static String app_id = "2016102300744870";
	
	// 商户私钥，您的PKCS8格式RSA2私钥
    public static String merchant_private_key = "MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQCofmzu+7C2WeQBUUIaIZqinn73ut8o0N66zxq3xv4wCfVOreyy77irkbtBP6xtt/LiQ1XhI8m7+LUrF0kt8dfLnQCnq4uv1mKkmVIdUbVIO60LqOUOxS/FC6ZkwH7ltOSoDE8x10fM4KZ1UPLsMDDURzQ2b5NPz90X5x2vNwJDFP+7LfjUSk569xR0W3yz5nsxPF0hF8w9DsC814lkrSCnedDvUJgNcevoAO7UiL9lwIXNTQG2gtKNtRkpf6FYjhWMeyeKSYX9+HgMOj565zpkRK9h0NY+4xSP0K0iizI37xKqhxFHYSqdiu7RyUJ2AGLLKjTcKoMGIPLJ+r8lF7/BAgMBAAECggEAauNg6a6DaPrXeLp6y6b7uSQA+djzjqoKUcnmciVrxT/b/aC6Ir2OklCt0PO6yBPEoiMDTQLuDC/3hjGMe/gK5KFh8/wQbmj7RN36yJy3+u3Ylif7v4X6Cu+cDBhNqB08XRsJ9l1SsDbngk7q/EiKAXB2P/5CHoSIBozYOPoVlkWn223lcXgK1WcS3UOnCBTXQVJpfMYkim/x77VQA1YGIkXyXdXNtaKWFh8mmfq9V5PH5BdUqCWWSoIwjQuPt6njMwIak2nKjkypZ34+JNsUqqIci/DEsELYlnKjHeM9AgRizuLbxnnDBvLB7AUnShJQ3h7Rgen40zvEZBM0eN3/UQKBgQDoMnVnBc6Fd3D8P87bd1PkHQj+KZSj1y+HxwEassRMbJIR2qyCeE8wp18jN0ssktL4NNivpw89TCe1SbollaqbfQ/w5WZTQ1I29ZJwuOca+OR7vXTeC+1yBM20OESE1ZQ+6GhZqiWvBQ5NyA+Yky6pR8sEGNxsgLWbyqI4vZtaxQKBgQC5xDRTIGzYWwqle2BchSC13ZqPcBqgF0Yn6YVgTP/X0XA0vYkGUksunavMA38uIj+q0e10DzQlMbycq3ysAqu+5WXYvQKwI6itDdC/XGGQDIOU9jQTurF0DtGURE2qr4TMT+fcm07SD8v52xZoHBY74MikCKkik42qEE/03TDQzQKBgQDYDFMm18VQdFFhtDFSDVa2crmyW5gdVpS4HleNsvZpMcA78p7/CtiMKb+g8cr1n2vDXNCdp7DLVTfBO+eDHx5ObPBPMfOi14oA70AhuDn448scw6yhCrIVnBiWbqB6hIvaeL7XZzSHBApKHzM7wP7yrYVdu5AlOSFB4ypL50hNDQKBgDIyV2VC9MZ9y2Gt9frqKk2aZQJva3Jo95cfXeAIznVV7bkCoj0oDZK6HksRgtNHg+a3tS9QUGngh7PaDuU4D4qU2DF52XkZGaIxgn/DCcXv67pb0PSn1P2kZ3NmBYqixQKkcOXGvcS7agXI8sQNGuDxO6Y/hNyH/JbAs6pZ/q/dAoGBAM818jNkKjvOYX3vg5MWdnn9Z1QLiQG+6r6f21TBs/hsluNrwCgw7nhYqv8rT790fsROu4jY+cXwZPKwpb1UHu+H5b/l6/Nkg1aW6vXB+MQcUZx4GxLdbPdKpC/JOmIaCiT22co1kiKDfIOzCA2yVuxoOimSqe8y5pvFcCyWSqxB";
	
	// 支付宝公钥,查看地址：https://openhome.alipay.com/platform/keyManage.htm 对应APPID下的支付宝公钥。
    public static String alipay_public_key = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAl3IsKeUMSs8gl4gSeZTVBWqosI2oWwSrsCRBR+tvMdHF238AsjZKcvH0icDu5zSuBvv4IsLSn1frbKQ+rleeq0oO6m8qSE8nvVID2kkaptteQR3kYjZWkcHF0nM4Nz5CzuzAEunN4PmHaJELNgLaCbkigZivfL48A3HuZv2PYTgOX1/M6jRbd4jId7iISlV4Gow7EmP7GfWZtJxbZAQV4yyEMjHNPAMeyIeFFsL9jscUzmQr0ae5yDzXMl4ke4zPtBPnNgAlXFH4bcCTbMdAl8Id97EVyER1Zzgt0YUIx0mGbEd4UPBTf7i3Gjl0Vs7YfgBPFQUvA58PSGZ30iRaMQIDAQAB";

	// 服务器异步通知页面路径  需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
	public static String notify_url = "http://fj6uay.natappfree.cc/pay/notify.html";

	// 页面跳转同步通知页面路径 需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
	public static String return_url = "http://fj6uay.natappfree.cc/pay/return.html";

	// 签名方式
	public static String sign_type = "RSA2";
	
	// 字符编码格式
	public static String charset = "utf-8";
	
	// 支付宝网关,正式的和沙箱的不同
	public static String gatewayUrl = "https://openapi.alipaydev.com/gateway.do";
	
}

