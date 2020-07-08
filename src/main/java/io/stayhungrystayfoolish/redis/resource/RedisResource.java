package io.stayhungrystayfoolish.redis.resource;

import io.stayhungrystayfoolish.redis.domain.User;
import org.redisson.Redisson;
import org.redisson.RedissonFairLock;
import org.redisson.RedissonLock;
import org.redisson.api.*;
import org.redisson.config.Config;
import org.redisson.redisnode.RedissonClusterNodes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.TimeUnit;
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

    @Autowired
    private RedissonClient client;

    public RedisResource(RedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @GetMapping("/redis")
    public User getRedisCache() {
        RLock lock = client.getLock("1");
        lock.lock(10, TimeUnit.SECONDS);

        User user = new User();
        user.setName("bonismo");
        user.setAge(20);
        user.setGrades(Stream.of("1", "2", "3").collect(Collectors.toList()));
        user.setMarryDate(Instant.now());
        redisTemplate.opsForValue().set("aaa", user);
        user = (User) redisTemplate.opsForValue().get("aaa");
        System.out.println("User ：" + user.toString());

        LinkedHashMap map = new LinkedHashMap<>();
        map.put("11", 11);

        redisTemplate.opsForValue().set("1", map);
        LinkedHashMap result = (LinkedHashMap) redisTemplate.opsForValue().get("1");
        System.out.println(result);
        return user;
    }

    @GetMapping("/lock")
    public void lock() {
        RLock lock = client.getLock("lock");
        lock.lock(300, TimeUnit.SECONDS);
        Thread thread = Thread.currentThread();
        Long id = thread.getId();
        System.out.println("Lock Resource" + id);
        lock.unlock();
    }

    @GetMapping("/multi-lock")
    public void mulitLock() {
        RLock lock = client.getLock("lock");
        lock.lock(300, TimeUnit.SECONDS);
        Thread thread = Thread.currentThread();
        Long id = thread.getId();
        System.out.println("Lock Resource" + id);
        lock.unlock();
    }

    @GetMapping("/test")
    public void test() {
        RLock lock = client.getLock("lock");
        lock.lock(1000, TimeUnit.SECONDS);
        Thread thread = Thread.currentThread();
        Long id = thread.getId();
        // perform some long operations...
        System.out.println("Lock Resource" + id);
        lock.unlock();
        RSet<String> ledgerSet = client.getSet("ledgerSet");
        ledgerSet.add("1111");
        String name = ledgerSet.getName();
        System.out.println(name);

        RBitSet set = client.getBitSet("bitmap");
        set.set(0, true);
        System.out.println(set.get(0));
        String nam = set.getName();
        System.out.println(nam);

        RBucket<User> bucket = client.getBucket("bucket");
        User user = new User();
        user.setName("boni");
        user.setAge(19);
        user.setMarryDate(Instant.now());
        user.setGrades(Collections.singletonList("1"));

        bucket.set(user);
        User s = bucket.get();
        System.out.println(s);
    }

    @GetMapping("/bitmap/{index}")
    public String bitMap(@PathVariable long index) {
        String key = "bitmap";
        RBitSet bitSet = client.getBitSet(key);
        bitSet.set(index, true);
        return "ok";
    }

    @GetMapping("/list/{ele}")
    public List<String> bitMap(@PathVariable String ele) {
        String key = "list";
        RDeque deque = client.getDeque(key);
        deque.add(ele);
        RList list = client.getList(key);
        List<String> result = list.readAll();
        return result;
    }

    @GetMapping("/hash/{key}/{value}")
    public String hash(@PathVariable String key, @PathVariable String value) {

        RMap<String, String> map = client.getMap("hashMap");
        map.put(key, value);
        String result = map.get(key);
        Set<Map.Entry<String, String>> entrySet = map.entrySet();
        for (Map.Entry<String, String> stringStringEntry : entrySet) {
            System.out.println(stringStringEntry.getKey());
            System.out.println(stringStringEntry.getValue());
        }
        return result;
    }
}
