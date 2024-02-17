package com.zhifei.security.config;

import com.zhifei.security.filter.AuthFilter;
import com.zhifei.security.handler.*;
import com.zhifei.security.properties.RememberMeProperties;
import com.zhifei.security.token.RedisTokenRepositoryImpl;
import com.zhifei.tools.encrypt.SecurityTools;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.access.prepost.PreFilter;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.security.web.authentication.rememberme.InMemoryTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenBasedRememberMeServices;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;

import javax.annotation.Resource;

/**
 * @EnableGlobalMethodSecurity 开启方法注解，其中在方法或者类上可以使用三套注解，用哪一套都可以：
 *
 * 1.Spring Security's {@link PreAuthorize}, {@link PreFilter}, {@link PostAuthorize}, {@link PostFilter} annotations：prePostEnabled = true，例子：@PreAuthorize("xxx")
 * 使用这套注解时，需要用到EL表达式：例如：@PreAuthorize("hasAnyRole('xxx')")     @PreAuthorize("hasAnyAuthority('xxx')")
 * has...Role和has...Authorize里面参数的区别：前者会自动拼接ROLE_，因此，在设置数据库角色、权限表时，角色可以前面加上ROLE_前缀，权限不加此前缀。
 * 而在使用这两个注解时，前者用于角色，后者用于权限，以作区分。
 *
 * 2.Spring Security's {@link Secured} annotations：securedEnabled = true，例子：@Secured("xxx")
 *
 * 3.JSR-250 annotations：jsr250Enabled = true，例子：@RolesAllowed("xxx")
 *
 * @author He Zhifei
 * @date 2020/7/1 15:39
 */
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Resource
    private UserDetailsService sysUserServiceImpl;

    @Autowired
    private RememberMeProperties rememberMeProps;

    @Autowired
    private UnAuthEntryPoint unAuthEntryPoint;

    @Autowired
    private AuthSuccessHandler authSuccessHandler;

    @Autowired
    private AuthFailureHandler authFailureHandler;

    @Autowired
    private UnAuthorizedHandler unAuthorizedHandler;

    @Autowired
    private LoutSuccessHandler loutSuccessHandler;

    /**
     * 自定义配置
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        /**
         * 如果是使用默认的formLogin，则配置.successHandler(authSuccessHandler).failureHandler(authFailureHandler)会生效，因为这里
         * 用{@link AuthFilter}重新实现了{@link org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter}
         * 所以要手动设置，当然，也可以直接在这个filter中实现successfulAuthentication、unsuccessfulAuthentication方法
         */
        AuthFilter authFilter = new AuthFilter("/myLogin", super.authenticationManager());
        authFilter.setAuthenticationSuccessHandler(authSuccessHandler);
        authFilter.setAuthenticationFailureHandler(authFailureHandler);
        authFilter.setRememberMeServices(cookieRememberMeServices());

        // 建议书写顺序：先写 不需要认证的（permitAll）、需要某些权限认证的，最后到需要认证的（.anyRequest().authenticated()）
        http.cors().and().csrf().disable()
//                .formLogin().permitAll()
//                .successHandler(authSuccessHandler)
//                .failureHandler(authFailureHandler)
//                .and()
                .addFilter(authFilter)
                .logout()
                .logoutUrl("/myLogout")           // 默认/logout
                .logoutSuccessHandler(loutSuccessHandler)
            .and()
                .authorizeRequests()
//                .antMatchers("/test/**").permitAll()
                .anyRequest().authenticated()
            .and()
                .exceptionHandling()
                .authenticationEntryPoint(unAuthEntryPoint)
                .accessDeniedHandler(unAuthorizedHandler)
            .and()
                .rememberMe()
                .rememberMeServices(cookieRememberMeServices());

//            // 自定义登录filter后，这里配置的rememberMe也会失效，需手动设置登录filter的RememberMeServices
//            .and()
//                .rememberMe()
//                .tokenValiditySeconds(60)               //token 60s失效，也就是60s后记住密码失效
//                .rememberMeParameter("remember-me")         //默认参数名也是remember-me
////                .tokenRepository(jdbcTokenRepository());
////                .tokenRepository(redisRememberMeTokenRepository());
//                .tokenRepository(new InMemoryTokenRepositoryImpl());
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(sysUserServiceImpl).passwordEncoder(bCryptPasswordEncoder());
    }

    @Bean
    public PasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public RememberMeServices cookieRememberMeServices() {
        PersistentTokenBasedRememberMeServices rememberMeServices = new PersistentTokenBasedRememberMeServices(
                rememberMeProps.getKey(), sysUserServiceImpl, tokenRepository()
        ) {
                // 重写redis token的规则series的生成规则，模板：md5(username)_原来规则，方便redis通过username删除token
                @Override
                protected String generateSeriesData() {
                    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                    String username = authentication.getName();
                    return new StringBuilder(SecurityTools.md5(username)).append("_").append(super.generateSeriesData()).toString();
                }
        };
        rememberMeServices.setTokenValiditySeconds(rememberMeProps.getTokenValiditySeconds());
        rememberMeServices.setCookieName(rememberMeProps.getCookieName());
        rememberMeServices.setParameter(rememberMeProps.getParameterName());
        return rememberMeServices;
    }

    @Bean
    public PersistentTokenRepository tokenRepository() {
//        // 使用Mysql存储Token
//        JdbcTokenRepositoryImpl tokenRepository = new JdbcTokenRepositoryImpl();
//        tokenRepository.setDataSource(dataSource);
//        return tokenRepository;

        // 使用Redis存储Token
//        return new RedisTokenRepositoryImpl();

//        // 使用内存存储Token
        return new InMemoryTokenRepositoryImpl();
    }

}
