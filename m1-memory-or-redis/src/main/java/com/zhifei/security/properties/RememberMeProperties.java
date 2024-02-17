package com.zhifei.security.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.web.authentication.rememberme.AbstractRememberMeServices;

@Configuration
@ConfigurationProperties(prefix = "remember-me")
public class RememberMeProperties {

    private String key = "REMEMBER_ME_KEY";

    /**
     * 多久没访问该系统则token失效，默认2周，单位：秒
     */
    private int tokenValiditySeconds = AbstractRememberMeServices.TWO_WEEKS_S;

    /**
     * cookie中的key-value的key名称
     */
    private String cookieName = "token";

    /**
     * 登录时，参数名称，类型为{@link Boolean}，true-记住密码；false-不记住密码
     */
    private String parameterName = "remember-me";

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public int getTokenValiditySeconds() {
        return tokenValiditySeconds;
    }

    public void setTokenValiditySeconds(int tokenValiditySeconds) {
        this.tokenValiditySeconds = tokenValiditySeconds;
    }

    public String getCookieName() {
        return cookieName;
    }

    public void setCookieName(String cookieName) {
        this.cookieName = cookieName;
    }

    public String getParameterName() {
        return parameterName;
    }

    public void setParameterName(String parameterName) {
        this.parameterName = parameterName;
    }
}
