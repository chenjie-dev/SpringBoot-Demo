package com.chenjie.redis;

import com.chenjie.redis.constant.RedisStarterConstant;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.List;

/**
 * @Description 区分springboot自己的redistemplate
 */
public class StarterRedisTemplate<K, V> extends RedisTemplate<K, V> {

    public String removePrefix(String key) {
        if (key == null) {
            return key;
        }

        String[] strs = key.split(":");
        if (strs.length == 1) {
            return key;
        }

        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 1; i < strs.length; i++) {
            stringBuilder.append(strs[i]);
        }

        return stringBuilder.toString();
    }

    /**
     * 为key添加公共命名空间前缀，如public:yourkey
     */
    public static String getPublicNamespaceKey(String key) {
        return RedisStarterConstant.publicNamespace + ":" + key;
    }

    /**
     * 为key添加公共命名空间前缀，可以添加其他下级分组，如public:emailServiceGroupName:yourkey
     */
    public static String getCustomPublicNamespaceKey(String originalKey, List<String> groups) {
        StringBuilder stringBuilder = new StringBuilder(RedisStarterConstant.publicNamespace + ":");
        groups.forEach(e -> {
            stringBuilder.append(e + ":");
        });
        stringBuilder.append(originalKey + ":");
        return stringBuilder.toString();
    }

}
