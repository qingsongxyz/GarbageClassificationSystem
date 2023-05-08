package com.qingsongxyz.util;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipaySystemOauthTokenRequest;
import com.alipay.api.request.AlipayUserInfoShareRequest;
import com.alipay.api.response.AlipaySystemOauthTokenResponse;
import com.alipay.api.response.AlipayUserInfoShareResponse;

/**
 * 阿里云扫码认证工具类
 */
public class AlipayHttpClient {

    /**
     * auth_code换取access_token与user_id
     *
     * @param appid
     * @param privateKey 私钥
     * @param publicKey  公钥
     * @param authCode   授权码
     * @return
     * @throws AlipayApiException
     */
    public static AlipaySystemOauthTokenResponse getAccessToken(String appid, String privateKey, String publicKey, String authCode) throws AlipayApiException {
        /*
         * 支付宝网关（固定）
         * APPID 即创建应用后生成
         * 开发者私钥，由开发者自己生成
         * 参数返回格式，只支持json
         * 编码集，支持GBK/UTF-8
         * 支付宝公钥，由支付宝生成
         * 商户生成签名字符串所使用的签名算法类型，目前支持RSA2和RSA，推荐使用RSA2
         */
        AlipayClient alipayClient = new DefaultAlipayClient("https://openapi.alipay.com/gateway.do", appid, privateKey, "json", "UTF-8", publicKey, "RSA2");
        // 请求对象
        AlipaySystemOauthTokenRequest request = new AlipaySystemOauthTokenRequest();
        // 第一步获取到的：auth_code
        request.setCode(authCode);
        // 授权类型
        request.setGrantType("authorization_code");
        // 发起请求
        return alipayClient.execute(request);
    }

    /**
     * 使用 access_token 获取用户信息
     *
     * @param appid
     * @param privateKey  私钥
     * @param publicKey   公钥
     * @param accessToken 令牌
     * @throws AlipayApiException
     */
    public static AlipayUserInfoShareResponse getUserInfo(String appid, String privateKey, String publicKey, String accessToken) throws AlipayApiException {
        /*
          支付宝网关（固定）
          APPID 即创建应用后生成
          开发者私钥，由开发者自己生成
          参数返回格式，只支持json
          编码集，支持GBK/UTF-8
          支付宝公钥，由支付宝生成
         */
        AlipayClient alipayClient = new DefaultAlipayClient("https://openapi.alipay.com/gateway.do", appid, privateKey, "json", "UTF-8", publicKey, "RSA2");
        // 请求对象
        AlipayUserInfoShareRequest request = new AlipayUserInfoShareRequest();
        // 传入token，发起请求
        return alipayClient.execute(request, accessToken);
    }
}
