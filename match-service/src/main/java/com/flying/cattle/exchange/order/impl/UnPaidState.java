/**
 * @filename: PaidState.java 2020年1月13日
 * @project match-service  V1.0
 * Copyright(c) 2020 flying-cattle Co. Ltd. 
 * All right reserved. 
 */
package com.flying.cattle.exchange.order.impl;

import org.springframework.beans.factory.InitializingBean;

import com.flying.cattle.exchange.order.OrderFactory;
import com.flying.cattle.exchange.order.StateHandler;
import com.flying.cattle.mt.entity.Order;
import com.flying.cattle.mt.enums.OrderState;

/**
 * @ClassName: PaidState
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author flying-cattle
 * @date 2020年1月13日
 */
public class UnPaidState implements StateHandler, InitializingBean {

	@Override
	public void handler(Order order) {
		//实现-删除订单
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		// TODO Auto-generated method stub
		OrderFactory.register(OrderState.UNPAID, this);
	}

}
