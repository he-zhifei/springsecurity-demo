package com.zhifei.tools.jwt;

import com.zhifei.tools.JacksonTools;
import com.zhifei.tools.encrypt.SecurityTools;
import io.jsonwebtoken.*;
import io.jsonwebtoken.impl.crypto.DefaultJwtSigner;
import io.jsonwebtoken.impl.crypto.JwtSigner;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.io.Encoders;
import org.apache.commons.lang3.StringUtils;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

/**
 * Jwt工具类的组件，使用的是RS256加密方式（推荐），可按如下切换成SHA512：
 *
 * <pre>
 *     String auth = "0123456789012345678901234567890123456789012345678901234567890123456789";
 *     jwtBuilder = Jwts.builder()
 *         .setHeaderParam("typ", "JWT")
 *         .setHeaderParam("alg", "SHA512")
 *         .signWith(Keys.hmacShaKeyFor(auth.getBytes()), SignatureAlgorithm.HS512);
 *     jwtParser = Jwts.parserBuilder()
 *         .setSigningKey(Keys.hmacShaKeyFor(auth.getBytes()))
 *         .build();
 * </pre>
 *
 * @author He Zhifei
 * @date 2021/12/17 11:23
 */
public class JwtBeans {

    private static final String CLAIMS_DATA = "data";

    /**
     * Jwt构建器
     */
    public static class Builder {

        private static final int NOT_EXPIRED = -1;

        private JwtBuilder jwtBuilder;

        public Builder(String rsaPrivateKey) {
            jwtBuilder = Jwts.builder()
                    .setHeaderParam(Header.TYPE, "JWT")
                    .setHeaderParam(JwsHeader.ALGORITHM, "RS256")
                    .signWith(SecurityTools.rsaPrivateKey(rsaPrivateKey), SignatureAlgorithm.RS256);
        }

        /**
         * 生成长期有效的Jwt
         * @param data 被存储的数据
         * @return Jwt
         */
        public String generateJwt(Object data) {
            return generateJwt(data, NOT_EXPIRED);
        }

        /**
         * 生成Jwt，并设定有效时间，单位：秒
         * @param data 被存储的数据
         * @param expire 过期时间
         * @return Jwt
         */
        public String generateJwt(Object data, int expire) {
            Date expDate = (expire == NOT_EXPIRED) ? null : new Date(System.currentTimeMillis() + expire * 1000);
            return jwtBuilder
                    .setId(UUID.randomUUID().toString())
                    .setExpiration(expDate)
                    .claim(CLAIMS_DATA, JacksonTools.toJson(data))
                    .compact();
        }
    }

    /**
     * Jwt解析器
     */
    public static class Parser {

        private JwtParser jwtParser;

        public Parser(String rsaPublicKey) {
            jwtParser = Jwts.parserBuilder()
                    .setSigningKey(SecurityTools.rsaPublicKey(rsaPublicKey))
                    .build();
        }

        /**
         * 获取载荷数据
         * @param token Jwt
         * @return 载荷数据
         */
        public Jws<Claims> parseJwt(String token) {
            return jwtParser.parseClaimsJws(token);
        }

        /**
         * 获取生成Jwt时存储的数据
         * @param token Jwt
         * @param clz 数据的类型
         * @return 存储的数据
         */
        public <T> T parseJwt4Data(String token, Class<T> clz) {
            Object data = parseJwt(token).getBody().get(CLAIMS_DATA);
            return JacksonTools.fromJson(data == null ? null : data.toString(), clz);
        }
    }

    /**
     * Jwt校验器
     */
    public static class Signer {

        private JwtSigner signer;

        public Signer(String rsaPrivateKey) {
            this.signer = new DefaultJwtSigner(SignatureAlgorithm.RS256,
                    SecurityTools.rsaPrivateKey(rsaPrivateKey), Encoders.BASE64URL);
        }

        /**
         * 校验Jwt（加密是否有效、有没有过期）
         * @param token Jwt
         * @return 是否通过
         */
        public Boolean verifyJwt(String token) {
            try {
                String[] arr = null;
                if (StringUtils.isBlank(token) ||  (arr = token.split("[.]")).length != 3) {
                    return false;
                }
                String base64UrlSignature = signer.sign(arr[0] + "." + arr[1]);
                return StringUtils.isNotBlank(base64UrlSignature) && base64UrlSignature.equals(arr[2]) && isNotExpired(arr[1]);
            } catch (Exception e) {
            }
            return false;
        }

        /**
         * 校验是否没过期
         * @param payload Jwt第二部分
         * @return 是否没过期
         */
        private Boolean isNotExpired(String payload) {
            String pl = new String(Decoders.BASE64URL.decode(payload), StandardCharsets.UTF_8);
            Map map = JacksonTools.fromJson(pl, Map.class);
            Object exp = map.get(Claims.EXPIRATION);
            if (exp != null) {
                Long expMs = Long.valueOf(exp.toString());
                return expMs*1000 > System.currentTimeMillis();
            }
            return true;
        }

    }
}
