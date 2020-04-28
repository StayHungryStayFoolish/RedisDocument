package io.stayhungrystayfoolish.redis.resource;

import io.stayhungrystayfoolish.redis.domain.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @Author: Created by bonismo@hotmail.com on 2020/4/28 4:15 下午
 * @Description:
 * @Version: 1.0
 */
@RestController
@RequestMapping("/api")
public class RedisResource {

    private final Logger logger = LoggerFactory.getLogger(RedisResource.class);

    private final RedisTemplate redisTemplate;

    public RedisResource(RedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @GetMapping("/redis")
    public User getRedisCache() {
        User user = new User();
        user.setName("bonismo");
        user.setAge(20);
        user.setGrades(Stream.of("1","2","3").collect(Collectors.toList()));
        user.setMarryDate(Instant.now());
        redisTemplate.opsForValue().set("aaa", user);
        user = (User) redisTemplate.opsForValue().get("aaa");
        System.out.println("User ："+user.toString());

        LinkedHashMap map = new LinkedHashMap<>();
        map.put("11", 11);

        redisTemplate.opsForValue().set("1", map);
        LinkedHashMap result = (LinkedHashMap) redisTemplate.opsForValue().get("1");
        System.out.println(result);
        return user;
    }
}
