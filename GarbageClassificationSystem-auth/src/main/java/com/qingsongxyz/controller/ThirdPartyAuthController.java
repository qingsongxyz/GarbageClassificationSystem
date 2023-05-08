package com.qingsongxyz.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.img.ImgUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.qrcode.QrCodeUtil;
import cn.hutool.extra.qrcode.QrConfig;
import cn.hutool.http.HttpStatus;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayApiException;
import com.alipay.api.response.AlipaySystemOauthTokenResponse;
import com.alipay.api.response.AlipayUserInfoShareResponse;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.qingsongxyz.dto.UserSerializableDTO;
import com.qingsongxyz.pojo.CommonResult;
import com.qingsongxyz.pojo.Message;
import com.qingsongxyz.service.feignService.PushThirdPartyMessageFeignService;
import com.qingsongxyz.service.feignService.UserFeignService;
import com.qingsongxyz.util.AlipayHttpClient;
import com.qingsongxyz.util.LoginHtmlUtil;
import com.qingsongxyz.util.WechatHttpClient;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotBlank;
import java.io.*;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.qingsongxyz.constant.RedisConstant.*;

/**
 * <p>
 * 三方认证 前端控制器
 * </p>
 *
 * @author qingsongxyz
 * @since 2023-2-12
 */
@RefreshScope
@Slf4j
@RequestMapping("/third-party")
@RestController
public class ThirdPartyAuthController {

    /*
      微信扫码认证参数
     */
    @Value("${oauth.wechat.appid}")
    public String wechat_appid;

    @Value("${oauth.wechat.app-secret}")
    public String wechat_app_secret;

    @Value("${oauth.wechat.redirect-uri}")
    public String wechat_redirect_url;

    /*
      支付宝扫码认证参数
     */
    @Value("${oauth.alipay.appid}")
    public String aliyun_appid;

    @Value("${oauth.alipay.redirect-uri}")
    public String aliyun_redirect_uri;

    @Value("${oauth.alipay.private-key}")
    public String aliyun_private_key;

    @Value("${oauth.alipay.public-key}")
    public String aliyun_public_key;

    private final RestTemplate restTemplate;

    private final StringRedisTemplate stringRedisTemplate;

    private final UserFeignService userFeignService;

    private final PushThirdPartyMessageFeignService pushThirdPartyMessageFeignService;

    public ThirdPartyAuthController(RestTemplate restTemplate, StringRedisTemplate stringRedisTemplate, @Qualifier("userFeignService") UserFeignService userFeignService, @Qualifier("pushThirdPartyMessageFeignService") PushThirdPartyMessageFeignService pushThirdPartyMessageFeignService) {
        this.restTemplate = restTemplate;
        this.stringRedisTemplate = stringRedisTemplate;
        this.userFeignService = userFeignService;
        this.pushThirdPartyMessageFeignService = pushThirdPartyMessageFeignService;
    }

    @ApiOperation("测试验证微信token")
    @ApiResponses({
            @ApiResponse(code = 200, message = "请求成功"),
            @ApiResponse(code = 400, message = "参数有误"),
            @ApiResponse(code = 401, message = "没有认证"),
            @ApiResponse(code = 403, message = "没有权限"),
            @ApiResponse(code = 500, message = "请求失败")
    })
    @GetMapping("/check")
    public String check(HttpServletRequest request) {
        String signature = request.getParameter("signature");
        String timestamp = request.getParameter("timestamp");
        String nonce = request.getParameter("nonce");
        String echostr = request.getParameter("echostr");

        if (WechatHttpClient.checkSignature(signature, timestamp, nonce)) {
            return echostr;
        }
        return "";
    }

    @ApiOperation("测试获取微信认证二维码")
    @ApiImplicitParam(name = "id", value = "唯一标识", required = true, paramType = "query", dataTypeClass = String.class, example = "1")
    @ApiResponses({
            @ApiResponse(code = 200, message = "请求成功"),
            @ApiResponse(code = 400, message = "参数有误"),
            @ApiResponse(code = 401, message = "没有认证"),
            @ApiResponse(code = 403, message = "没有权限"),
            @ApiResponse(code = 500, message = "请求失败")
    })
    @GetMapping("/wechat/auth")
    public CommonResult getQRCode(@NotBlank(message = "唯一标识不能为空") String id) throws IOException {
        stringRedisTemplate.opsForValue().set(WECHAT_STATE_KEY + ":" + id, id, WECHAT_STATE_SECOND_TTL, TimeUnit.SECONDS);

        //拼接获取授权码url
        String url = "https://open.weixin.qq.com/connect/oauth2/authorize?" +
                "appid=" + wechat_appid +
                "&redirect_uri=" + URLEncoder.encode(wechat_redirect_url, "UTF-8") +
                "&response_type=code" +
                "&scope=" + "snsapi_userinfo" +
                "&state=" + id +
                "&forcePopup=true#wechat_redirect";

        //输出二维码base64编码
        String base64Str = QrCodeUtil.generateAsBase64(url,
                QrConfig.create()
                        .setImg(ImageIO.read(new URL("http://garbage-bucket.oss-cn-shanghai.aliyuncs.com/gcs/wechat.png"))) //设置logo
                        .setErrorCorrection(ErrorCorrectionLevel.H), // 高纠错级别
                ImgUtil.IMAGE_TYPE_PNG);
        return CommonResult.ok(base64Str, "获取微信认证二维码成功!");
    }

