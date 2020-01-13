/**
 * @filename: OrderFactory.java 2020年1月13日
 * @project match-service  V1.0
 * Copyright(c) 2020 flying-cattle Co. Ltd. 
 * All right reserved. 
 */
package com.flying.cattle.exchange.order;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.util.Assert;
import com.flying.cattle.mt.enums.OrderState;
/**
 * @ClassName: OrderFactory
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author flying-cattle
 * @date 2020年1月13日
 */
public class OrderFactory {
	private static Map<OrderState, StateHandler> services = new ConcurrentHashMap<OrderState, StateHandler>();

	public static StateHandler getByOrderState(OrderState orderState) {
		return services.get(orderState);
	}

	public static void register(OrderState OrderState, StateHandler stateHandler) {
		Assert.notNull(OrderState, "userType can't be null");
		services.put(OrderState, stateHandler);
	}
}
