package com.qingsongxyz.constant;

public class RedisConstant {

    //微信防止CSRF攻击键前缀
    public static final String WECHAT_STATE_KEY = "wechat_state";

    //微信防止CSRF攻击键过期时间
    public static final int WECHAT_STATE_SECOND_TTL = 120;

    //支付宝防止CSRF攻击键前缀
    public static final String ALIPAY_STATE_KEY = "alipay_state";

    //支付宝防止CSRF攻击键过期时间
    public static final int ALIPAY_STATE_SECOND_TTL = 120;

    //垃圾信息键前缀、过期时间
    public static final String GARBAGE_LIST_KEY = "garbageList";

    public static final long GARBAGE_LIST_KEY_TTL_SECOND = 120;

    //垃圾分类键、过期时间
    public static final String GARBAGE_CATEGORY_LIST_KEY = "garbageCategoryList";

    public static final long GARBAGE_CATEGORY_LIST_KEY_TTL_SECOND = 300;

    //用户排行榜键
    public static final String USER_RANKING_LIST = "userRankingList";

    //商品库存键前缀
    public static final String STORAGE_LIST_KEY = "storageList";

    //用户签到键前缀
    public static final String USER_SIGN_IN = "userSignIn";

    //验证码键前缀、过期时间
    public static final String CAPTCHA_KEY = "captcha";

    public static final int CAPTCHA_TTL_SECOND = 120;

    //聊天在线用户集合键前缀
    public static final String ONLINE_CHAT_USERS = "onlineChatUsers";

    //角色权限集合键
    public static final String ROLE_PATH_KEY = "role_path";
}
