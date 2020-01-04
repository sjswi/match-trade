/**
 * @filename: PushDetphJob.java 2019-12-13
 * @project power-web  V1.0
 * Copyright(c) 2018 BianPeng Co. Ltd. 
 * All right reserved. 
 */
package com.flying.cattle.me.data.out;

import java.math.BigDecimal;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import com.alibaba.fastjson.JSON;
import com.flying.cattle.me.entity.ClusterInfo;
import com.flying.cattle.me.entity.Depth;
import com.flying.cattle.me.util.HazelcastUtil;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;

import lombok.extern.slf4j.Slf4j;

/**
 * Copyright: Copyright (c) 2019
 * -说明：深度推送
 * @version: V1.0
 * @author: BianPeng
 * 
 */
@Component
@EnableScheduling
@Slf4j
public class PushDepthJob {

	@Autowired
	public HazelcastInstance hzInstance;

	@Autowired
	private KafkaTemplate<String, String> template;

	@Value("#{'${match.engine.coinTeams}'.split(',')}")
	private List<String> coinTeams;

	/**
	 * @Title: PushOrder
	 * @Description: TODO(推送深度，因为kafka用了Group的原因，准备改为redis来做广播，让所有match-service都收到消息。PS:如果kafka有解决方案的指教下，万分感谢)
	 * @param  order
	 * @return void 返回类型
	 * @throws
	 */
	@Scheduled(fixedDelay = 1000)
	public void pushDepth() {
		try {
			if (!isMasterNode()) {
				return;
			}
			if (!coinTeams.isEmpty()) {
				for (String coinTeam : coinTeams) {
					//  买盘
					List<Depth> buyList = this.getMarketDepth(coinTeam, Boolean.TRUE);
					//  卖盘
					List<Depth> sellList = this.getMarketDepth(coinTeam, Boolean.FALSE);
					// 发送数据处理
					Map<String, List<Depth>> map = new HashMap<String, List<Depth>>();
					map.put("buy", buyList);
					map.put("sell", sellList);
					// 推送深度
					template.send("push_depth", JSON.toJSONString(map));
				}
			}
		} catch (Exception e) {
			log.error("深度数据处理错误：" + e);
			e.printStackTrace();
		}
	}

	/**
	 * -★ -获取行情深度
	 * 
	 * @param coinTeam 交易队
	 * @param isBuy    是否是买
	 * @return List<Depth>
	 */
	public List<Depth> getMarketDepth(String coinTeam, Boolean isBuy) {
		// XBIT-USDT 买盘
		IMap<BigDecimal, BigDecimal> buyMap = hzInstance.getMap(HazelcastUtil.getMatchKey(coinTeam, isBuy));
		List<Depth> depths = new ArrayList<Depth>();
		if (buyMap.size() > 0) {
			List<Depth> list = new ArrayList<Depth>();
			if (isBuy) {
				list = buyMap.entrySet().stream().sorted(Entry.<BigDecimal, BigDecimal>comparingByKey().reversed())
						.map(obj -> new Depth(obj.getKey().toString(), obj.getValue().toString(),
								obj.getValue().toString(), 1, coinTeam, isBuy)).limit(100)
						.collect(Collectors.toList());
			} else {
				list = buyMap.entrySet().stream().sorted(Entry.<BigDecimal, BigDecimal>comparingByKey())
						.map(obj -> new Depth(obj.getKey().toString(), obj.getValue().toString(),
								obj.getValue().toString(), 1, coinTeam, isBuy)).limit(100)
						.collect(Collectors.toList());
			}
			list.stream().reduce(new Depth("0", "0", "0", 1, coinTeam, isBuy), (one, two) -> {
				one.setTotal((new BigDecimal(one.getTotal()).add(new BigDecimal(two.getNumber()))).toString());
				depths.add(new Depth(two.getPrice(), two.getNumber(), one.getTotal(), two.getPlatform(),
						two.getCoinTeam(), two.getIsBuy()));
				return one;
			});
		} else {
			Depth depth = new Depth("0.00", "0.0000", "0.0000", 1, coinTeam, isBuy);
			depths.add(depth);
		}
		return depths;
	}
	
	/**
	 * @Title: isMasterNode
	 * @Description: TODO(主节点判断)
	 * @param  参数
	 * @return void 返回类型
	 * @throws
	 */
	private Boolean isMasterNode() {
		try {
			IMap<String, ClusterInfo> map = hzInstance.getMap("Cluster-IP");
			long current = System.currentTimeMillis();
			String ip = InetAddress.getLocalHost().getHostAddress();//获得本机IP  
			if (!map.containsKey(ip)) {
				map.put(ip, new ClusterInfo(ip, current, current));
			}
			Comparator<ClusterInfo> comparing = Comparator.comparing(ClusterInfo::getStartTime);
			ClusterInfo ci =map.values().stream().min(comparing).get();
			if (ci.getIp().equals(ip)) {
				ci.setUpdateTime(System.currentTimeMillis());
				map.put(ip, ci);
				return Boolean.TRUE;
			}else {
				long interval = current - ci.getUpdateTime();
				if (ci.getStartTime()!=ci.getUpdateTime()&&interval>3000) {
					map.delete(ci.getIp());
				}
			}
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		return Boolean.FALSE;
	}
}
