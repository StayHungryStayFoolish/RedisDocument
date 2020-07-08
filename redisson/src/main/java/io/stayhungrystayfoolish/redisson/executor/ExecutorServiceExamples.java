package io.stayhungrystayfoolish.redisson.executor;

import org.redisson.Redisson;
import org.redisson.RedissonNode;
import org.redisson.api.ExecutorOptions;
import org.redisson.api.RExecutorService;
import org.redisson.api.RScheduledExecutorService;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.redisson.config.RedissonNodeConfig;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.Serializable;
import java.util.Collections;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api")
public class ExecutorServiceExamples {

    @GetMapping("/consumer")
    public void consumer() throws InterruptedException, ExecutionException {
        final Config config = new Config();
        config.useSingleServer()
                .setAddress("redis://127.0.0.1:6379")
                .setConnectionMinimumIdleSize(1)
                .setConnectionPoolSize(2);

        final RedissonNodeConfig nodeConfig = new RedissonNodeConfig(config);
        nodeConfig.setExecutorServiceWorkers(Collections.singletonMap("myExecutor", 5));
        final RedissonNode node = RedissonNode.create(nodeConfig);
        node.start();

        final RedissonClient client = Redisson.create(config);
        ExecutorOptions options = ExecutorOptions.defaults();
        // 指定重新尝试执行任务的时间间隔。
        // ExecutorService的工作节点将等待10分钟后重新尝试执行任务
        // 设定为0则不进行重试
        // 默认值为5分钟
        options.taskRetryInterval(20, TimeUnit.SECONDS);
//        final RExecutorService e = client.getExecutorService("myExecutor", options);
        RScheduledExecutorService e = client.getExecutorService("es", options);
        e.schedule(new CallableTask(), 1, TimeUnit.SECONDS, 2, TimeUnit.SECONDS);
        System.out.println("executorService.isShutdown ===================== : " + e.isShutdown());
        System.out.println("executorService.isTerminated ====================== :" + e.isTerminated());
//        e.execute(new RunnableTask());
//		final Future<String> result = e.submit(new CallableTask());
//		System.out.println("result =" + result.get());
        if (e.isShutdown()) {
            e.shutdown();
            node.shutdown();
        }
    }

    public static void main(String[] args) {
        final Config config = new Config();
        config.useSingleServer()
                .setAddress("redis://127.0.0.1:6379")
                .setConnectionMinimumIdleSize(1)
                .setConnectionPoolSize(2);

        final RedissonNodeConfig nodeConfig = new RedissonNodeConfig(config);
        nodeConfig.setExecutorServiceWorkers(Collections.singletonMap("myExecutor", 5));
        final RedissonNode node = RedissonNode.create(nodeConfig);
        node.start();

        final RedissonClient client = Redisson.create(config);
        ExecutorOptions options = ExecutorOptions.defaults();
        // 指定重新尝试执行任务的时间间隔。
        // ExecutorService的工作节点将等待10分钟后重新尝试执行任务
        // 设定为0则不进行重试
        // 默认值为5分钟
        options.taskRetryInterval(20, TimeUnit.SECONDS);
        final RExecutorService e = client.getExecutorService("myExecutor", options);
//        RScheduledExecutorService e = client.getExecutorService("myExecutor", options);
//        e.schedule(new CallableTask(), 1, TimeUnit.SECONDS, 2, TimeUnit.SECONDS);
        System.out.println("executorService.isShutdown ===================== : " + e.isShutdown());
        System.out.println("executorService.isTerminated ====================== :" + e.isTerminated());
        e.execute(new RunnableTask());
//		final Future<String> result = e.submit(new CallableTask());
//		System.out.println("result =" + result.get());
        if (e.isShutdown()) {
            e.shutdown();
            node.shutdown();
        }
    }
}
