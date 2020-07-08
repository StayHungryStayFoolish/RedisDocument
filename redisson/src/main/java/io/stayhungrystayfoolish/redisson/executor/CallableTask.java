package io.stayhungrystayfoolish.redisson.executor;

import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;
import org.redisson.api.annotation.RInject;

import java.io.Serializable;
import java.time.Instant;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicInteger;

public class CallableTask implements Callable<String>, Serializable {

	@RInject
	RedissonClient redisson;

	@Override
	public String call() throws Exception {
		final RMap<String, String> map = redisson.getMap("myMap");
		AtomicInteger key = new AtomicInteger();
		AtomicInteger value = new AtomicInteger();
		map.put("Callable : " + String.valueOf(key.incrementAndGet()), Instant.now().toString());
		//return map.get("3");
		return "3";
	}

}
