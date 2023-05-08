package com.qingsongxyz.template;

/**
 * 发送模板类
 */
public class Template {

    /**
     * 生成验证码邮件信息
     * @param username 用户名
     * @param code 验证码
     * @return 验证码邮件信息
     */
    public static String generateCaptchaTemplate(String username, String code) {

        return  "<!DOCTYPE html>\n" +
                "<html lang=\"en\">\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <title>验证码</title>\n" +
                "    <style>\n" +
                "        div{\n" +
                "            margin-top: 10px;\n" +
                "            margin-bottom: 20px;\n" +
                "            font-size: 15px;\n" +
                "            font-weight: bolder;\n" +
                "        }\n" +
                "\n" +
                "        span{\n" +
                "            font-size: 18px;\n" +
                "            color: tomato;\n" +
                "        }\n" +
                "\n" +
                "        p{\n" +
                "            margin-top: 10px;\n" +
                "            font-size: 12px;\n" +
                "            color: darkgrey;\n" +
                "        }\n" +
                "    </style>\n" +
                "</head>\n" +
                "<body>\n" +
                "    <h3>尊敬的" + username + ": 你好</h3>\n" +
                "    <div>你的验证码为: <span>" + code + "</span> , 有效期为2分钟, 请尽快验证!</div>\n" +
                "    <hr>\n" +
                "    <p>此为系统邮件,请勿回复</p>\n" +
                "    <p>请勿将验证码泄露给其他人</p>\n" +
                "    <p>垃圾分类回收系统</p>\n" +
                "</body>\n" +
                "</html>";
    }
}
