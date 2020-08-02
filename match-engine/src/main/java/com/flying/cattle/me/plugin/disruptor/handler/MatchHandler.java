//package com.flying.cattle.me.plugin.disruptor.handler;
//
//import com.lmax.disruptor.WorkHandler;
//import com.flying.cattle.me.common.enums.EnumOrderType;
//import com.flying.cattle.me.match.domain.MatchOrder;
//import com.flying.cattle.me.match.factory.MatchStrategyFactory;
//import com.flying.cattle.me.match.service.OrderMatchServiceAbstract;
//
///**
// * 撮合处理器
// * @author kinbug
// */
//public class MatchHandler implements WorkHandler<MatchOrder> {
//
//	/**
//	 * 撮合处理器，线性消费
//	 * @param event
//	 */
//	@Override
//	public void onEvent(MatchOrder event) {
//		OrderMatchServiceAbstract service = MatchStrategyFactory.getByOrderType(EnumOrderType.of(event.getOrderType()));
//		service.startMatch(event);
//	}
//
//}
