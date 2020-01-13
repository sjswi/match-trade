/**
 * @filename: PaidState.java 2020年1月13日
 * @project match-service  V1.0
 * Copyright(c) 2020 flying-cattle Co. Ltd. 
 * All right reserved. 
 */
package com.flying.cattle.exchange.order.impl;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.flying.cattle.exchange.order.OrderFactory;
import com.flying.cattle.exchange.order.StateHandler;
import com.flying.cattle.exchange.plugins.mq.SendService;
import com.flying.cattle.mt.entity.Order;
import com.flying.cattle.mt.enums.OrderState;

/**
 * @ClassName: PaidState
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author flying-cattle
 * @date 2020年1月13日
 */
@Component
public class PaidState implements StateHandler , InitializingBean{

	@Autowired
	private SendService sendService;
	
	@Override
	public void handler(Order order) {
		//1：实现-资金操作
		Boolean isPaid = true;
		if (isPaid) {
			//21：成功发到撮合
			order.setState(OrderState.PAID.value);
			sendService.sendNewOrder(order.toJsonString());
		}else {
			//22：失败发到撮合
			order.setState(OrderState.UNPAID.value);
		}
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		// TODO Auto-generated method stub
		OrderFactory.register(OrderState.PAID, this);
	}

}
