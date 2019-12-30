/**
 * @filename: OrderFactory.java 2019年12月19日
 * @project match-engine  V1.0
 * Copyright(c) 2020 flying-cattle Co. Ltd. 
 * All right reserved. 
 */
package com.flying.cattle.me.disruptor.factory;

import com.flying.cattle.me.entity.MatchOrder;
import com.lmax.disruptor.EventFactory;

/**
 * @ClassName: OrderFactory
 * @Description: TODO(Order工厂)
 * @author flying-cattle
 * @date 2019年12月19日
 */
public class OrderFactory implements EventFactory<MatchOrder>{

	@Override
	public MatchOrder newInstance() {
		return new MatchOrder();
	}

}
