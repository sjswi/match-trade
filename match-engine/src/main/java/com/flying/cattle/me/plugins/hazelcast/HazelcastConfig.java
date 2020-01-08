/**
 * @filename: HazelcastConfig.java 2019年12月19日
 * @project match-engine  V1.0
 * Copyright(c) 2020 flying-cattle Co. Ltd. 
 * All right reserved. 
 */
package com.flying.cattle.me.plugins.hazelcast;

import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

import com.flying.cattle.me.plugins.hazelcast.setting.HazelcastSetting;
import com.flying.cattle.me.plugins.hazelcast.setting.TcpIpSetting;
import com.hazelcast.config.Config;
import com.hazelcast.config.JoinConfig;
import com.hazelcast.config.NetworkConfig;
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
@DependsOn("hazelcastSetting")
public class HazelcastConfig {

	private String app_name = "match";

	// @Value("${spring.profiles.active}")
	private String app_active = "local";

	@Autowired
	HazelcastSetting hazelcastSetting;

	@Bean
	public Config config() {
		String instanceKey = app_name + app_active + "-instance";
		Config config = new Config();
		config.setInstanceName(instanceKey);
		networkSetting(config);
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

	/**
	 * 网络配置
	 * @param config
	 */
	private void networkSetting(Config config){
		//未开启自定义网络配置
		if(Objects.isNull(hazelcastSetting.getSelfSetting())||!hazelcastSetting.getSelfSetting()){
			return;
		}
		NetworkConfig network = config.getNetworkConfig();
		JoinConfig join = network.getJoin();
		//主动发现配置
		if(Objects.nonNull(hazelcastSetting.getTcpIp())&&hazelcastSetting.getTcpIp().getEnable()){
			TcpIpSetting tcpIpSetting= hazelcastSetting.getTcpIp();
			join.getMulticastConfig().setEnabled(Boolean.FALSE);
			tcpIpSetting.getMembers().forEach(item->
					join.getTcpIpConfig().addMember(item).setRequiredMember(item).setEnabled(Boolean.TRUE)
			);
			network.getInterfaces().setEnabled(Boolean.TRUE).addInterface(hazelcastSetting.getTcpIp().getInterfaceEx());
			config.setNetworkConfig(network);
			return;
		}
		//K8s网络发现
		if(Objects.nonNull(hazelcastSetting.getKubernetes())&&hazelcastSetting.getKubernetes().getEnable()){

			join.getMulticastConfig().setEnabled(Boolean.FALSE);
			join.getKubernetesConfig().setEnabled(Boolean.TRUE)
					.setProperty("service-dns",hazelcastSetting.getKubernetes().getServiceDns());
			config.setNetworkConfig(network);
			return;
		}
	}
}
