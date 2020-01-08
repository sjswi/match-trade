/**
 * @filename: TestController.java 2020年1月4日
 * @project match-engine  V1.0
 * Copyright(c) 2020 flying-cattle Co. Ltd. 
 * All right reserved. 
 */
package com.flying.cattle.me.data.test;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.flying.cattle.me.entity.MatchOrder;
import com.flying.cattle.me.plugins.disruptor.producer.OrderProducer;
import com.flying.cattle.me.util.SnowflakeIdWorker;
import com.lmax.disruptor.RingBuffer;

import lombok.extern.slf4j.Slf4j;

/**
 * @ClassName: TestController
 * @Description: TODO(测试类)
 * @author flying-cattle
 * @date 2020年1月4日
 */
@Profile("local")
@RequestMapping("/match/engine")
@RestController
@Slf4j
public class TestController {
	long start = 0;

	@Autowired
	RingBuffer<MatchOrder> ringBuffer;

	/**
	 * @Title: addNewOrder @Description: TODO(添加新的订单) @param 参数 @return void
	 * 返回类型 @throws
	 */
	@PostMapping("/addNewOrders/{size}")
	public String addNewOrders(@PathVariable("size") long size) {
		try {
			for (long i = 1; i <= size; i++) {
				Random random = new Random();
				int price = random.nextInt(100) + 1;
				OrderParam param = new OrderParam(Boolean.TRUE, new BigDecimal(price), BigDecimal.ONE, BigDecimal.ONE,"XBIT-USDT", Boolean.FALSE);
				MatchOrder order = paramToOrder(param);
				order.setId(SnowflakeIdWorker.generateId());
				order.setUid(i);
				if (order.getUid().longValue() == 1) {
					start = System.currentTimeMillis();
				}
				if (order.getUid().longValue() % 10000 == 0) {
					log.info("当前是第：" + order.getUid() + "条数据，耗时：" + (System.currentTimeMillis() - start) + "(毫秒)");
				}
				OrderProducer producer = new OrderProducer(ringBuffer);
				producer.onData(order);
			}
		} catch (Exception e) {
			log.error("添加新的订单错误：" + e);
			e.printStackTrace();
		}
		return "添加成功：" + System.currentTimeMillis();
	}

	/**
	 * @Title: paramToOrder @Description: TODO(添加等等参数转order) @param OrderParam
	 * entity @return Order @throws
	 */
	public static MatchOrder paramToOrder(OrderParam entity) {
		MatchOrder order = new MatchOrder();
		order.setIsBuy(entity.getIsBuy());
		order.setCoinTeam(entity.getCoinTeam());
		order.setNumber(entity.getNumber());
		order.setPrice(entity.getPrice());
		if (entity.getIsMarket()) {
			order.setTotalPrice(entity.getTotal());
		} else {
			order.setTotalPrice(order.getPrice().multiply(order.getNumber()));
		}
		order.setFinishNumber(BigDecimal.ZERO);
		order.setUnFinishNumber(entity.getNumber());
		order.setIsMarket(entity.getIsMarket());
		order.setState(0);
		order.setCreateTime(new Date());
		if (order.getIsBuy()) {
			order.setSurplusFrozen(order.getTotalPrice());// 剩余冻结
		} else {
			order.setSurplusFrozen(order.getUnFinishNumber());// 剩余冻结
		}
		return order;
	}
}
