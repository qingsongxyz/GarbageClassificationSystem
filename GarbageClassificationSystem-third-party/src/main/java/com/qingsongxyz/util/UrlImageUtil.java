package com.qingsongxyz.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * 网络图片工具类
 */
public class UrlImageUtil {

    /**
     * 读取网络图片输入流
     * @param url 图片url
     * @return 输入流
     * @throws IOException IO异常
     */
    public static InputStream getUrlImage(String url) throws IOException {
        //如果路径中包含特殊符号或者空格会出错
        //先将地址本身带有的%转为%25 再将空格转换为%20
        url = url.replaceAll("%", "%25").replaceAll(" ", "%20");
        URL u = new URL(url);
        HttpURLConnection connection = (HttpURLConnection) u.openConnection();
        connection.addRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:107.0) Gecko/20100101 Firefox/107.0");
        connection.setConnectTimeout(10 * 1000);
        connection.setReadTimeout(15 * 1000);
        return connection.getInputStream();
    }
}
