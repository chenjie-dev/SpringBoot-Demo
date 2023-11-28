package com.chenjie.redis.serializer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;
import org.springframework.lang.Nullable;

import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;

/**
 * @Description redis自定义key序列化和反序列化工具
 */
public class RedisKeySerializer implements RedisSerializer<String> {

    private final String redisPrefixNamespace;
    private final String redisPublicPrefix;
    private final Pattern publicPrefixPattern;
    private boolean namespaceSwitch;

    public RedisKeySerializer(String redisPrefixNamespace, String redisPublicPrefix, Boolean namespaceSwitch) {
        this.redisPrefixNamespace = redisPrefixNamespace;
        this.redisPublicPrefix = redisPublicPrefix;
        this.publicPrefixPattern = Pattern.compile(this.redisPublicPrefix + ":.+");
        this.namespaceSwitch = namespaceSwitch == null ? true : namespaceSwitch;
    }

    @Override
    public byte[] serialize(String s) throws SerializationException {
        if (namespaceSwitch) {
            return enPrefixToKeyToByte(s);
        } else {
            return s.getBytes(StandardCharsets.UTF_8);
        }
    }

    @Override
    public String deserialize(byte[] bytes) throws SerializationException {
        if (namespaceSwitch) {
            return dePrefixToKeyToByte(bytes);
        } else {
            return new String(bytes, StandardCharsets.UTF_8);
        }
    }

    public byte[] serializeWithNoPrefix(String s) {
        return s.getBytes(StandardCharsets.UTF_8);
    }

    public String deserializeWithNoPrefix(byte[] bytes) {
        return new String(bytes, StandardCharsets.UTF_8);
    }

    /**
     * 为key添加命名空间前缀，当key本身含有一个约定的公共前缀时则不添加
     */
    private byte[] enPrefixToKeyToByte(String key) {
        if (key == null) {
            return null;
        }
        return (publicPrefixPattern.matcher(key).matches() ? key : redisPrefixNamespace + ":" + key).getBytes(StandardCharsets.UTF_8);
    }

    /**
     * 反序列化时去掉加的前缀
     */
    private String dePrefixToKeyToByte(byte[] bytes) {
        if (bytes == null) {
            return null;
        }
        String key = new String(bytes, StandardCharsets.UTF_8);
        return publicPrefixPattern.matcher(key).matches() ? key : key.replaceFirst(redisPrefixNamespace + ":", "");
    }


    private final ObjectMapper mapper = new ObjectMapper();

    public byte[] objectSerialize(@Nullable Object source) throws SerializationException {
        if (source == null) {
            return new byte[0];
        }

        try {
            return mapper.writeValueAsBytes(source);
        } catch (JsonProcessingException e) {
            throw new SerializationException("Could not write JSON: " + e.getMessage(), e);
        }
    }

    public byte[] objectSerialize(@Nullable Object source, ObjectMapper mapper) throws SerializationException {
        if (source == null) {
            return new byte[0];
        }

        try {
            return mapper.writeValueAsBytes(source);
        } catch (JsonProcessingException e) {
            throw new SerializationException("Could not write JSON: " + e.getMessage(), e);
        }
    }

    public Object objectSeserialize(@Nullable byte[] bytes) throws SerializationException {
        if (bytes == null || bytes.length == 0) {
            return null;
        }

        try {
            return mapper.readValue(bytes, Object.class);
        } catch (Exception ex) {
            throw new SerializationException("Could not read JSON: " + ex.getMessage(), ex);
        }
    }

    public Object objectSeserialize(@Nullable byte[] bytes, ObjectMapper mapper) throws SerializationException {
        if (bytes == null || bytes.length == 0) {
            return null;
        }

        try {
            return mapper.readValue(bytes, Object.class);
        } catch (Exception ex) {
            throw new SerializationException("Could not read JSON: " + ex.getMessage(), ex);
        }
    }

}
