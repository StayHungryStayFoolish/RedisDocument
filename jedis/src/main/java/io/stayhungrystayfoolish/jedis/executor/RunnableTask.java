package io.stayhungrystayfoolish.jedis.executor;

import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;
import org.redisson.api.annotation.RInject;

import java.io.Serializable;
import java.time.Instant;

public class RunnableTask implements Runnable, Serializable {

    @RInject
    RedissonClient redisson;

    @Override
    public void run() {
        final RMap<String, String> map = redisson.getMap("taskMap");
        Instant instant = Instant.now();
        map.put("Jedis Client Runnable : " + instant.toString(), instant.toString());
        System.out.println("Jedis Client Runnable Task A started!!!!!!!!!!");
        try {
            Thread.sleep(100);
            for (String value : map.values()) {
                if (Thread.currentThread().isInterrupted()) {
                    System.out.println("Value : " + value);
                    System.out.println("Task Cancel ...");
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Jedis Client Runnable Task A finished!!!!!!!!!!");
    }
}
