package io.stayhungrystayfoolish.redis.stream;

import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author: Created by bonismo@hotmail.com on 2020/4/30 3:42 下午
 * @Description:
 * @Version: 1.0
 */
@RestController
@RequestMapping("/api")
public class RedisStreamProducer {

    private final Logger logger = LoggerFactory.getLogger(RedisStreamProducer.class);


    private final static String STREAMS_KEY = "STREAMS:test";

//    @Autowired
//    Jedis jedis;

    @Autowired
    RedisClient redisClient;

    private final RedisTemplate redisTemplate;

    public RedisStreamProducer(RedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }


    @GetMapping("/streams/{count}")
    public String streamMessage(@PathVariable int count) {

        StringBuffer result = new StringBuffer();
        StatefulRedisConnection<String, String> connection = redisClient.connect();
        RedisCommands<String, String> syncCommands = connection.sync();

        for (int i = 0; i < count; i++) {

            Map<String, String> messageBody = new HashMap<>();
            messageBody.put("speed", "15");
            messageBody.put("direction", "270");
            messageBody.put("sensor_ts", String.valueOf(System.currentTimeMillis()));

            String messageId = syncCommands.xadd(
                    "weather_sensor:wind",
                    messageBody);
            String format = String.format("Message %s : %s posted", messageId, messageBody);
            result.append(format);
            System.out.println(result);

        }
        connection.close();
        redisClient.shutdown();
        return result.toString();
    }
}
