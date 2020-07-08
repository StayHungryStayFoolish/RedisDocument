package io.stayhungrystayfoolish.redisson.domain;

import org.redisson.api.RAtomicLong;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;
import org.redisson.api.annotation.RInject;

import java.io.Serializable;
import java.util.UUID;

/**
 * @Author: bonismo@hotmail.com
 * @Description:
 * @Date: 2020/7/8 11:38 下午
 * @Version: V1.0
 */
public class RunnableTask implements Runnable, Serializable {

    @RInject
    private RedissonClient redissonClient;

    private long param;

    public RunnableTask() {
    }

    public RunnableTask(long param) {
        this.param = param;
    }

    @Override
    public void run() {
        String key = UUID.randomUUID().toString();
        final RMap<String, String> map = redissonClient.getMap("myMap");
        map.put(key, key);
        RAtomicLong atomic = redissonClient.getAtomicLong("myAtomic");
        long result = atomic.addAndGet(param);
        System.out.println("====>" + result);
    }

}
