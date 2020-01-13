/**
 * @filename: MatchOutResources.java 2019年12月20日
 * @project exchange  V1.0
 * Copyright(c) 2020 flying-cattle Co. Ltd. 
 * All right reserved. 
 */
package com.flying.cattle.exchange.data;

import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;

import com.alibaba.fastjson.JSON;
import com.flying.cattle.exchange.order.OrderFactory;
import com.flying.cattle.exchange.order.StateHandler;
import com.flying.cattle.exchange.plugins.mq.MatchSink;
import com.flying.cattle.mt.entity.Order;
import com.flying.cattle.mt.enums.OrderState;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

/**
 * @ClassName: MatchOutResources
 * @Description: 撮合输出数据源
 * @author flying-cattle
 * @date 2019年12月20日
 */
@EnableBinding(MatchSink.class)
@Slf4j
public class MatchOutResources {
	
	private String depthStr = "记录上次深度";
	
	/**
	 * @Title: push_depth
	 * @Description: TODO(盘口深度数据)
	 * @param  echo
	 * @return void 返回类型
	 * @throws
	 */
	@StreamListener(MatchSink.IN_PUSH_DEPTH)
	public void push_depth(Flux<String> flux) {
		flux.subscribe(message -> {
			// PushDepth pd = JSON.parseObject(message, PushDepth.class);
			if (!depthStr.equals(message)) {
				depthStr = message;
				log.info("当前深度：" + message);
			}
		});
	}

	/**
	 * @Title: push_depth
	 * @Description: TODO(订单变化)
	 * @param  echo
	 * @return void 返回类型
	 * @throws
	 */
	@StreamListener(MatchSink.IN_ORDER_ALTER)
	public void update_order(Flux<String> flux) {
		flux.subscribe(message -> {
			Order order = JSON.parseObject(message, Order.class);
			StateHandler stateHandler = OrderFactory.getByOrderState(OrderState.of(order.getState()).get());
			stateHandler.handler(order);
		});
	}
	

	/**
	 * @Title: push_depth
	 * @Description: TODO(新的交易记录)
	 * @return void 返回类型
	 * @throws
	 */
	@StreamListener(MatchSink.IN_NEW_TRADE)
	public void new_trade(Flux<String> flux) {
		flux.subscribe(message -> {
			log.info("交易记录：" + message);
		});
	}
}
