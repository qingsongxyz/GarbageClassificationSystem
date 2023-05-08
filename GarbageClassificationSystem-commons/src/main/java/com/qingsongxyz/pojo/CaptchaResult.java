package com.qingsongxyz.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 极验二次验证对象
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CaptchaResult {

    private String lot_number;

    private String captcha_output;

    private String pass_token;

    private String gen_time;
}
