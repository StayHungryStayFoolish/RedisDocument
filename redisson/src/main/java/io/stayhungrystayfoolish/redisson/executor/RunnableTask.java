package io.stayhungrystayfoolish.redisson.executor;

import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;
import org.redisson.api.annotation.RInject;

import java.io.Serializable;
import java.time.Instant;
import java.util.concurrent.atomic.AtomicInteger;

public class RunnableTask implements Runnable, Serializable {

    @RInject
    RedissonClient redisson;

    @Override
    public void run() {
        final RMap<String, String> map = redisson.getMap("myMap");
        AtomicInteger key = new AtomicInteger();
        AtomicInteger value = new AtomicInteger();
        Instant instant = Instant.now();
        map.put("Runnable : " + String.valueOf(key.incrementAndGet()), instant.toString());
        System.out.println("=================>" + instant.toString());
        System.out.println("JOB A started!!!!!!!!!!");
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("JOB A finished!!!!!!!!!!");
    }

}
