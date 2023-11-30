package com.chenjie.redis.config;

import com.chenjie.redis.StarterRedisTemplate;
import com.chenjie.redis.constant.RedisStarterConstant;
import com.chenjie.redis.serializer.RedisKeySerializer;
import com.chenjie.redis.serializer.RedisValueJacksonSerializer;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.lettuce.core.cluster.ClusterClientOptions;
import io.lettuce.core.cluster.ClusterTopologyRefreshOptions;
import io.lettuce.core.resource.ClientResources;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettucePoolingClientConfiguration;
import org.springframework.data.redis.serializer.RedisSerializationContext;

import java.time.Duration;

/**
 * @Description redis自动配置类
 */
@Slf4j
@Configuration
@EnableConfigurationProperties(PrefixConfig.class)
public class RedisServiceAutoConfigure {

    @Autowired
    private RedisProperties redisProperties;

    @Autowired
    private PrefixConfig prefixConfig;

    @Autowired
    private KubernetesClient kubernetesClient;

    /**
     * key 的序列化器
     */
    private RedisKeySerializer keyRedisSerializer;

    /**
     * value 的序列化器
     */
    private RedisValueJacksonSerializer valueRedisSerializer = new RedisValueJacksonSerializer();

    @Bean
    public CacheManager cacheManager(LettuceConnectionFactory redisConnectionFactory) {
        // RedisCacheWriter
        RedisCacheWriter redisCacheWriter = RedisCacheWriter.nonLockingRedisCacheWriter(redisConnectionFactory);
        // RedisCacheConfiguration - 值的序列化方式
        RedisSerializationContext.SerializationPair<Object> serializationPair = RedisSerializationContext.SerializationPair.fromSerializer(valueRedisSerializer);
        RedisCacheConfiguration redisCacheConfiguration = RedisCacheConfiguration.defaultCacheConfig().serializeValuesWith(serializationPair);

        return new RedisCacheManager(redisCacheWriter, redisCacheConfiguration);
    }


    private GenericObjectPoolConfig getPoolConfig(RedisProperties.Pool properties) {
        GenericObjectPoolConfig config = new GenericObjectPoolConfig();
        config.setMaxTotal(properties.getMaxActive());
        config.setMaxIdle(properties.getMaxIdle());
        config.setMinIdle(properties.getMinIdle());
        if (properties.getMaxWait() != null) {
            config.setMaxWaitMillis(properties.getMaxWait().toMillis());
        }

        return config;
    }

    @Bean
    public RedisClusterConfiguration getClusterConfig() {
        //添加redis 集群节点信息
        RedisClusterConfiguration rcc = new RedisClusterConfiguration(redisProperties.getCluster().getNodes());
        rcc.setPassword(RedisPassword.of(redisProperties.getPassword()));
        rcc.setMaxRedirects(100);
        return rcc;
    }

    @Bean
    public LettuceConnectionFactory redisConnectionFactory(RedisClusterConfiguration cluster, LettuceClientConfiguration lettuceClientConfiguration) {
        return new LettuceConnectionFactory(cluster, lettuceClientConfiguration);
    }

    @Bean
    public LettuceClientConfiguration lettuceClientConfiguration(ClientResources clientResources) {

        ClusterTopologyRefreshOptions clusterTopologyRefreshOptions = ClusterTopologyRefreshOptions.builder()
                //.enablePeriodicRefresh(Duration.ofSeconds(5))
                // 开启全部自适应刷新 自适应刷新不开启,Redis集群变更时将会导致连接异常
                .enableAllAdaptiveRefreshTriggers()
                .adaptiveRefreshTriggersTimeout(Duration.ofSeconds(10))
                // 开周期刷新
                //.enablePeriodicRefresh(Duration.ofSeconds(10))
                .build();

        ClusterClientOptions clusterClientOptions = ClusterClientOptions.builder()
                //.autoReconnect(false)  是否自动重连
                //.pingBeforeActivateConnection(Boolean.TRUE)
                //.cancelCommandsOnReconnectFailure(Boolean.TRUE)
                //.disconnectedBehavior(ClientOptions.DisconnectedBehavior.REJECT_COMMANDS)
                .topologyRefreshOptions(clusterTopologyRefreshOptions).build();
        //没有配置的话，则默认为3s
        if (redisProperties.getTimeout() == null) {
            redisProperties.setTimeout(Duration.ofSeconds(3L));
        }
        return LettucePoolingClientConfiguration.builder().commandTimeout(redisProperties.getTimeout())
                .poolConfig(getPoolConfig(redisProperties.getLettuce().getPool()))
                .clientResources(clientResources)
                .clientOptions(clusterClientOptions)
                .build();
    }

    @Bean
    public StarterRedisTemplate<String, Object> redisTemplate(LettuceConnectionFactory redisConnectionFactory) {
        StarterRedisTemplate<String, Object> starterRedisTemplate = new StarterRedisTemplate<>();

        // 配置连接工厂
        starterRedisTemplate.setConnectionFactory(redisConnectionFactory);
        // 值序列化-RedisFastJsonSerializer
        starterRedisTemplate.setValueSerializer(valueRedisSerializer);
        starterRedisTemplate.setHashValueSerializer(valueRedisSerializer);
        // 键序列化-StringRedisSerializer
        String localNamespace = chooseLocalNamespace();
        Boolean namespaceSwitch = prefixConfig.getNamespaceSwitch();
        this.keyRedisSerializer = new RedisKeySerializer(localNamespace, RedisStarterConstant.publicNamespace, namespaceSwitch);
        log.info("RedisStarter  ====>  localNamespace: {}   publicNamespace: {}", localNamespace, RedisStarterConstant.publicNamespace);
        starterRedisTemplate.setKeySerializer(keyRedisSerializer);
        starterRedisTemplate.setHashKeySerializer(keyRedisSerializer);

        return starterRedisTemplate;
    }

    private String chooseLocalNamespace() {
        String namespace = "local";

        // 获取配置文件中配的localNamespace
        String localNamespace = prefixConfig.getLocalNamespace();
        // 配置文件里配的localNamespace优先级最高
        if (localNamespace != null && !"".equals(localNamespace)) {
            namespace = localNamespace;
        } else {
            // 获取k8s命名空间
            String k8sNamespace = kubernetesClient.getNamespace();
            if (k8sNamespace != null) {
                namespace = k8sNamespace;
            }
        }

        return namespace;
    }

}
