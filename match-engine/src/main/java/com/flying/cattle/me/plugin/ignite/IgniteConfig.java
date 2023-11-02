//package com.flying.cattle.me.plugin.ignite;
//
//import java.util.Arrays;
//import java.util.TreeMap;
//import java.util.concurrent.ConcurrentHashMap;
//
//import org.apache.ignite.Ignite;
//import org.apache.ignite.Ignition;
//import org.apache.ignite.configuration.IgniteConfiguration;
//import org.apache.ignite.spi.discovery.tcp.TcpDiscoverySpi;
//import org.apache.ignite.spi.discovery.tcp.ipfinder.vm.TcpDiscoveryVmIpFinder;
//import org.apache.ignite.springdata.repository.config.EnableIgniteRepositories;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.core.env.Environment;
//
//@Configuration
//@EnableIgniteRepositories
//public class IgniteConfig {
//
//	@Autowired
//	private Environment env;
//
//	private final static String ENV = "test";
//
//	@Bean("ignite")
//	public Ignite igniteInstance() {
//		IgniteConfiguration cfg = new IgniteConfiguration();
//		cfg.setIgniteInstanceName("match_engine_cache");
//
//		TcpDiscoverySpi spi = new TcpDiscoverySpi();
//		TcpDiscoveryVmIpFinder ipFinder = new TcpDiscoveryVmIpFinder();
//		ipFinder.setAddresses(Arrays.asList("10.10.150.26:47500"));
//		spi.setIpFinder(ipFinder);
//		cfg.setDiscoverySpi(spi);
//
////		cfg.setPeerClassLoadingEnabled(true); // 启用类对等加载
////		if (env.getActiveProfiles().length != 0 && env.getActiveProfiles()[0].equals(ENV)){
////			// init IP 地址
////			TcpDiscoverySpi spi = new TcpDiscoverySpi();
////			TcpDiscoveryVmIpFinder ipFinder = new TcpDiscoveryVmIpFinder();
////			ipFinder.setAddresses(Arrays.asList("172.18.0.1", "172.17.20.249"));
////			spi.setIpFinder(ipFinder);
////			// 设置为客户端
////			cfg.setClientMode(true);
////		}
//
//		return Ignition.start(cfg);
//	}
//
//	// 记录以及出现的币对
//	@Bean
//	public ConcurrentHashMap<String, String> igniteCacheKeys() {
//		return new ConcurrentHashMap<String, String>();
//	}
//
//	@Bean
//	public ConcurrentHashMap<String, TreeMap<Long, TreeMap<Long, Long>>> hotspot() {
//		return new ConcurrentHashMap<String, TreeMap<Long,TreeMap<Long,Long>>>();
//	}
//}
//
