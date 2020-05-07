package io.stayhungrystayfoolish.redis.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;
import io.lettuce.core.cluster.ClusterClientOptions;
import io.lettuce.core.cluster.ClusterTopologyRefreshOptions;
import io.lettuce.core.cluster.RedisClusterClient;
import io.lettuce.core.cluster.api.StatefulRedisClusterConnection;
import io.lettuce.core.cluster.api.async.RedisClusterAsyncCommands;
import io.lettuce.core.cluster.api.sync.RedisAdvancedClusterCommands;
import io.lettuce.core.cluster.api.sync.RedisClusterCommands;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.RedisClusterConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @Author: Created by bonismo@hotmail.com on 2020/4/28 2:39 下午
 * @Description:
 * @Version: 1.0
 */
@EnableCaching
@ConditionalOnClass(RedisOperations.class)
@EnableConfigurationProperties(RedisProperties.class)
@Configuration
@ConditionalOnProperty(name = "spring.redis.type", havingValue = "cluster")
public class ClusterRedisConfiguration {

    private final Logger logger = LoggerFactory.getLogger(ClusterRedisConfiguration.class);

    // user spring component
    @Autowired
    private RedisProperties redisProperties;

    @Bean(name = "connectionFactory")
    public RedisConnectionFactory connectionFactory() {
        logger.debug("Configuring Redis Cluster .");
        RedisClusterConfiguration configuration = new RedisClusterConfiguration(redisProperties.getCluster().getNodes());
        configuration.setPassword(RedisPassword.of(redisProperties.getPassword()));
        return new LettuceConnectionFactory(configuration);
    }

    /**
     *  Enabling adaptive cluster topology view updates
     *  @Description: https://github.com/lettuce-io/lettuce-core/wiki/Redis-Cluster
     */
    @Bean
    public RedisClusterClient redisClusterClient(RedisConnectionFactory redisConnectionFactory) {
        List<RedisURI> redisURIS = new ArrayList<>();
        List<String> nodes = redisProperties.getCluster().getNodes();
        for (String node : nodes) {
            redisURIS.add(configRedisURI(node));
        }
        RedisClusterClient redisClusterClient = RedisClusterClient.create(redisURIS);
        ClusterTopologyRefreshOptions topologyRefreshOptions = ClusterTopologyRefreshOptions.builder()
                .enableAdaptiveRefreshTrigger(ClusterTopologyRefreshOptions.RefreshTrigger.MOVED_REDIRECT, ClusterTopologyRefreshOptions.RefreshTrigger.PERSISTENT_RECONNECTS)
                .adaptiveRefreshTriggersTimeout(30, TimeUnit.SECONDS)
                .build();
        redisClusterClient.setOptions(
                ClusterClientOptions.
                        builder().
                        topologyRefreshOptions(topologyRefreshOptions).
                        build());
        return redisClusterClient;
    }

    private RedisURI configRedisURI(String node) {
        String host = node.split(":")[0];
        int port = Integer.parseInt(node.split(":")[1]);
        return RedisURI.Builder.redis(host, port)
                .withPassword(redisProperties.getPassword())
                .build();
    }

    @Bean(destroyMethod = "close")
    public StatefulRedisClusterConnection<String, String> connection(RedisClusterClient redisClient) {
        return redisClient.connect();
    }

    @Bean(name = "redisAdvancedClusterCommands")
    public RedisAdvancedClusterCommands<String, String> clusterAdvanceCommands(StatefulRedisClusterConnection connection) {
        return connection.sync();
    }

    @Bean(name = "redisClusterCommands")
    public RedisClusterCommands<String, String> clusterCommands(StatefulRedisClusterConnection connection) {
        return connection.sync();
    }

    @Bean(name = "redisClusterAsyncCommands")
    public RedisClusterAsyncCommands<String, String> commands(StatefulRedisClusterConnection connection) {
        return connection.async();
    }


    @Bean
    @ConditionalOnMissingBean(name = "{redisTemplate}")
    public RedisTemplate<Object, Object> redisTemplate(@Qualifier(value = "connectionFactory") RedisConnectionFactory redisConnectionFactory) {
        logger.info("Initialization Cluster Redis RedisTemplate");
        RedisTemplate<Object, Object> template = new RedisTemplate<>();
        template.setKeySerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());

        ObjectMapper mapper = new ObjectMapper().
                registerModule(new ParameterNamesModule()).
                registerModule(new Jdk8Module()).
                registerModule(new JavaTimeModule());
        mapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        // this method is deprecated,use activateDefaultTyping in {Spring 2.2.2.RELEASE}
        mapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
//        mapper.activateDefaultTyping(LaissezFaireSubTypeValidator.instance, ObjectMapper.DefaultTyping.NON_FINAL);
        GenericJackson2JsonRedisSerializer genericJackson2JsonRedisSerializer = new GenericJackson2JsonRedisSerializer(mapper);

        template.setKeySerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(genericJackson2JsonRedisSerializer);
        template.setHashValueSerializer(genericJackson2JsonRedisSerializer);
        template.setConnectionFactory(redisConnectionFactory);
        template.afterPropertiesSet();
        return template;
    }

    @Bean
    @ConditionalOnMissingBean
    public StringRedisTemplate stringRedisTemplate(RedisConnectionFactory redisConnectionFactory) {
        StringRedisTemplate template = new StringRedisTemplate();
        template.setConnectionFactory(redisConnectionFactory);
        return template;
    }

    @Bean(name = "redisCacheManager")
    public CacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        logger.info(" Initialization RedisCacheManager .");
        // no expire time
//        RedisCacheManager cacheManager = RedisCacheManager.builder(connectionFactory).build();
//         expire time
        RedisCacheManager cacheManager = RedisCacheManager
                .builder(connectionFactory)
                .cacheDefaults(RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofDays(1)))
                .transactionAware()
                .build();
        return cacheManager;
    }
}
