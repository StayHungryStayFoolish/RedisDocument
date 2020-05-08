package io.stayhungrystayfoolish.redis.stream.cluster;

import io.lettuce.core.cluster.api.sync.RedisAdvancedClusterCommands;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    private final static String STREAMS_KEY = "STREAMS_CLUSTER:test";

    private final RedisTemplate redisTemplate;

    private final RedisAdvancedClusterCommands<String, String> advancedClusterCommands;

    public ClusterStreamProducer(RedisTemplate redisTemplate, @Autowired(required = false) RedisAdvancedClusterCommands<String, String> advancedClusterCommands) {
        this.redisTemplate = redisTemplate;
        this.advancedClusterCommands = advancedClusterCommands;
    }

    @GetMapping("/cluster/{key}/{value}")
    public String cluster2Set(@PathVariable String key,@PathVariable String value) {
        redisTemplate.opsForValue().set(key, value);
        String result = (String) redisTemplate.opsForValue().get(key);
        return result;
    }

    @GetMapping("/cluster/streams/producer/{count}")
    public String clusterStreamsMessageProducer(@PathVariable int count) {
        logger.info("Single Redis Producer Message : {}", count);
        StringBuffer result = new StringBuffer();

        for (int i = 0; i < count; i++) {
            Map<String, String> messageBody = new HashMap<>();
            messageBody.put("speed", "15");
            messageBody.put("direction", "270");
            messageBody.put("sensor_ts", Instant.now().toString());
            messageBody.put("loop_info", String.valueOf(i));
            String messageId = advancedClusterCommands.xadd(
                    STREAMS_KEY,
                    messageBody);
            String format = String.format("Message %s : %s posted \n", messageId, messageBody);
            result.append(format);
        }
        logger.info("Producer Result : \n{}", result);
        List<String> keys = advancedClusterCommands.keys("*");
        for (String key : keys) {
            logger.info("Key : {}", key);
        }
        return result.toString();
    }
}
