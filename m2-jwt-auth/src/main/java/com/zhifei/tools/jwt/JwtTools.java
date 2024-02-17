package com.zhifei.tools.jwt;

/**
 * Jwt工具类，使用的是RS256加密方式
 *
 * @author He Zhifei
 * @date 2021/12/17 12:06
 */
public class JwtTools {

    private static final String CLAIMS_DATA = "data";

    /**
     * 实例化Jwt构建器（生成Jwt）
     * @param rsaPrivateKey rsa私钥
     * @return Jwt构建器
     */
    public static JwtBeans.Builder builder(String rsaPrivateKey) {
        return new JwtBeans.Builder(rsaPrivateKey);
    }

    /**
     * 实例化Jwt解析器（解析Jwt）
     * @param rsaPublicKey rsa公钥
     * @return Jwt解析器
     */
    public static JwtBeans.Parser parser(String rsaPublicKey) {
        return new JwtBeans.Parser(rsaPublicKey);
    }

    /**
     * 实例化Jwt校验器（校验Jwt）
     * @param rsaPrivateKey rsa私钥
     * @return Jwt校验器
     */
    public static JwtBeans.Signer signer(String rsaPrivateKey) {
        return new JwtBeans.Signer(rsaPrivateKey);
    }

}
