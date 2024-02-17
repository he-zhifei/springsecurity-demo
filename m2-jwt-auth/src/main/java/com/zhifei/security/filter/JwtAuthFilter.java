package com.zhifei.security.filter;

import com.zhifei.entity.SecurityUser;
import com.zhifei.entity.SysUser;
import com.zhifei.plugin.exception.entity.R;
import com.zhifei.plugin.exception.enums.RCode;
import com.zhifei.plugin.exception.exception.CustomException;
import com.zhifei.security.exception.AuthException;
import com.zhifei.security.properties.JwtRsaProperties;
import com.zhifei.service.SysUserService;
import com.zhifei.tools.HttpTools;
import com.zhifei.tools.jwt.JwtTools;
import com.zhifei.vo.RolesAndMenus;
import com.zhifei.vo.UserVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/**
 * 认证过滤器（Jwt）
 *
 * @author He Zhifei
 * @date 2020/7/18 2:07
 */
public class JwtAuthFilter extends BasicAuthenticationFilter {

    @Autowired
    private JwtRsaProperties jwtRsaProps;

    @Autowired
    private SysUserService sysUserService;

    public JwtAuthFilter(AuthenticationManager authenticationManager) {
        super(authenticationManager);
    }

    /**
     * Jwt认证流程方法
     *
     * @param request
     * @param response
     * @param chain
     * @throws IOException
     * @throws ServletException
     */
    public void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        try {
            // 获取Authorization请求头，并校验格式
            String auth = HttpTools.getRequest().getHeader("Authorization");
            if (StringUtils.isBlank(auth)) {
                throw new AuthException(RCode.NEED_LOGIN);
            }
            String token = auth.replaceAll("^(Bearer|bearer)", "").trim();
            if (StringUtils.isBlank(token)) {
                throw new AuthException(RCode.NEED_LOGIN);
            }

            // 校验签名是否合法
            if (!JwtTools.signer(jwtRsaProps.getPrivateKey()).verifyJwt(token)) {
                throw new CustomException(RCode.NEED_LOGIN);
            }

            // 获取jwt中的用户名
            UserVo userVo = JwtTools.parser(jwtRsaProps.getPublicKey()).parseJwt4Data(token, UserVo.class);

            // 校验用户是否真实存在，且处于可用状态
            String username = userVo.getUsername();
            SysUser user = sysUserService.getByUsername(username);
            checkSysUser(user);

            // 生成token并设置到SecurityContext中
            RolesAndMenus rolesAndMenus = sysUserService.getRealRolesAndMenus(user.getId());
            Set<SimpleGrantedAuthority> authorities = new HashSet<>();
            Optional.ofNullable(rolesAndMenus.getRoleSet()).orElse(Collections.emptySet()).forEach(role -> {
                authorities.add(new SimpleGrantedAuthority(SecurityUser.ROLE_PREFIX + role.getName()));
            });
            Optional.ofNullable(rolesAndMenus.getMenuSet()).orElse(Collections.emptySet()).forEach(menu -> {
                authorities.add(new SimpleGrantedAuthority(menu.getPermission()));
            });
            UsernamePasswordAuthenticationToken authenticateToken = new UsernamePasswordAuthenticationToken(
                    user.getUsername(), null, authorities);
            SecurityContextHolder.getContext().setAuthentication(authenticateToken);

            // 继续执行过滤器链中的其它过滤器
            chain.doFilter(request, response);
        } catch (Exception e) {
            HttpTools.writeJson(response, R.fail().rCode(RCode.NEED_LOGIN));
        }
    }

    /**
     * 检查用户是否存在且可用
     * @param user
     */
    private void checkSysUser(SysUser user) {
        if (user == null) {
            throw new AuthException(RCode.INCORRECT_PRINCIPAL);
        }
        if (user.getEnabled() == 0) {
            throw new AuthException(RCode.ACCOUNT_LOCKED);
        }
    }

}