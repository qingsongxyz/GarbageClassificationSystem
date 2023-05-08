package com.qingsongxyz.pojo;

/**
 * 发送信息枚举类
 */
public enum LetterType {

    CAPTCHA(1, "验证码");

    private int id;

    private String name;

    LetterType(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "LetterType{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
