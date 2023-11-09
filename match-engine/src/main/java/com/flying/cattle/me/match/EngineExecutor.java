package com.flying.cattle.me.match;

import java.util.List;

import com.flying.cattle.me.plugin.DBUtil;
import com.flying.cattle.me.plugin.mysql.MySQLUtil;
import org.apache.ignite.IgniteCache;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.flying.cattle.me.match.domain.MatchOrder;
import com.flying.cattle.me.match.domain.TradeModel;

import com.flying.cattle.me.util.EngineUtil;
import com.flying.cattle.mt.enums.EnumOrderState;

import lombok.extern.slf4j.Slf4j;

/**
 * 撮合处理器(撮合过程)
 * 
 * @author senkyouku
 * @date 2020-07-02
 */
@Component
@Slf4j
public class EngineExecutor {

	final DBUtil dbUtil;

	final TradeModel tradeModel;

	private StringBuffer makerOids;

	public EngineExecutor(@Qualifier("MySQLUtil") DBUtil dbUtil, TradeModel tradeModel) {
		this.dbUtil = dbUtil;
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
		String tableName = dbUtil
				.getIgniteOrderBook(EngineUtil.getReOrderBookKey(taker));
		List<Long> outData = dbUtil.getOrderBookHead(tableName, !taker.isIfBid(),20);
		if (CollectionUtils.isEmpty(outData)) {
			return taker;
		}

		for (int i = 0; i < outData.size(); i++) {
			MatchOrder maker = dbUtil.get(outData.get(i), tableName);
			// 判断是否可以成交,并给市价买赋值数量
			if (!EngineUtil.tradable(taker, maker)) {
				return taker;
			}
			int contrast = Long.compare(taker.getNoDealNum(), maker.getNoDealNum());
			// 成交数量
			long dealNum = contrast < 0 ? taker.getNoDealNum() : maker.getNoDealNum();
			EngineUtil.makerHandle(maker, dealNum, contrast);
			MatchOrder matchOrder =dbUtil.updateOrderInDB(maker, tableName);
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
				outData = dbUtil.getOrderBookHead(tableName, !taker.isIfBid(),20);
				i = -1;
			}
		}

		// 发送taker
		// tradeModel.sendTakerDealInfo(taker, makerOids.toString());
		return taker;
	}
}
