package io.stayhungrystayfoolish.redisson.resource;

import io.stayhungrystayfoolish.redisson.executor.RunnableTask;
import io.stayhungrystayfoolish.redisson.executor.Schedule;
import org.redisson.Redisson;
import org.redisson.RedissonNode;
import org.redisson.api.ExecutorOptions;
import org.redisson.api.RExecutorFuture;
import org.redisson.api.RScheduledExecutorService;
import org.redisson.api.RedissonClient;
import org.redisson.config.ClusterServersConfig;
import org.redisson.config.Config;
import org.redisson.config.RedissonNodeConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.sql.SQLOutput;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * @Author: Created by bonismo@hotmail.com on 2020/7/10 5:11 下午
 * @Description:
 * @Version: 1.0
 */
@RestController
@RequestMapping("/api")
public class ScheduleWorkerResource {

    private final Logger logger = LoggerFactory.getLogger(ScheduleWorkerResource.class);

    private final RedissonClient client;

    public ScheduleWorkerResource(RedissonClient client) {
        this.client = client;
    }

    @GetMapping("/publish-task")
    public void publishTask() throws IOException, ExecutionException, InterruptedException {
        Config config = Config.fromYAML(Schedule.class.getClassLoader().getResource("redisson.yml"));
        ClusterServersConfig clusterServersConfig = config.useClusterServers();

        clusterServersConfig.getNodeAddresses().stream().forEach(s -> System.out.println("Node : "+s));

        ExecutorOptions option = ExecutorOptions.defaults();
        option.taskRetryInterval(10, TimeUnit.MILLISECONDS);
        RScheduledExecutorService executorService = client.getExecutorService("JobA",option);
        // submit JobA
        RExecutorFuture<?> future = executorService
                .submit(new RunnableTask());
//        System.out.println("=================================================="+future.get());
//        executorService.shutdown();
    }

    @GetMapping("/consumer-task")
    public void consumerTask() throws IOException, ExecutionException, InterruptedException {
        Config config = Config.fromYAML(Schedule.class.getClassLoader().getResource("redisson.yml"));
        RedissonNodeConfig nodeConfig = new RedissonNodeConfig(config);
        nodeConfig.getExecutorServiceWorkers().put("JobA", 1);
        RedissonNode node = RedissonNode.create(nodeConfig);
        node.start();
//        node.shutdown();
    }
}
