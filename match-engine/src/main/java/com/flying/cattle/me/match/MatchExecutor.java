/**
 * @filename: MatchExecutor.java 2019年12月19日
 * @project match-engine  V1.0
 * Copyright(c) 2020 flying-cattle Co. Ltd. 
 * All right reserved. 
 */
package com.flying.cattle.me.match;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.flying.cattle.me.util.HazelcastUtil;
import com.flying.cattle.mt.entity.LevelMatch;
import com.flying.cattle.mt.entity.MatchOrder;
import com.flying.cattle.mt.enums.DealWay;
import com.flying.cattle.mt.enums.OrderState;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;

import lombok.extern.slf4j.Slf4j;


/**
 * @ClassName: MatchExecutor
 * @Description: TODO(撮合的撮合过程)
 * @author flying-cattle
 * @date 2019年12月19日
 */
@Component
@Slf4j
public class MatchExecutor {

	@Autowired
	HazelcastInstance hzInstance;
	
	@Autowired
	MatchDetailHandler matchDetailHandler;
	
	public MatchOrder doMatch(MatchOrder input) {
		try {
			// 获取对手盘口
			IMap<BigDecimal, BigDecimal> outMap = hzInstance.getMap(HazelcastUtil.getMatchKey(input.getCoinTeam(), !input.getIsBuy()));
			if (null!=outMap&&outMap.size()>0) {
				BigDecimal outPrice = HazelcastUtil.getOptimalMatch(outMap,input.getIsBuy()); 
				if (HazelcastUtil.canMatch(input, outPrice)) {
					BigDecimal outNum = outMap.get(outPrice);
					if (outNum.compareTo(BigDecimal.ZERO)<1) {
						outMap.remove(outPrice);
						doMatch(input); // 递归处理
					}
					int contrast = input.getUnFinishNumber().compareTo(outNum);
					BigDecimal dealNum = contrast > -1?outNum:input.getUnFinishNumber();
					input.setFinishNumber(input.getFinishNumber().add(dealNum));
					input.setUnFinishNumber(input.getUnFinishNumber().subtract(dealNum));
					if (input.getIsBuy()) {
						input.setSurplusFrozen(input.getSurplusFrozen().subtract(outPrice.multiply(dealNum)));
					}else {
						input.setSurplusFrozen(input.getSurplusFrozen().subtract(dealNum));
					}
					//撮合详情记录 >>> 一个价格一个详情
					matchDetailHandler.sendTradeRecord(input, outPrice, dealNum, DealWay.TAKER);
					List<LevelMatch> lms = input.getList();
					LevelMatch lm = new LevelMatch(outPrice, dealNum);
					lm.setEatUp(contrast > -1 ?true:false);
					lms.add(lm);
					input.setList(lms);
					if (contrast == 1) {//水平价格被吃完
						outMap.remove(outPrice);
						input.setState(OrderState.SOME_DEAL.value);
						doMatch(input); // 递归处理
					}else if (contrast == 0) {//都被吃完
						outMap.remove(outPrice);
						input.setState(OrderState.FINISH_DEAL.value);
					}else {//水平价格有剩余
						outMap.compute(outPrice, (k,v) -> v.subtract(dealNum));
						input.setState(OrderState.FINISH_DEAL.value);
					}
				}
			}
		} catch (Exception e) {
			log.error("执行撮合错误："+e);
			log.error("执行撮合错误源数据："+input);
			input.setState(3);//撤销掉
		} 
		return input;
	}
}
