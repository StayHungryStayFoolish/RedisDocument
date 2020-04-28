package io.stayhungrystayfoolish.redis.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @Author: Created by bonismo@hotmail.com on 2020/4/28 2:41 下午
 * @Description:
 * @Version: 1.0
 */
@Component
@ConfigurationProperties(prefix = "spring.redis.cluster")
public class RedisClusterProperties {

    /**
     * spring.redis.cluster.node[0] = 127.0.0.1:6379
     * spring.redis.cluster.node[1] = 127.0.0.1:6380
     */
    private List<String> nodes;

    public List<String> getNodes() {
        return nodes;
    }

    public void setNodes(List<String> nodes) {
        this.nodes = nodes;
    }
}