    @ApiOperation("测试获取微信用户信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "code", value = "授权码", required = true, paramType = "query", dataTypeClass = String.class, example = "1"),
            @ApiImplicitParam(name = "state", value = "唯一标识", required = true, paramType = "query", dataTypeClass = String.class, example = "1")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "请求成功"),
            @ApiResponse(code = 400, message = "参数有误"),
            @ApiResponse(code = 401, message = "没有认证"),
            @ApiResponse(code = 403, message = "没有权限"),
            @ApiResponse(code = 500, message = "请求失败")
    })
    @GetMapping(value = "/oauth/callback/wechat")
    public String wechatCallback(String code, String state) {
        String s = stringRedisTemplate.opsForValue().get(WECHAT_STATE_KEY + ":" + state);
        //1.验证state存在 防止CSRF攻击
        if (StrUtil.isBlank(s)) {
            return LoginHtmlUtil.getHtml(false);
        }

        try {
            //2.通过code换取access_token和openid
            String url = "https://api.weixin.qq.com/sns/oauth2/access_token?" +
                    "appid=" + wechat_appid +
                    "&secret=" + wechat_app_secret +
                    "&code=" + code +
                    "&grant_type=authorization_code";
            String result = restTemplate.getForObject(url, String.class);
            JSONObject tokenObject = JSON.parseObject(result);
            String access_token = tokenObject.getString("access_token");
            String openid = tokenObject.getString("openid");
            log.info("ThirdPartyAuthController wechatCallback access_token:{}, openid:{}", access_token, openid);

            //创建发送消息对象
            Message<UserSerializableDTO> message = new Message<>();
            message.setSource("server");
            message.setTarget(s);
            message.setType(Message.MessageType.THIRD_PARTY_REGISTER.toString());
            message.setAck(false);
            message.setTime(LocalDateTime.now());

            //3.判断是否存在该用户
            CommonResult res = userFeignService.getUserByOpenid(openid);
            if(res.getCode() == HttpStatus.HTTP_UNAVAILABLE){
                return LoginHtmlUtil.getSystemErrorHtml();
            }

            Object data = res.getData();
            if (ObjectUtil.isNotNull(data) && data instanceof Map) {
                UserSerializableDTO userDTO = BeanUtil.fillBeanWithMap((Map) data, new UserSerializableDTO(), CopyOptions.create().ignoreNullValue().ignoreError());
                message.setType(Message.MessageType.THIRD_PARTY_SUCCESS.toString());
                message.setContent(userDTO);
            } else {
                //4.获取用户信息
                url = "https://api.weixin.qq.com/sns/userinfo?" +
                        "access_token=" + access_token +
                        "&openid=" + openid +
                        "&lang=zh_CN";
                result = restTemplate.getForObject(url, String.class);
                if (ObjectUtil.isNotNull(result)) {
                    result = new String(result.getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
                    JSONObject userObject = JSON.parseObject(result);
                    String nickName = userObject.getString("nickname");
                    String headimgurl = userObject.getString("headimgurl");

                    log.info("ThirdPartyAuthController wechatCallback nickName:{}, headimgurl:{}", nickName, headimgurl);

                    UserSerializableDTO user = new UserSerializableDTO();
                    user.setUsername(nickName);
                    user.setImage(headimgurl);
                    user.setOpenid(openid);
                    message.setContent(user);
                }
            }

            //发送消息到消息队列
            CommonResult r = pushThirdPartyMessageFeignService.pushThirdPartyMessage(message);
            if(r.getCode() == HttpStatus.HTTP_UNAVAILABLE){
                return LoginHtmlUtil.getSystemErrorHtml();
            }
        } catch (RestClientException e) {
            log.error("ThirdPartyAuthController wechatCallback error:{}", e.getMessage());
            return LoginHtmlUtil.getHtml(false);
        }
        return LoginHtmlUtil.getHtml(true);
    }

    @ApiOperation("测试获取支付宝扫码界面url")
    @ApiImplicitParam(name = "id", value = "唯一标识", required = true, paramType = "query", dataTypeClass = String.class, example = "1")
    @ApiResponses({
            @ApiResponse(code = 200, message = "请求成功"),
            @ApiResponse(code = 400, message = "参数有误"),
            @ApiResponse(code = 401, message = "没有认证"),
            @ApiResponse(code = 403, message = "没有权限"),
            @ApiResponse(code = 500, message = "请求失败")
    })
    @GetMapping(value = "/alipay/auth")
    public CommonResult getUrl(@NotBlank(message = "唯一标识不能为空") String id) throws UnsupportedEncodingException {
        //用于第三方应用防止CSRF攻击
        stringRedisTemplate.opsForValue().set(ALIPAY_STATE_KEY + ":" + id, id, ALIPAY_STATE_SECOND_TTL, TimeUnit.SECONDS);

        //获取Authorization Code
        //https://openauth.alipay.com/oauth2/publicAppAuthorize.htm?app_id=2021003145652060&scope=auth_user&redirect_uri=http://5b2675k660.wicp.vip/oauth/callback/alipay
        String url = "https://openauth.alipay.com/oauth2/publicAppAuthorize.htm?scope=auth_user" +
                "&app_id=" + aliyun_appid +
                "&redirect_uri=" + URLEncoder.encode(aliyun_redirect_uri, "UTF-8") +
                "&state=" + id;
        return CommonResult.ok(url, "获取支付宝扫码界面url成功!");
    }

    @ApiOperation("测试支付宝扫码认证")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "auth_code", value = "授权码", required = true, paramType = "query", dataTypeClass = String.class, example = "1"),
            @ApiImplicitParam(name = "state", value = "唯一标识", required = true, paramType = "query", dataTypeClass = String.class, example = "1")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "请求成功"),
            @ApiResponse(code = 400, message = "参数有误"),
            @ApiResponse(code = 401, message = "没有认证"),
            @ApiResponse(code = 403, message = "没有权限"),
            @ApiResponse(code = 500, message = "请求失败")
    })
    @GetMapping(value = "/oauth/callback/alipay")
    public CommonResult alipayCallback(String auth_code, String state) {
        String s = stringRedisTemplate.opsForValue().get(ALIPAY_STATE_KEY + ":" + state);

        //1.验证state是否存在
        if (StrUtil.isBlank(s)) {
            return CommonResult.failure(HttpStatus.HTTP_UNAUTHORIZED, "支付宝扫码认证失败!!!");
        }

        try {
            //2.通过auth_code获取Access Token 以及 user_id
            AlipaySystemOauthTokenResponse tokenResponse = AlipayHttpClient.getAccessToken(aliyun_appid, aliyun_private_key, aliyun_public_key, auth_code);
            //支付宝用户唯一标识
            String userId = tokenResponse.getUserId();

            //创建发送消息对象
            Message<UserSerializableDTO> message = new Message<>();
            message.setSource("server");
            message.setTarget(s);
            message.setType(Message.MessageType.THIRD_PARTY_REGISTER.toString());
            message.setAck(false);
            message.setTime(LocalDateTime.now());

            //3.判断该用户是否存在
            CommonResult res = userFeignService.getUserByAlipayid(userId);
            if(res.getCode() == HttpStatus.HTTP_UNAVAILABLE){
                return res;
            }

            Object data = res.getData();
            if (ObjectUtil.isNotNull(data) && data instanceof Map) {
                UserSerializableDTO userDTO = BeanUtil.fillBeanWithMap((Map) data, new UserSerializableDTO(), CopyOptions.create().ignoreNullValue().ignoreError());
                message.setType(Message.MessageType.THIRD_PARTY_SUCCESS.toString());
                message.setContent(userDTO);
            } else {
                //4.通过auth_code获取用户信息
                AlipayUserInfoShareResponse response = AlipayHttpClient.getUserInfo(aliyun_appid, aliyun_private_key, aliyun_public_key, tokenResponse.getAccessToken());
                String body = response.getBody();
                log.info("ThirdPartyAuthController alipayCallback body:{}", body);
                // 请求成功
                if ("10000".equals(response.getCode())) {
                    String avatar = response.getAvatar();
                    String nickName = response.getNickName();
                    log.info("ThirdPartyAuthController alipayCallback userId:{}, avatar:{}, nickName:{}", userId, avatar, nickName);

                    UserSerializableDTO user = new UserSerializableDTO();
                    user.setUsername(nickName);
                    user.setImage(avatar);
                    user.setAlipayid(userId);
                    message.setContent(user);
                }
            }
            //发送用户消息给前端
            CommonResult result = pushThirdPartyMessageFeignService.pushThirdPartyMessage(message);
            if(result.getCode() == HttpStatus.HTTP_UNAVAILABLE){
                return result;
            }
        } catch (AlipayApiException e) {
            log.error("ThirdPartyAuthController alipayCallback error:{}", e.getMessage());
            return CommonResult.failure(HttpStatus.HTTP_INTERNAL_ERROR, "支付宝扫码认证失败!!!");
        }
        return CommonResult.ok("支付宝扫码认证成功!");
    }
}
