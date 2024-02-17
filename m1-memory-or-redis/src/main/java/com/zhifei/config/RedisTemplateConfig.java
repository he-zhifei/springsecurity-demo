package com.zhifei.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.io.Serializable;

/**
 * RedisTemplate配置
 *
 * opsForXXX和boundXXXOps的区别：
 * XXX为value的类型，前者获取一个operator，但是没有指定操作的对象（key），可以在一个连接（事务）内操作多个key以及对应的value；
 * 后者获取了一个指定操作对象（key）的operator，在一个连接（事务）内只能操作这个key对应的value。
 *
 * @author He Zhifei
 * @date 2022/3/18 9:36
 */
@Configuration
public class RedisTemplateConfig {

    /**
     * 增加RedisTemplate对value为Object类型的支持，默认只支持String类型的key和value，与{@link StringRedisTemplate}一样。
     * 另外，springboot2.x之前redis的连接池为jedis，2.0以后redis的连接池改为了lettuce，lettuce能够支持redis4，
     * 需要java8及以上，使用LettuceConnectionFactory代替RedisConnectionFactory
     *
     * {@link RedisSerializer}类下提供了几种默认的序列化器
     *
     * @param factory
     * @return RedisTemplate
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(LettuceConnectionFactory factory) {
        RedisTemplate<String, Object> template = new RedisTemplate<String, Object>();
        template.setConnectionFactory(factory);

        StringRedisSerializer stringSerializer = new StringRedisSerializer();
        Jackson2JsonRedisSerializer jsonSerializer = new Jackson2JsonRedisSerializer(Object.class);
        ObjectMapper mapper = new ObjectMapper();
        mapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        // 旧版本使用，新版本已经是@Deprecated了
        //mapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL, JsonTypeInfo.As.WRAPPER_ARRAY);
        mapper.activateDefaultTyping(LaissezFaireSubTypeValidator.instance, ObjectMapper.DefaultTyping.NON_FINAL,
                JsonTypeInfo.As.WRAPPER_ARRAY);
        jsonSerializer.setObjectMapper(mapper);
        // 设置key/hashKey序列化器为字符串类型序列化器，value/hashValue序列化器为json类型序列化器
        template.setKeySerializer(stringSerializer);
        template.setHashKeySerializer(stringSerializer);
        template.setValueSerializer(jsonSerializer);
        template.setHashValueSerializer(jsonSerializer);
        template.afterPropertiesSet();

        return template;
    }

    /**
     * key/value（也支持byte[]）都为较为复杂的Object类型时使用objectRedisTemplate
     * @param factory
     * @return
     */
    @Bean(name = "objectRedisTemplate")
    public RedisTemplate<Serializable, Serializable> objectRedisTemplate(LettuceConnectionFactory factory) {
        RedisTemplate<Serializable, Serializable> template = new RedisTemplate<Serializable, Serializable>();
        template.setConnectionFactory(factory);

        RedisSerializer4Object serializer = new RedisSerializer4Object();
        template.setKeySerializer(serializer);
        template.setHashKeySerializer(serializer);
        template.setValueSerializer(serializer);
        template.setHashValueSerializer(serializer);
        template.setDefaultSerializer(serializer);
        template.afterPropertiesSet();
        return template;
    }

    private class RedisSerializer4Object implements RedisSerializer {
        @Override
        public byte[] serialize(Object o) throws SerializationException {
            return org.springframework.util.SerializationUtils.serialize(o);
        }

        @Override
        public Object deserialize(byte[] bytes) throws SerializationException {
            return org.springframework.util.SerializationUtils.deserialize(bytes);
        }
    }

}
