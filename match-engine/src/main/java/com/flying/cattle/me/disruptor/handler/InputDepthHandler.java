/**
 * @filename: InputDepthHandler.java 2019年12月19日
 * @project match-engine  V1.0
 * Copyright(c) 2020 flying-cattle Co. Ltd. 
 * All right reserved. 
 */
package com.flying.cattle.me.disruptor.handler;

import com.flying.cattle.me.entity.MatchOrder;
import com.flying.cattle.me.match.MatchDetailHandler;
import com.flying.cattle.me.util.SpringContextUtils;
import com.lmax.disruptor.EventHandler;

import lombok.extern.slf4j.Slf4j;

/**
 * @ClassName: InputDepthHandler
 * @Description: TODO(新订单深度处理消费者)
 * @author flying-cattle
 * @date 2019年12月19日
 */
@Slf4j
public class InputDepthHandler implements EventHandler<MatchOrder> {

	// EventHandler的方法 - 都消费
	public void onEvent(MatchOrder order, long sequence, boolean endOfBatch) throws Exception {
		try {
			MatchDetailHandler matchDetailHandler = (MatchDetailHandler) SpringContextUtils.getBean("matchDetailHandler");
			if (order.getState().intValue()!=0) {
				matchDetailHandler.sendOrderChange(order);
			}
			//当状态大于1时，不造成深度变化
			if (order.getState().intValue()==0||order.getState().intValue()==1) {
				matchDetailHandler.inputMatchDepth(order);
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error("===入单消费错误："+e);
		}
	}
}
