package io.stayhungrystayfoolish.redisson.resource;

import io.lettuce.core.RedisClient;
import io.stayhungrystayfoolish.redisson.domain.CallableTask;
import io.stayhungrystayfoolish.redisson.domain.RunnableTask;
import io.stayhungrystayfoolish.redisson.domain.User;
import org.redisson.Redisson;
import org.redisson.RedissonNode;
import org.redisson.api.*;
import org.redisson.config.Config;
import org.redisson.config.RedissonNodeConfig;
import org.redisson.spring.starter.RedissonAutoConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.Serializable;
import java.util.Collections;
import java.util.concurrent.*;
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

    static AtomicInteger success = new AtomicInteger();
    static AtomicInteger failed = new AtomicInteger();

    @GetMapping("/executor")
    public void executor() {
        RExecutorService service = client.getExecutorService("task");
        service.submit(new TaskTest());
        service.shutdown();
    }

    public static class TaskTest implements Runnable, Serializable {

        @Autowired
        private RedissonClient client;

        @Override
        public void run() {
            RBucket<String> bucket = client.getBucket("bucket");
            bucket.set("kkkkkkkkkkkkkkkkkkkkkkk");
            System.out.println("=================" + bucket.get());
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
        }
    }

    @GetMapping("/test")
    public void task() {
        ExecutorOptions options = ExecutorOptions.defaults();
        // 指定重新尝试执行任务的时间间隔。
        // ExecutorService的工作节点将等待10分钟后重新尝试执行任务
        // 指定重新尝试执行任务的时间间隔。
        // ExecutorService的工作节点将等待10分钟后重新尝试执行任务
        // 默认值为5分钟
        options.taskRetryInterval(30, TimeUnit.SECONDS);
        RExecutorService executorService = client.getExecutorService("myExecutor", options);
        executorService.submit(new RunnableTask(123));
    }

    public static void main(final String[] args) throws InterruptedException, ExecutionException {
        final Config config = new Config();
        config.useSingleServer()
                .setAddress("redis://127.0.0.1:6379")
                .setConnectionMinimumIdleSize(1)
                .setConnectionPoolSize(2);



        final RedissonNodeConfig nodeConfig = new RedissonNodeConfig(config);
        nodeConfig.setExecutorServiceWorkers(Collections.singletonMap("myExecutor", 1));
        final RedissonNode node = RedissonNode.create(nodeConfig);
        node.start();

        final RedissonClient client = Redisson.create(config);
        ExecutorOptions options = ExecutorOptions.defaults();
        // 指定重新尝试执行任务的时间间隔。
        // ExecutorService的工作节点将等待10分钟后重新尝试执行任务
        // 指定重新尝试执行任务的时间间隔。
        // ExecutorService的工作节点将等待10分钟后重新尝试执行任务
        // 默认值为5分钟
        options.taskRetryInterval(30, TimeUnit.SECONDS);
        final RExecutorService e = client.getExecutorService("myExecutor", options);
        System.out.println("executorService.isShutdown :" + e.isShutdown());
        System.out.println("executorService.isTerminated :" + e.isTerminated());
        //e.execute(new RunnableTask());
        final Future<String> result = e.submit(new CallableTask());
        System.out.println("result = " + result.get());
        e.shutdown();

        node.shutdown();
    }
}
