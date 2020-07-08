package io.stayhungrystayfoolish.redisson.domain;

import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;
import org.redisson.api.annotation.RInject;

import java.io.Serializable;
import java.util.UUID;
import java.util.concurrent.Callable;

/**
 * @Author: bonismo@hotmail.com
 * @Description:
 * @Date: 2020/7/8 11:54 下午
 * @Version: V1.0
 */
public class CallableTask implements Callable<String>, Serializable {

    @RInject
    RedissonClient redissonClient;

    @Override
    public String call() throws Exception {
        String key = UUID.randomUUID().toString();
        final RMap<String, String> map = redissonClient.getMap("myMap");
        map.put(key, key);
        return "3";
    }
}
