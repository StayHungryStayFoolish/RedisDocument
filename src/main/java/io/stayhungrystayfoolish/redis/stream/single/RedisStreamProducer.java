package io.stayhungrystayfoolish.redis.stream.single;

import io.lettuce.core.RedisFuture;
import io.lettuce.core.SetArgs;
import io.lettuce.core.api.async.RedisStringAsyncCommands;
import io.lettuce.core.api.sync.RedisCommands;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.security.PermitAll;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * @Author: Created by bonismo@hotmail.com on 2020/4/30 3:42 下午
 * @Description: 1. use @ConditionalOnBean(name = "redisCommands")
 * *                       when spring.redis.type = single ,this api can access (type = cluster can not access)
 * *               2. use @Autowired(required = false) when spring.redis.type = single / cluster
 * *                       this api can access ,but only type is single can works (type = cluster can access but not work)
 * @Version: 1.0
 */
@RestController
@RequestMapping("/api")
public class RedisStreamProducer {

    private final Logger logger = LoggerFactory.getLogger(RedisStreamProducer.class);

    private final static String STREAMS_KEY = "STREAMS_SINGLE:test";

    private final RedisTemplate redisTemplate;

    private final RedisCommands<String, String> syncCommands;
    private final RedisStringAsyncCommands<String, String> asyncCommands;

    public RedisStreamProducer(RedisTemplate redisTemplate, @Autowired(required = false) RedisCommands<String, String> syncCommands, @Autowired(required = false) RedisStringAsyncCommands<String, String> asyncCommands) {
        this.redisTemplate = redisTemplate;
        this.syncCommands = syncCommands;
        this.asyncCommands = asyncCommands;
    }


    @GetMapping("/single/streams/producer/{count}")
    public String singleStreamsMessageProducer(@PathVariable int count) {
        logger.info("Single Redis Producer Message : {}", count);
        StringBuffer result = new StringBuffer();

        for (int i = 0; i < count; i++) {
            Map<String, String> messageBody = new HashMap<>();
            messageBody.put("speed", "15");
            messageBody.put("direction", "270");
            messageBody.put("sensor_ts", Instant.now().toString());
            messageBody.put("loop_info", String.valueOf(i));

            String messageId = syncCommands.xadd(
                    STREAMS_KEY,
                    messageBody);
            String format = String.format("Message %s : %s posted \n", messageId, messageBody);
            result.append(format);
        }
        logger.info("Producer Result : \n{}", result);
        return result.toString();
    }

    @GetMapping("/single/async/{key}/{value}")
    public String singleAsync(@PathVariable String key, @PathVariable String value) throws ExecutionException, InterruptedException {
        SetArgs args = new SetArgs();
        asyncCommands.set(key, value);
        RedisFuture<String> redisFuture = asyncCommands.get(key);
        return redisFuture.get();
    }
}
