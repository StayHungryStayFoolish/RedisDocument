package io.stayhungrystayfoolish.redis.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;

/**
 * @Author: Created by bonismo@hotmail.com on 2020/4/28 2:39 下午
 * @Description:
 * @Version: 1.0
 */
@Configuration
@ConditionalOnProperty(name = "spring.redis.type", havingValue = "cluster")
public class RedisClusterConfiguration {


}
