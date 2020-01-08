/**
 * @filename: MatchOutResources.java 2019年12月20日
 * @project exchange  V1.0
 * Copyright(c) 2020 flying-cattle Co. Ltd. 
 * All right reserved. 
 */
package com.flying.cattle.exchange.data;

import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import com.alibaba.fastjson.JSON;
import com.flying.cattle.exchange.model.PushDepth;
import com.flying.cattle.exchange.plugins.kafka.MatchSink;
import lombok.extern.slf4j.Slf4j;

/**
 * @ClassName: MatchOutResources
 * @Description: 撮合输出数据源
 * @author flying-cattle
 * @date 2019年12月20日
 */
@EnableBinding(MatchSink.class)
@Slf4j
public class MatchOutResources {
	
	/**
	 * @Title: push_depth
	 * @Description: TODO(盘口深度数据)
	 * @param  echo
	 * @return void 返回类型
	 * @throws
	 */
	@StreamListener(MatchSink.IN_PUSH_DEPTH)
	public void push_depth(String echo) {
		PushDepth pd = JSON.parseObject(echo, PushDepth.class);
		pd.getBuy();
	}
	
	/**
	 * @Title: push_depth
	 * @Description: TODO(订单变化)
	 * @param  echo
	 * @return void 返回类型
	 * @throws
	 */
	@StreamListener(MatchSink.IN_ORDER_ALTER)
	public void update_order(String echo) {
		log.info("---订单变化："+echo);
	}
	

	/**
	 * @Title: push_depth
	 * @Description: TODO(新的交易记录)
	 * @return void 返回类型
	 * @throws
	 */
	@StreamListener(MatchSink.IN_NEW_TRADE)
	public void new_trade(String echo) {
		log.info("~~~交易信息："+echo);
	}
}
