package com.qingsongxyz.constant;

public class SecurityConstant {

    /**
     * 认证请求头key
     */
    public static final String AUTHORIZATION_KEY = "Authorization";

    /**
     * JWT令牌前缀
     */
    public static final String JWT_PREFIX = "Bearer ";

    /**
     * Basic认证前缀
     */
    public static final String BASIC_PREFIX = "Basic ";

    /**
     * JWT存储权限前缀
     */
    public static final String AUTHORITY_PREFIX = "ROLE_";

    /**
     * JWT载体key
     */
    public static final String JWT_PAYLOAD_KEY = "payload";

    /**
     * JWT存储权限属性
     */
    public static final String JWT_AUTHORITIES_KEY = "authorities";

    /**
     * 不加密密码前缀
     */
    public static final String PASSWORD_NOOP_PREFIX = "{noop}";

    /**
     * Bcrypt密码加密前缀
     */
    public static final String PASSWORD_BCRYPT_PREFIX = "{bcrypt}";

}
