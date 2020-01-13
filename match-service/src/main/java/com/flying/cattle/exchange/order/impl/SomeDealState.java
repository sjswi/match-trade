/**
 * @filename: SomeDeal.java 2020年1月13日
 * @project match-service  V1.0
 * Copyright(c) 2020 flying-cattle Co. Ltd. 
 * All right reserved. 
 */
package com.flying.cattle.exchange.order.impl;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import com.flying.cattle.exchange.order.OrderFactory;
import com.flying.cattle.exchange.order.StateHandler;
import com.flying.cattle.mt.entity.Order;
import com.flying.cattle.mt.enums.OrderState;

/**
 * @ClassName: SomeDeal
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author flying-cattle
 * @date 2020年1月13日
 */
@Component
public class SomeDealState implements StateHandler , InitializingBean{

	@Override
	public void handler(Order order) {
		// 实现-部分撮合的数据变化，只入库
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		// TODO Auto-generated method stub
		OrderFactory.register(OrderState.SOME_DEAL, this);
	}

}
