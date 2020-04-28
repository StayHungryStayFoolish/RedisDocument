package io.stayhungrystayfoolish.redis.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * @Author: Created by bonismo@hotmail.com on 2020/4/28 2:41 下午
 * @Description:
 * @Version: 1.0
 */
@Component
@ConditionalOnProperty(prefix = "spring.redis.cluster")
public class RedisClusterProperties {
}
