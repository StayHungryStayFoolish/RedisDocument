package io.stayhungrystayfoolish.redisson.executor;

import org.redisson.RedissonNode;
import org.redisson.config.Config;
import org.redisson.config.RedissonNodeConfig;

/**
 * @Author: bonismo@hotmail.com
 * @Description:
 * @Date: 2020/7/9 2:49 上午
 * @Version: V1.0
 */
public class Worker {
    public static void main(String[] args) {
        Config config = new Config();
        config.useSingleServer().setAddress("redis://127.0.0.1:6379");
        RedissonNodeConfig nodeConfig = new RedissonNodeConfig(config);
        nodeConfig.getExecutorServiceWorkers().put("JobA", 1);
        RedissonNode node = RedissonNode.create(nodeConfig);
        node.start();
    }

}
