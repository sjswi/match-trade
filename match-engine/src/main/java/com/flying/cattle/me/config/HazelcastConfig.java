/**
 * @filename: HazelcastConfig.java 2019年12月19日
 * @project match-engine  V1.0
 * Copyright(c) 2020 flying-cattle Co. Ltd. 
 * All right reserved. 
 */
package com.flying.cattle.me.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.hazelcast.config.Config;
import com.hazelcast.config.InMemoryFormat;
import com.hazelcast.config.MapConfig;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.jet.Jet;
import com.hazelcast.jet.JetInstance;
import com.hazelcast.jet.config.JetConfig;

/**
 * @ClassName: HazelcastConfig
 * @Description: TODO(Hazelcast-jet配置)
 * @author flying-cattle
 * @date 2019年12月19日
 */
@Configuration
public class HazelcastConfig {

	private String app_name = "match";

	// @Value("${spring.profiles.active}")
	private String app_active = "local";

	@Bean
	public Config config() {
		String instanceKey = app_name + app_active + "-instance";
		Config config = new Config();
		config.setInstanceName(instanceKey);
		return config;
	}

	@Bean
	public JetInstance jetInstance(Config config) {
		JetConfig jConfig = new JetConfig();
		jConfig.setHazelcastConfig(config);
		JetInstance jetInstance = Jet.newJetInstance(jConfig);
		return jetInstance;
	}

	@Bean
	public HazelcastInstance hzInstance(JetInstance jetInstance) {
		HazelcastInstance hzInstance = jetInstance.getHazelcastInstance();
		return hzInstance;
	}
}
