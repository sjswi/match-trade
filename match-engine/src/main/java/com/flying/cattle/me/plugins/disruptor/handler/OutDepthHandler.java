/**
 * @filename: OutDepthHandler.java 2019年12月19日
 * @project match-engine  V1.0
 * Copyright(c) 2020 flying-cattle Co. Ltd. 
 * All right reserved. 
 */
package com.flying.cattle.me.plugins.disruptor.handler;

import com.flying.cattle.me.match.MatchDetailHandler;
import com.flying.cattle.me.util.SpringContextUtils;
import com.flying.cattle.mt.entity.MatchOrder;
import com.lmax.disruptor.EventHandler;

/**
 * @ClassName: OutDepthHandler
 * @Description: TODO(已挂单深度处理消费者)
 * @author flying-cattle
 * @date 2019年12月19日
 */
public class OutDepthHandler implements EventHandler<MatchOrder> {

	// EventHandler的方法 - 都消费
	public void onEvent(MatchOrder order, long sequence, boolean endOfBatch) throws Exception {
		//当订单是1或2时，说明吃过单，造成了出单深度变化
		if (this.isEatOrder(order)) {
			MatchDetailHandler matchDetailHandler = (MatchDetailHandler) SpringContextUtils.getBean("matchDetailHandler");
			matchDetailHandler.outMatchDepth(order);
		}
	}
	
	//判断是否吃过单
	private Boolean isEatOrder(MatchOrder order) {
		if (order.getIsMarket()) {
			return order.getList().size()>0?true:false;
		}else {
			if (order.getState().intValue()==1||order.getState().intValue()==2) {
				return true;
			}else {
				return false;
			}
		}
	}
}
