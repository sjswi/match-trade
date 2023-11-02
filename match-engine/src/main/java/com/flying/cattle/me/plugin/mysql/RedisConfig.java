package com.flying.cattle.me.plugin.mysql;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * @program: match-trade
 * @description:
 * @author: yuxiaobing
 * @mail：a17281293@gmail.com
 * @date: 2023-10-30 20:23
 **/
@Configuration
public class RedisConfig {

    @Autowired
    private RedisConnectionFactory redisConnectionFactory;

    @Bean
    public RedisTemplate<String, Object> redisTemplate() {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory);
        return template;
    }
}


