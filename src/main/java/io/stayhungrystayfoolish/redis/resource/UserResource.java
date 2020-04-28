package io.stayhungrystayfoolish.redis.resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author: Created by bonismo@hotmail.com on 2020/4/28 1:27 下午
 * @Description:
 * @Version: 1.0
 */
@RestController
@RequestMapping("/api")
public class UserResource {


    private final Logger logger = LoggerFactory.getLogger(UserResource.class);

    @GetMapping("/user")
    public String user() {
        logger.info("REST Request user .");
        return "user";
    }
}
