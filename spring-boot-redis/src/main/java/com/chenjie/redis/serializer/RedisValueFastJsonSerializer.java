package com.chenjie.redis.serializer;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;

import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * @Description redis自定义value序列化和反序列化工具
 */
public class RedisValueFastJsonSerializer<T> implements RedisSerializer<T> {

    private final Class<T> clazz;

    public RedisValueFastJsonSerializer(Class<T> clazz) {
        this.clazz = clazz;
    }

    @Override
    public byte[] serialize(T t) throws SerializationException {
        if (Objects.isNull(t)) {
            return new byte[0];
        }
        return JSON.toJSONString(t, SerializerFeature.WriteClassName).getBytes(StandardCharsets.UTF_8);
    }

    @Override
    public T deserialize(byte[] bytes) throws SerializationException {
        if (Objects.isNull(bytes) || ArrayUtils.isEmpty(bytes)) {
            return null;
        }
        return JSON.parseObject(new String(bytes, StandardCharsets.UTF_8), clazz);
    }

}
