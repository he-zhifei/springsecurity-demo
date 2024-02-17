package com.zhifei.security.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * JwtRsaProperties
 *
 * @author He Zhifei
 * @date 2023/10/13 10:40
 */
@Configuration
@ConfigurationProperties(prefix = "jwt.rsa")
public class JwtRsaProperties {

    /**
     * 公钥
     */
    private String publicKey;

    /**
     * 私钥
     */
    private String privateKey;

    /**
     * accessToken过期时间（秒）
     */
    private Integer accessTokenExpired = 60 * 30;

    /**
     * refreshToken过期时间（秒），在这个时间范围内有访问，无限续签
     */
    private Integer refreshTokenExpired = 60 * 60 * 24 * 7;

    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    public String getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
    }

    public Integer getAccessTokenExpired() {
        return accessTokenExpired;
    }

    public void setAccessTokenExpired(Integer accessTokenExpired) {
        this.accessTokenExpired = accessTokenExpired;
    }

    public Integer getRefreshTokenExpired() {
        return refreshTokenExpired;
    }

    public void setRefreshTokenExpired(Integer refreshTokenExpired) {
        this.refreshTokenExpired = refreshTokenExpired;
    }
}
