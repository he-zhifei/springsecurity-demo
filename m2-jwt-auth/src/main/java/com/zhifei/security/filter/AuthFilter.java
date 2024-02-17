package com.zhifei.security.filter;

import com.zhifei.entity.SysUser;
import com.zhifei.plugin.exception.enums.RCode;
import com.zhifei.security.exception.AuthException;
import com.zhifei.tools.JacksonTools;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 认证过滤器（用户名、密码）
 *
 * @author He Zhifei
 * @date 2020/7/18 0:21
 */
public class AuthFilter extends UsernamePasswordAuthenticationFilter {

    private AuthenticationManager authenticationManager;

    public AuthFilter(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    public AuthFilter(String loginUrl, AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
        // 指定登录url，默认 /login
        if (loginUrl != null && loginUrl.trim().length() != 0) {
            super.setRequiresAuthenticationRequestMatcher(new AntPathRequestMatcher(loginUrl));
        }
    }

    /**
     * 认证流程方法
     * @param request
     * @param response
     * @return
     */
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) {
        // 请求方法校验
        if (!request.getMethod().equalsIgnoreCase("POST")) {
            throw new AuthException("认证接口仅支持POST请求");
        }

        // 获取认证请求数据
        SysUser sysUser = null;
        try {
            sysUser = JacksonTools.fromInputStream(request.getInputStream(), SysUser.class);
        } catch (IOException e) {
            throw new AuthException(RCode.REQUEST_BODY_DATA_ERROR);
        }
        if (sysUser == null) {
            throw new AuthException(RCode.REQUEST_BODY_DATA_ERROR);
        }

        // 用户名、密码非空校验
        if (StringUtils.isBlank(sysUser.getUsername())) {
            throw new AuthException(RCode.BLANK_PRINCIPAL);
        }
        if (StringUtils.isBlank(sysUser.getPassword())) {
            throw new AuthException(RCode.BLANK_CREDENTIAL);
        }

        UsernamePasswordAuthenticationToken authenticateToken = new UsernamePasswordAuthenticationToken(
                sysUser.getUsername(), sysUser.getPassword());
        return authenticationManager.authenticate(authenticateToken);
    }

    /**
     * 目的：废除父类的此方法，因为父类filter是可以多种请求方法登录的，当前类只允许使用POST
     * @param postOnly
     */
    @Deprecated
    @Override
    public void setPostOnly(boolean postOnly) {
        throw new AuthException("认证接口仅支持POST请求，请勿修改认证请求方法");
    }

}
