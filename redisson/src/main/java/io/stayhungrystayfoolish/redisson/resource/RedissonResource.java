package io.stayhungrystayfoolish.redisson.resource;

import io.stayhungrystayfoolish.redisson.domain.User;
import org.redisson.api.RBucket;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.redisson.spring.starter.RedissonAutoConfiguration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.Serializable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Author: Created by bonismo@hotmail.com on 2020/7/8 2:52 下午
 * @Description:
 * @Version: 1.0
 */
@RestController
@RequestMapping("/api")
public class RedissonResource {

    private final RedissonClient client;

    private final RedisTemplate template;

    public RedissonResource(RedissonClient client, RedisTemplate template) {
        this.client = client;
        this.template = template;
    }

    @GetMapping("/add")
    public User redisssonAdd() {
        User user = new User("boni", 20);
        RBucket<User> bucket = client.getBucket("k1");
        bucket.set(user);
        return bucket.get();
    }

    @GetMapping("/lock")
    public void templateAdd() {
        ExecutorService service = Executors.newFixedThreadPool(10);
        AtomicInteger success = new AtomicInteger();
        AtomicInteger failed = new AtomicInteger();
        for (int i = 0; i < 10; i++) {
            service.execute(() -> {
                RLock lock = client.getLock("lock-test");
                try {
                    boolean res = lock.tryLock(30, 10, TimeUnit.SECONDS);
                    if (res) {
                        System.out.println("获取锁成功 " + success.incrementAndGet() + " 次");
                    }
                } catch (Exception e) {
                    System.out.println("获取锁失败 " + failed.incrementAndGet() + " 次");
                    e.printStackTrace();
                } finally {
                    lock.unlock();
                }
            });
        }
        service.shutdown();
    }
}
