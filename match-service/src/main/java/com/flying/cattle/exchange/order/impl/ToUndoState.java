/**
 * @filename: ToUndoState.java 2020年1月13日
 * @project match-service  V1.0
 * Copyright(c) 2020 flying-cattle Co. Ltd. 
 * All right reserved. 
 */
package com.flying.cattle.exchange.order.impl;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.flying.cattle.exchange.order.OrderFactory;
import com.flying.cattle.exchange.order.StateHandler;
import com.flying.cattle.exchange.plugins.mq.SendService;
import com.flying.cattle.mt.entity.CancelOrderParam;
import com.flying.cattle.mt.entity.Order;
import com.flying.cattle.mt.enums.OrderState;

/**
 * @ClassName: ToUndoState
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author flying-cattle
 * @date 2020年1月13日
 */
@Service
public class ToUndoState implements StateHandler , InitializingBean {

	@Autowired
	private SendService sendService;
	
	@Override
	public void handler(Order order) {
		// 实现-数据库查询，看是否可以撤销
		boolean toUndo=true;
		if (toUndo) {
			CancelOrderParam param = new CancelOrderParam(order.getId(), order.getIsBuy(), order.getCoinTeam()); 
			sendService.sendCancelOrder(param.toJsonString());
		}
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		OrderFactory.register(OrderState.TO_UNDO, this);
	}

}
