/**
 * @filename: PushData.java 2019年12月19日
 * @project match-engine  V1.0
 * Copyright(c) 2020 flying-cattle Co. Ltd. 
 * All right reserved. 
 */
package com.flying.cattle.me.data.out;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.flying.cattle.me.plugins.mq.SendService;
import com.flying.cattle.mt.entity.MatchOrder;
import com.flying.cattle.mt.entity.Order;
import com.flying.cattle.mt.entity.Trade;

import lombok.extern.slf4j.Slf4j;

/**
 * @ClassName: PushData
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author flying-cattle
 * @date 2019年12月19日
 */
@Component
@Slf4j
public class PushData {
	
	@Autowired
	SendService sendService;
	
	/**
	 * @Title: PushOrder
	 * @Description: TODO(推送订单变化)
	 * @param  order
	 * @return void 返回类型
	 * @throws
	 */
	public void updateOrder(MatchOrder order) {
		try {
			Order or = new Order();
			BeanUtils.copyProperties(order, or);
			sendService.sendOrderAlter(or.toJsonString());
		} catch (Exception e) {
			e.printStackTrace();
			log.error("===推送订单失败，数据原型为："+order.toJsonString());
		}
	}
	
	/**
	 * @Title: PushOrder
	 * @Description: TODO(推送订单变化)
	 * @param  trade
	 * @return void 返回类型
	 * @throws
	 */
	public void addTrade(Trade trade) {
		try {
			sendService.sendNewTrade(trade.toJsonString());
		} catch (Exception e) {
			e.printStackTrace();
			log.error("===推送交易失败，数据原型为："+trade.toJsonString());
		}
	}
}
