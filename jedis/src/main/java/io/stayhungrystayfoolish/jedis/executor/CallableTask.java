package io.stayhungrystayfoolish.jedis.executor;

import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;
import org.redisson.api.annotation.RInject;

import java.io.Serializable;
import java.time.Instant;
import java.util.concurrent.Callable;

public class CallableTask implements Callable<String>, Serializable {

    @RInject
    RedissonClient redisson;

    @Override
    public String call() {
        final RMap<String, String> map = redisson.getMap("taskMap");
        Instant instant = Instant.now();
        map.put("Jedis Client Callable : " + instant.toString(), instant.toString());
        System.out.println("Jedis Client Callable Task A started !");
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Jedis Client Callable Task A finished !");
        return "Jedis Client Callable Task A finished !";
    }
}
