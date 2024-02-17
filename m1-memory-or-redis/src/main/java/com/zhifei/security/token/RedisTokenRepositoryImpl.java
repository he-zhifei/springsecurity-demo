package com.zhifei.security.token;

import com.zhifei.security.properties.RememberMeProperties;
import com.zhifei.security.config.SecurityConfig;
import com.zhifei.tools.JacksonTools;
import com.zhifei.tools.encrypt.SecurityTools;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.web.authentication.rememberme.InMemoryTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentRememberMeToken;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * 把token放Redis，模仿{@link InMemoryTokenRepositoryImpl}而来
 *
 * 虽然在当前类上没有使用@Component等注解注入到Spring容器
 * 但在{@link SecurityConfig#tokenRepository()}有注入，因此可以使用@Autowired
 *
 * @author He Zhifei
 * @date 2020/7/15 22:17
 */
public class RedisTokenRepositoryImpl implements PersistentTokenRepository {

    private static final String TOKEN_PREFIX = "spring-security-token:";

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private RememberMeProperties rememberMeProperties;

    private String getKey(String series) {
        return TOKEN_PREFIX + series;
    }

    @Override
    public synchronized void createNewToken(PersistentRememberMeToken token) {
        String series = token.getSeries();
        RedisToken currentToken = getCurrentRedisToken(series);
        if (currentToken != null) {
            throw new DataIntegrityViolationException("Series Id '" + series + "' already exists!");
        }
        stringRedisTemplate.opsForValue().set(getKey(series), JacksonTools.toJson(toRedisToken(token)),
                rememberMeProperties.getTokenValiditySeconds(), TimeUnit.SECONDS);
    }

    @Override
    public synchronized void updateToken(String series, String tokenValue, Date lastUsed) {
        RedisToken currentToken = getCurrentRedisToken(series);
        RedisToken newToken = new RedisToken(
                currentToken.getUsername(), series, tokenValue, new Date());
        stringRedisTemplate.opsForValue().set(getKey(series), JacksonTools.toJson(newToken),
                rememberMeProperties.getTokenValiditySeconds(), TimeUnit.SECONDS);
    }

    @Override
    public synchronized PersistentRememberMeToken getTokenForSeries(String seriesId) {
        return toPersistentToken(getCurrentRedisToken(seriesId));
    }

    @Override
    public synchronized void removeUserTokens(String username) {
        StringBuilder keyBuilder = new StringBuilder(TOKEN_PREFIX).append(SecurityTools.md5(username)).append("_").append("*");
        Set<String> keySet = stringRedisTemplate.keys(keyBuilder.toString());
        stringRedisTemplate.delete(keySet);
    }

    private RedisToken getCurrentRedisToken(String series) {
        String currentTokenStr = stringRedisTemplate.opsForValue().get(getKey(series));
        if (StringUtils.isEmpty(currentTokenStr)) return null;
        RedisToken currentToken = JacksonTools.fromJson(currentTokenStr, RedisToken.class);
        return currentToken;
    }

    private RedisToken toRedisToken(PersistentRememberMeToken token) {
        if (token == null) {
            return null;
        }
        return new RedisToken(token.getUsername(), token.getSeries(), token.getTokenValue(), token.getDate());
    }

    private PersistentRememberMeToken toPersistentToken(RedisToken redisToken) {
        if (redisToken == null) {
            return null;
        }
        return new PersistentRememberMeToken(redisToken.getUsername(), redisToken.getSeries(), redisToken.getTokenValue(), redisToken.getDate());
    }

}
