/**
 * @project walk-exchange-match  V1.0
 * @filename: TradeModel.java 2020年7月7日
 * Copyright(c) 2020 kinbug Co. Ltd. 
 * All right reserved. 
 */
package com.flying.cattle.me.match.domain;

import java.util.Date;

import org.springframework.stereotype.Component;

import com.flying.cattle.me.data.out.SendService;
import com.flying.cattle.mt.enums.EnumTradeType;
import com.flying.cattle.mt.message.TradeDTO;

/**
 * @ClassName: TradeModel
 * @Description: TODO(Trade域聚合根)
 * @author kinbug
 * @date 2020年7月7日
 */
@Component
public class TradeModel {

	final SendService sendService;

	public TradeModel(SendService sendService) {
		this.sendService = sendService;
	}

	/**
	 * @Title: createAndSendTrade 
	 * @Description: TODO(Maker:创建并发送交易记录) 
	 * @param taker 委托吃单
	 * @param maker 委托挂单 
	 * @param num 成交数量
	 * @param price 成交价 
	 * @return void 返回类型 
	 * @throws
	 */
	public void sendMakerDealInfo(MatchOrder taker, MatchOrder maker, long num, long price) {
		TradeDTO trade = new TradeDTO(taker.getUid(), taker.getAccountId(), taker.getId(), maker.getUid(),
				maker.getAccountId(), maker.getId(), maker.isIfBid(), num, price, num * price, taker.getSymbolId(),
				new Date(), EnumTradeType.MAKER.getCode(), "");
		// 撮合产生的撮合记录
		sendService.sendNewTrade(trade);
		// 撮合导致订单变化
		sendService.sendUpdateOrder(maker);
	}

	/**
	 * @Title: createAndSendTrade 
	 * @Description: TODO(taker:创建并发送交易记录) 
	 * @param taker 委托吃单
	 * @param maker 委托挂单 
	 * @param num 成交数量
	 * @param price 成交价
	 * @param makerOids 成交列表 
	 * @return void 返回类型 
	 * @throws
	 */
	public void sendTakerDealInfo(MatchOrder taker, String makerOids) {
		TradeDTO trade = new TradeDTO(taker.getUid(), taker.getAccountId(), taker.getId(),  0L,
				 0L, 0L, taker.isIfBid(), taker.getDealNum(), 0L, taker.getDealAmount(), taker.getSymbolId(),
				new Date(), EnumTradeType.TAKER.getCode(), makerOids);
		// 撮合产生的撮合记录
		sendService.sendNewTrade(trade);
		// 撮合导致订单变化
		sendService.sendUpdateOrder(taker);
	}
}
