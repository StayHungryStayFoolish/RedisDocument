package io.stayhungrystayfoolish.redisson;

import org.junit.jupiter.api.Test;
import org.redisson.RedissonExecutorService;
import org.redisson.api.*;
import org.redisson.api.annotation.RInject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.io.Serializable;
import java.net.URI;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class RedissonApplicationTests {

    @Autowired
    RedissonClient client;

    @Autowired
    private MockMvc restTraceSourceMockMvc;

    @Test
    void redissonAdd() throws Exception {
        URI uri = new URI("/api/add");
        MvcResult result = restTraceSourceMockMvc.perform(get(uri)).andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print())
                .andReturn();
    }

    @Test
    void redissonLock() {
        AtomicInteger success = new AtomicInteger();
        AtomicInteger failed = new AtomicInteger();

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                boolean res = false;
                RLock lock = client.getLock("lock-test");
                try {
                    res = lock.tryLock(30, 10, TimeUnit.SECONDS);
                    if (res) {
                        System.out.println("获取锁成功 " + success.incrementAndGet() + " 次");
                    }
                } catch (Exception e) {
                    System.out.println("获取锁失败 " + failed.incrementAndGet() + " 次");
                    e.printStackTrace();
                } finally {
                    if (res) {
                        lock.unlock();
                    }
                }
            }
        };

        for (int i = 0; i < 10; i++) {
            runnable.run();
        }
    }
}
