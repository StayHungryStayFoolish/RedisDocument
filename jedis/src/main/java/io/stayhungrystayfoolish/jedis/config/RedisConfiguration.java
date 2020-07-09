package io.stayhungrystayfoolish.jedis.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.Jedis;

/**
 * @Author: Created by bonismo@hotmail.com on 2020/7/9 1:55 下午
 * @Description:
 * @Version: 1.0
 */
@Configuration
public class RedisConfiguration {

    @Bean
    public Jedis jedis() {
        Jedis jedis = new Jedis("", 11);
        return jedis;
    }
}
