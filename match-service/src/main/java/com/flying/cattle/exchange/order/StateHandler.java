/**
 * @filename: OrderState.java 2020年1月11日
 * @project match-service  V1.0
 * Copyright(c) 2020 flying-cattle Co. Ltd. 
 * All right reserved. 
 */
package com.flying.cattle.exchange.order;

import com.flying.cattle.mt.entity.Order;

/**
 * @ClassName: OrderState
 * @Description: TODO(状态设计模式)
 * @author flying-cattle
 * @date 2020年1月11日
 */
public interface StateHandler {
	void handler(Order order);
}
