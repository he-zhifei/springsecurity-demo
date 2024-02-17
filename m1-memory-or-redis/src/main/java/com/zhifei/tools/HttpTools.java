package com.zhifei.tools;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * Http工具类
 *
 * @author He Zhifei
 * @date 2021/11/30 17:11
 */
public class HttpTools {

    private static final Logger LOGGER = LoggerFactory.getLogger(HttpTools.class);

    /**
     * 默认字符集编码
     */
    private static final String CHARSET = StandardCharsets.UTF_8.name();

    /**
     * 获取HttpServletRequest
     *
     * @return HttpServletRequest
     */
    public static HttpServletRequest getRequest() {
        return ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
    }

    /**
     * 判断ip是否为空或者"unknown"
     * @param ip
     * @return
     */
    private static boolean isBlankOrUnknownIp(String ip) {
        return ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip);
    }

    /**
     * 获取请求ip
     *
     * @return 请求ip
     */
    public static String getIp() {
        return getIp(getRequest());
    }

    /**
     * 获取请求ip
     *
     * @param request 请求
     * @return 请求ip
     */
    public static String getIp(HttpServletRequest request) {
        if (request == null) {
            request = getRequest();
        }
        if (request == null) {
            return "unknown";
        }
        String ip = request.getHeader("x-forwarded-for");
        if (isBlankOrUnknownIp(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (isBlankOrUnknownIp(ip)) {
            ip = request.getHeader("X-Forwarded-For");
        }
        if (isBlankOrUnknownIp(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (isBlankOrUnknownIp(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (isBlankOrUnknownIp(ip)) {
            ip = request.getRemoteAddr();
        }
        return "0:0:0:0:0:0:0:1".equals(ip) ? "127.0.0.1" : ip;
    }

    /**
     * 生成Basic Authorization
     *
     * @param username 用户名
     * @param password 密码
     * @return base64加密串
     */
    public static String generateBasicAuthorization(String username, String password) throws UnsupportedEncodingException {
        return "Basic " + new String(Base64.getEncoder().encode((username + ":" + password).getBytes(CHARSET)), CHARSET);
    }

    /**
     * 把对象转成json字符串回写给页面
     * @param response 响应对象
     * @param object 待回写的对象
     */
    public static void writeJson(ServletResponse response, Object object) {
        response.setContentType("application/json;charset=" + CHARSET);
        try (
                PrintWriter writer = response.getWriter();
        ) {
            writer.write(object instanceof String ? (String) object : JacksonTools.toJson(object));
            writer.flush();
        } catch (IOException e) {
            LOGGER.error("HttpTools.writeJson error: " + object, e);
        }
    }

    /**
     * 回写html，指定字符编码
     * @param response 响应对象
     * @param content html文本
     * @param charset 字符集
     */
    public static void writeHtml(ServletResponse response, String content, String charset) {
        response.setContentType("text/html;charset=" + charset);
        try (
                PrintWriter writer = response.getWriter();
        ) {
            writer.write(content);
            writer.flush();
        } catch (IOException e) {
            LOGGER.error("HttpTools.writeHtml error: " + content, e);
        }
    }

    /**
     * 回写html，默认使用utf-8
     * @param response 响应对象
     * @param content html文本
     */
    public static void writeHtml(ServletResponse response, String content) {
        writeHtml(response, content, CHARSET);
    }

}
