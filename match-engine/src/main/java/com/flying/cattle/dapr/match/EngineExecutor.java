package com.flying.cattle.dapr.match;

import com.flying.cattle.dapr.match.domain.MatchOrder;
import com.flying.cattle.dapr.match.domain.TradeModel;
import com.flying.cattle.dapr.plugin.dapr.DaprUtil;
import com.flying.cattle.dapr.util.EngineUtil;
import com.flying.cattle.mt.enums.EnumOrderState;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * 撮合处理器(撮合过程)
 * 
 * @author senkyouku
 * @date 2020-07-02
 */
@Component
@Slf4j
public class EngineExecutor {

	final DaprUtil daprUtil;

	final TradeModel tradeModel;

	private StringBuffer makerOids;

	public EngineExecutor(DaprUtil daprUtil, TradeModel tradeModel) {
		this.daprUtil = daprUtil;
		this.tradeModel = tradeModel;
	}

	public MatchOrder run(MatchOrder taker) {
		// 撮合前预处理
		makerOids = new StringBuffer();
		return this.doMatch(taker);
	}

	/**
	 * TODO 处理撮合
	 */
	public MatchOrder doMatch(MatchOrder taker) {
		String tableName = daprUtil
				.getIgniteOrderBook(EngineUtil.getReOrderBookKey(taker));
		List<Long> outData = daprUtil.getOrderBookHead(tableName, !taker.isIfBid(),20);
		if (CollectionUtils.isEmpty(outData)) {
			return taker;
		}

		for (int i = 0; i < outData.size(); i++) {
			MatchOrder maker = daprUtil.get(outData.get(i), tableName);
			// 判断是否可以成交,并给市价买赋值数量
			if (!EngineUtil.tradable(taker, maker)) {
				return taker;
			}
			int contrast = Long.compare(taker.getNoDealNum(), maker.getNoDealNum());
			// 成交数量
			long dealNum = contrast < 0 ? taker.getNoDealNum() : maker.getNoDealNum();
			EngineUtil.makerHandle(maker, dealNum, contrast);
			MatchOrder matchOrder =daprUtil.updateOrderInDB(maker, tableName);
			// maker 处理是成功的
			if (null != matchOrder) {
				EngineUtil.takerHandle(taker, maker.getPrice(), dealNum, contrast);
				// tradeModel.sendMakerDealInfo(taker, maker, dealNum, maker.getPrice());
				makerOids.append(maker.getId()).append(",");
				// taker 是完成状态
				if (taker.getState() == EnumOrderState.ALL_DEAL.getCode()) {
					break;
				}
			}
			// outData 遍历最后一条纪录时自动取数
			if (i == (outData.size()-1)) {
				outData = daprUtil.getOrderBookHead(tableName, !taker.isIfBid(),20);
				i = -1;
			}
		}

		// 发送taker
		// tradeModel.sendTakerDealInfo(taker, makerOids.toString());
		return taker;
	}
}
