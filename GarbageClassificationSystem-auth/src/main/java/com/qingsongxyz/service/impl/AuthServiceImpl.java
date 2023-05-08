package com.qingsongxyz.service.impl;

import com.qingsongxyz.pojo.CaptchaResult;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.endpoint.TokenEndpoint;
import org.springframework.stereotype.Service;
import org.springframework.web.HttpRequestMethodNotSupportedException;

import java.security.Principal;
import java.util.Map;

@Service
public class AuthServiceImpl {

    private final TokenEndpoint tokenEndpoint;

    public AuthServiceImpl(TokenEndpoint tokenEndpoint) {
        this.tokenEndpoint = tokenEndpoint;
    }

    public OAuth2AccessToken postAccessToken(Principal principal, Map<String, String> parameters) throws HttpRequestMethodNotSupportedException {
        return tokenEndpoint.postAccessToken(principal, parameters).getBody();
    }

    public OAuth2AccessToken postAccessTokenWithCaptchaResult(CaptchaResult captchaResult, Principal principal, Map<String, String> parameters) throws HttpRequestMethodNotSupportedException {
        return tokenEndpoint.postAccessToken(principal, parameters).getBody();
    }
}
