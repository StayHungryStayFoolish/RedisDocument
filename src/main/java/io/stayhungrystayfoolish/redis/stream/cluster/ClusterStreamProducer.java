package io.stayhungrystayfoolish.redis.stream.cluster;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author: Created by bonismo@hotmail.com on 2020/5/6 7:33 下午
 * @Description: 1. use @ConditionalOnBean(name = "redisCommands")
 *  *                  when spring.redis.type = single ,this api can access (type = cluster can not access)
 *  *            2. use @Autowired(required = false) when spring.redis.type = single / cluster
 *  *                  this api can access ,but only type is single can works (type = cluster can access but not work)
 * @Version: 1.0
 */
@RestController
@RequestMapping("/api")
public class ClusterStreamProducer {

    private final Logger logger = LoggerFactory.getLogger(ClusterStreamProducer.class);

    private final RedisTemplate redisTemplate;

    public ClusterStreamProducer(RedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @GetMapping("/cluster/{key}/{value}")
    public String cluster2Set(@PathVariable String key,@PathVariable String value) {
        redisTemplate.opsForValue().set(key, value);
        String result = (String) redisTemplate.opsForValue().get(key);
        return result;
    }
}
