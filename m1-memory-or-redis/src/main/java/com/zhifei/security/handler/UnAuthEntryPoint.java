package com.zhifei.security.handler;

import com.zhifei.plugin.exception.entity.R;
import com.zhifei.plugin.exception.enums.RCode;
import com.zhifei.tools.HttpTools;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 认证入口点，取消未认证默认跳转登录页的行为
 */
@Component
public class UnAuthEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException e) throws IOException, ServletException {
        HttpTools.writeJson(response, R.fail().rCode(RCode.NEED_LOGIN));
    }
}
