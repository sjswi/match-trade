package com.flying.cattle.me.data.input;

import com.flying.cattle.me.plugin.mysql.MySQLUtil;
import org.springframework.cglib.beans.BeanCopier;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;

import com.flying.cattle.me.match.domain.MatchOrder;
import com.flying.cattle.me.match.factory.MatchStrategyFactory;
import com.flying.cattle.me.match.service.AbstractOrderMatchService;
import com.flying.cattle.me.plugin.rocketmq.MatchSink;
import com.flying.cattle.mt.enums.EnumOrderType;
import com.flying.cattle.mt.message.OrderDTO;

import lombok.extern.slf4j.Slf4j;

/**
 * @ClassName: ReceiveMqMessage
 * @Description: 订单数据请求
 * @author kinbug
 * @date 2020年6月29日
 */

@EnableBinding(MatchSink.class)
@Slf4j
public class ReceiveMqMessage {
	
	public final MySQLUtil mySQLUtil;
	
	final BeanCopier beanCopier = BeanCopier.create(OrderDTO.class, MatchOrder.class, false);

	public ReceiveMqMessage(MySQLUtil mySQLUtil) {
		this.mySQLUtil = mySQLUtil;
	}

	/**
	 * @Title: new_order_topic 
	 * @Description: 
	 * 接收委托订单数据，必须在同一个group中，且要顺序消费,保证分布式下线性撮合
	 * @return void 返回类型 
	 */
	@StreamListener(MatchSink.INPUT_NEW_ORDER)
	public void newOrder(OrderDTO order) {
		log.info("newOrder request order  {}" + order.toString());
		MatchOrder matchOrder = new MatchOrder();
		beanCopier.copy(order, matchOrder, null);
		if (mySQLUtil.passUnioueVerify(matchOrder)) {
			// 根据订单类型获取撮合策略
			AbstractOrderMatchService service = MatchStrategyFactory.getByOrderType(EnumOrderType.of(matchOrder.getOrderType()));
			if (service == null) {
				log.error("~~~当前订单类型无撮合策略,{}", order.toString());
				return;
			}
			// 开始撮合
			service.match(matchOrder);
		} else {
			log.error("===重复的消息：" + order.toString());
		}
	}

	/**
	 * @Title: newOrder
	 * @Description: 接收撤销订单数据，不需要线性
	 * @param order  Flux<String>
	 * @return void 返回类型 
	 */
	@StreamListener(MatchSink.INPUT_CANCEL_ORDER)
	public void cancelOrder(OrderDTO order) {
		log.info("cancelOrder request order {}" + order.toString());
		MatchOrder matchOrder = new MatchOrder();
		beanCopier.copy(order, matchOrder, null);
		mySQLUtil.doCancelOrder(matchOrder);
	}

}
