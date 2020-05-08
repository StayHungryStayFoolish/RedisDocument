package io.stayhungrystayfoolish.redis.stream.single;

import io.lettuce.core.Consumer;
import io.lettuce.core.RedisBusyException;
import io.lettuce.core.StreamMessage;
import io.lettuce.core.XReadArgs;
import io.lettuce.core.api.sync.RedisCommands;
import io.lettuce.core.codec.StringCodec;
import io.lettuce.core.output.StatusOutput;
import io.lettuce.core.protocol.CommandArgs;
import io.lettuce.core.protocol.CommandKeyword;
import io.lettuce.core.protocol.CommandType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @Author: Created by bonismo@hotmail.com on 2020/5/6 10:03 上午
 * @Description: 1. use @ConditionalOnBean(name = "redisCommands")
 *                      when spring.redis.type = single ,this api can access (type = cluster can not access)
 *               2. use @Autowired(required = false) when spring.redis.type = single / cluster
 *                      this api can access ,but only type is single can works (type = cluster can access but not work)
 * @Version: 1.0
 */
@RestController
@RequestMapping("/api")
//@ConditionalOnBean(name = "redisCommands")
public class RedisStreamConsumer {

    private final Logger logger = LoggerFactory.getLogger(RedisStreamConsumer.class);

    private final static String STREAMS_KEY = "STREAMS_SINGLE:test";
    private final static String CONSUMER_NAME = "consumer_1";

    private final RedisCommands<String, String> syncCommands;

    public RedisStreamConsumer(@Autowired(required = false) RedisCommands<String, String> syncCommands) {
        this.syncCommands = syncCommands;
    }

    @GetMapping("/single/streams/consumer/{groupName}")
    public String singleStreamsConsumerMessage(@PathVariable String groupName) {
        logger.info("Single Redis Consumer Message .");
        StringBuilder result = new StringBuilder();
        String status = null;
        // 1. Create Group
        // Redis officially allows create an empty stream using the "MKSTREAM" command
        // to avoid the exception
        // "ERR The XGROUP subcommand requires the key to exist.
        // Note that for CREATE you may want to use the MKSTREAM option to create an empty stream automatically."
        StringCodec codec = StringCodec.UTF8;

        // specify $, only new message arriving in the stream from now on will be provided to consumers in the group.
        // specify 0, instead the consumer group will consume all the messages in the stream history to start with.
        CommandArgs<String, String> args = new CommandArgs<>(codec)
                .add(CommandKeyword.CREATE)
                .add(STREAMS_KEY)
                .add(groupName)
                .add("$")
                .add("MKSTREAM");
        logger.info("Commands : {}", args.toCommandString());
        try {
            status = syncCommands.dispatch(CommandType.XGROUP, new StatusOutput<>(codec), args);
        } catch (RedisBusyException e) {
            logger.error(String.format("Group '%s' already exists .", groupName));
        }

        logger.info("Waiting for new messages : {}", status);

        // 2. Read Stream Message
        while (true) {
            List<StreamMessage<String, String>> messages = syncCommands.xreadgroup(
                    Consumer.from(groupName, CONSUMER_NAME),
                    XReadArgs.StreamOffset.lastConsumed(STREAMS_KEY));

            if (!messages.isEmpty()) {
                for (StreamMessage<String, String> message : messages) {
                    logger.info("Consumer Message : {}", message);
                    logger.info("Consumer Message Id : {}", message.getId());
                    result.append(message);
                    syncCommands.xack(STREAMS_KEY, groupName, message.getId());
                }
            }
            return result.toString();
        }

    }
}
