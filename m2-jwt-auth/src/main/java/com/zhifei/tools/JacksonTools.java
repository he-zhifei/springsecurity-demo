package com.zhifei.tools;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

/**
 * Json工具类（Jackson）
 *
 * @author He Zhifei
 * @date 2021/12/20 19:02
 */
public class JacksonTools {

    private static final Logger LOGGER = LoggerFactory.getLogger(JacksonTools.class);

    private static final ObjectMapper MAPPER = new ObjectMapper();

    static {
        MAPPER.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS)
                // 把json字符串转为对象时，避免json字符串包含对象未知的属性报错，而是把把已知属性进行赋值转化。
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .enable(MapperFeature.PROPAGATE_TRANSIENT_MARKER)
                .setSerializationInclusion(JsonInclude.Include.ALWAYS);
    }

    /**
     * 把对象转为json字符串
     * @param o 被转化的对象
     * @return 转化的json字符串
     */
    public static String toJson(Object o) {
        String result = null;
        try {
            result = MAPPER.writeValueAsString(o);
        } catch (Exception e) {
            LOGGER.error("JacksonTools.toJson error: " + o, e);
        }
        return result;
    }

    /**
     * 把输入流转为对象
     * @param inputStream 被转化的输入流
     * @param objectType 转化的对象class类型
     * @param <T> 对象的泛型
     * @return 转化的对象
     */
    public static <T> T fromInputStream(InputStream inputStream, Class<T> objectType) {
        if (inputStream == null) {
            return null;
        }
        T result = null;
        try {
            result = MAPPER.readValue(inputStream, objectType);
        } catch (Exception e) {
            LOGGER.error("JacksonTools.fromInputStream error: " + inputStream, e);
        }
        return result;
    }

    /**
     * 把json字符串转为对象
     * @param json 被转化的json字符串
     * @param objectType 转化的对象class类型
     * @param <T> 对象的泛型
     * @return 转化的对象
     */
    public static <T> T fromJson(String json, Class<T> objectType) {
        if (json == null || json.length() == 0) {
            return null;
        }
        T result = null;
        try {
            result = MAPPER.readValue(json, objectType);
        } catch (Exception e) {
            LOGGER.error("JacksonTools.fromJson error: " + json, e);
        }
        return result;
    }

    /**
     * 把json字符串转为List
     * @param json 被转化的json字符串
     * @param elementType 转化后List中元素的class类型
     * @param <E> List元素的泛型
     * @return 转化的List集合
     */
    public static <E> List<E> fromJsonToList(String json, Class<E> elementType) {
        if (json == null || json.length() == 0) {
            return null;
        }
        List<E> result = null;
        try {
            result = MAPPER.readValue(json, createCollectionType(List.class, elementType));
        } catch (Exception e) {
            LOGGER.error("JacksonTools.fromJsonToList error: " + json, e);
        }
        return result;
    }

    /**
     * 把json字符串转为Map
     * @param json 被转化的json字符串
     * @param keyType 转化后Map的key的class类型
     * @param valueType 转化后Map的value的class类型
     * @param <K> Map的key泛型
     * @param <V> Map的value泛型
     * @return 转化后的Map集合
     */
    public static <K,V> Map<K,V> fromJsonToMap(String json, Class<K> keyType, Class<V> valueType) {
        if (json == null || json.length() == 0) {
            return null;
        }
        Map<K,V> result = null;
        try {
            result = MAPPER.readValue(json, createCollectionType(Map.class, keyType, valueType));
        } catch (Exception e) {
            LOGGER.error("JacksonTools.fromJsonToMap error: " + json, e);
        }
        return result;
    }

    /**
     * 根据传入的集合类型和元素类型，创建JavaType对象，具体用法可查看：
     * {@link JacksonTools#fromJsonToList(String, Class)} 和
     * {@link JacksonTools#fromJsonToMap(String, Class, Class)}
     *
     * @param collectionClass 集合类型
     * @param elementClasses 集合元素类型
     * @return JavaType对象
     */
    public static JavaType createCollectionType(Class<?> collectionClass, Class<?>... elementClasses) {
        return MAPPER.getTypeFactory().constructParametricType(collectionClass, elementClasses);
    }

    /**
     * 根据传入的JavaType对象，把json字符串转成对象，结合
     * {@link JacksonTools#createCollectionType(Class, Class[])} 方法使用。
     *
     * @param json 被转化的json字符串
     * @param javaType JavaType对象
     * @param <T> 结果对象泛型
     * @return 转化的对象
     */
    public static <T> T fromJsonToJavaType(String json, JavaType javaType) {
        if (json == null || json.length() == 0) {
            return null;
        }
        T result = null;
        try {
            result = MAPPER.readValue(json, javaType);
        } catch (Exception e) {
            LOGGER.error("JacksonTools.fromJsonToJavaType error: " + json, e);
        }
        return result;
    }

    /**
     * 把json字符串转为泛型对象
     * @param json 被转化的json字符串
     * @param valueTypeRef 转化的对象class类型引用
     * @param <T> 泛型
     * @return 转化的对象
     *
     * <p>
     * 可以直接转为Map或者List等泛型复杂形式，示例：
     * List<String> stringList = JacksonTools.fromJsonToTypeReference(json, new TypeReference<List<String>>() {});
     * 不推荐使用，因为每次都要创建一个此类的引用对象。推荐使用 {@link JacksonTools#fromJsonToList(String, Class)}
     * 这种方式处理复杂的集合类型。
     * </p>
     */
    public static <T> T fromJsonToTypeReference(String json, TypeReference<T> valueTypeRef) {
        if (json == null || json.length() == 0) {
            return null;
        }
        T result = null;
        try {
            result = MAPPER.readValue(json, valueTypeRef);
        } catch (Exception e) {
            LOGGER.error("JacksonTools.fromJsonToTypeReference error: " + json, e);
        }
        return result;
    }
}
