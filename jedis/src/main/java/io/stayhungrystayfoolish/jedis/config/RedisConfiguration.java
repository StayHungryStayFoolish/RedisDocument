package io.stayhungrystayfoolish.jedis.config;

import org.redisson.config.Config;
import org.redisson.config.RedissonNodeConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.Jedis;

import java.io.IOException;

/**
 * @Author: Created by bonismo@hotmail.com on 2020/7/9 1:55 下午
 * @Description:
 * @Version: 1.0
 */
@Configuration
public class RedisConfiguration {

//    @Bean
//    public Jedis jedis() {
//        Jedis jedis = new Jedis("", 11);
//        return jedis;
//    }

    @Bean
    public RedissonNodeConfig redissonNodeConfig() throws IOException {
        Config config = Config.fromYAML(this.getClass().getClassLoader().getResource("redisson.yml"));
        return new RedissonNodeConfig(config);
    }

}
