package io.stayhungrystayfoolish.jedis.resource;

import io.stayhungrystayfoolish.jedis.executor.CallableTask;
import io.stayhungrystayfoolish.jedis.executor.RunnableTask;
import org.redisson.RedissonNode;
import org.redisson.api.*;
import org.redisson.config.RedissonNodeConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * @Author: Created by bonismo@hotmail.com on 2020/7/10 5:11 下午
 * @Description:
 * @Version: 1.0
 */
@RestController
@RequestMapping("/api")
public class ScheduleTaskResource {

    private final Logger logger = LoggerFactory.getLogger(ScheduleTaskResource.class);

    private static final String STANDARD_TASK_KEY = "STANDARD_TASK_KEY";
    private static final String SCHEDULE_TASK_KEY = "SCHEDULE_TASK_KEY";

    private Map<String, String> taskIdMap = new HashMap<>();

    private final RedissonNodeConfig nodeConfig;

    private final RedissonClient client;

    public ScheduleTaskResource(RedissonNodeConfig nodeConfig, RedissonClient client) {
        this.nodeConfig = nodeConfig;
        this.client = client;
    }

//------- 标准任务 ------------------------------------------------------------------------------------------------------

    @GetMapping("/publish-standard-task")
    public void publishTask() throws ExecutionException, InterruptedException {
        logger.info("PUBLISH-STANDARD-TASK > ");
        ExecutorOptions option = ExecutorOptions.defaults();
        option.taskRetryInterval(10, TimeUnit.SECONDS);
        RExecutorService executorService = client.getExecutorService(STANDARD_TASK_KEY, option);
        // 异步执行
        RExecutorFuture<?> callableFuture = executorService.submit(new CallableTask());
        String taskd = callableFuture.getTaskId();
        taskIdMap.put(STANDARD_TASK_KEY, taskd);
        // 同步执行
        executorService.execute(new RunnableTask());
        logger.info("CallableTask Finished Result : " + callableFuture.get());
    }

    @GetMapping("/finish-standard-task")
    public void finishStandardTask() {
        nodeConfig.getExecutorServiceWorkers().put(STANDARD_TASK_KEY, 10);
        RedissonNode node = RedissonNode.create(nodeConfig);
        node.start();
    }

    @GetMapping("/cancel-standard-task")
    public Boolean cancelStandardTask() {
        RScheduledExecutorService executorService = client.getExecutorService(STANDARD_TASK_KEY);
        String taskId = taskIdMap.get(STANDARD_TASK_KEY);
        logger.info("Task ID : " + taskId);
        return executorService.cancelTask(taskId);
    }

//------- 定时任务 ------------------------------------------------------------------------------------------------------

    @GetMapping("/publish-schedule-task")
    public void publishScheduleTask() {
        logger.info("PUBLISH-SCHEDULE-TASK > ");
        RScheduledExecutorService executorService = client.getExecutorService(SCHEDULE_TASK_KEY);
        // 延迟 5 秒执行，15 秒后循环执行
//        RScheduledFuture<?> future = executorService.scheduleAtFixedRate(new RunnableTask(), 5, 15, TimeUnit.SECONDS);
        // 不会再次执行
        RScheduledFuture<?> future1 = executorService.schedule(new CallableTask(), 5, TimeUnit.SECONDS);
        String taskId = future1.getTaskId();
        logger.info("Task ID : " + taskId);
        taskIdMap.put(SCHEDULE_TASK_KEY, taskId);
    }

    @GetMapping("/finish-schedule-task")
    public void finishScheduleTask() {
        nodeConfig.getExecutorServiceWorkers().put(SCHEDULE_TASK_KEY, 10);
        RedissonNode node = RedissonNode.create(nodeConfig);
        node.start();
    }

    @GetMapping("/cancel-schedule-task")
    public Boolean cancelScheduleTask() {
        RScheduledExecutorService executorService = client.getExecutorService(SCHEDULE_TASK_KEY);
        String taskId = taskIdMap.get(SCHEDULE_TASK_KEY);
        logger.info("Task ID : " + taskId);
        return executorService.cancelTask(taskId);
    }
}
