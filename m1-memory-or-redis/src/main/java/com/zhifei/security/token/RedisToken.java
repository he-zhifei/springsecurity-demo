package com.zhifei.security.token;

import org.springframework.security.web.authentication.rememberme.PersistentRememberMeToken;

import java.io.Serializable;
import java.util.Date;

/**
 * 模仿{@link PersistentRememberMeToken}而来，因为其无法序列化，故重新创建一个实现序列化接口
 *
 * @author He Zhifei
 * @date 2020/7/16 0:16
 */
public class RedisToken implements Serializable {

    private String username;
    private String series;
    private String tokenValue;
    private Date date;

    public RedisToken(String username, String series, String tokenValue, Date date) {
        this.username = username;
        this.series = series;
        this.tokenValue = tokenValue;
        this.date = date;
    }

    public RedisToken() {
    }

    public String getUsername() {
        return username;
    }

    public String getSeries() {
        return series;
    }

    public String getTokenValue() {
        return tokenValue;
    }

    public Date getDate() {
        return date;
    }
}
