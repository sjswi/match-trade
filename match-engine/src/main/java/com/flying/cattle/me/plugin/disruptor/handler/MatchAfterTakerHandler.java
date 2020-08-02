///**
// * @project walk-exchange-match  V1.0
// * @filename: MatchAfterHandler.java 2020年7月9日
// * Copyright(c) 2020 kinbug Co. Ltd.
// * All right reserved.
// */
//package com.flying.cattle.me.plugin.disruptor.handler;
//
//import com.lmax.disruptor.EventHandler;
//import com.flying.cattle.me.common.enums.EnumOrderType;
//import com.flying.cattle.me.match.domain.MatchOrder;
//import com.flying.cattle.me.match.factory.MatchStrategyFactory;
//import com.flying.cattle.me.match.service.OrderMatchServiceAbstract;
//
///**
// * @ClassName: MatchAfterHandler
// * @Description: TODO(撮合后taker处理)
// * @author kinbug
// * @date 2020年7月9日
// */
//public class MatchAfterTakerHandler implements EventHandler<MatchOrder> {
//
//	@Override
//	public void onEvent(MatchOrder event, long sequence, boolean endOfBatch) throws Exception {
//		OrderMatchServiceAbstract service = MatchStrategyFactory.getByOrderType(EnumOrderType.of(event.getOrderType()));
//		service.afterTakerMatch(event);
//	}
//
//}
