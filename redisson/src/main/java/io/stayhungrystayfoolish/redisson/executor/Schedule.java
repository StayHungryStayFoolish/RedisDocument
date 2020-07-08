package io.stayhungrystayfoolish.redisson.executor;

import org.redisson.Redisson;
import org.redisson.api.ExecutorOptions;
import org.redisson.api.RExecutorFuture;
import org.redisson.api.RScheduledExecutorService;
import org.redisson.api.RedissonClient;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * @Author: bonismo@hotmail.com
 * @Description:
 * @Date: 2020/7/9 2:50 上午
 * @Version: V1.0
 */
public class Schedule {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        RedissonClient client = Redisson.create();
        ExecutorOptions option = ExecutorOptions.defaults();
        option.taskRetryInterval(3000, TimeUnit.MILLISECONDS);
        RScheduledExecutorService executorService = client.getExecutorService("JobA",option);
        // submit JobA
        RExecutorFuture<?> future = executorService
                .submit(new RunnableTask());
        System.out.println("=================================================="+future.get());
    }
}
