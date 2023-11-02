package com.flying.cattle.me.plugin.mysql;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Bean;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @program: match-trade
 * @description: dapr engine conifg
 * @author: yuxiaobing
 * @mail：a17281293@gmail.com
 * @date: 2023-10-29 16:33
 **/

@Configuration
public class MySQLConfig {

    @Bean
	public ConcurrentHashMap<String, String> igniteCacheKeys() {
		return new ConcurrentHashMap<String, String>();
	}
}
