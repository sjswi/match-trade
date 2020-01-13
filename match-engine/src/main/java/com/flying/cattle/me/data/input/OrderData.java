/**
 * @filename: OrderData.java 2019年12月19日
 * @project match-engine  V1.0
 * Copyright(c) 2020 flying-cattle Co. Ltd. 
 * All right reserved. 
 */
package com.flying.cattle.me.data.input;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;

import com.alibaba.fastjson.JSON;
import com.flying.cattle.me.data.out.PushData;
import com.flying.cattle.me.plugins.disruptor.producer.OrderProducer;
import com.flying.cattle.me.plugins.mq.MatchSink;
import com.flying.cattle.me.util.HazelcastUtil;
import com.flying.cattle.mt.entity.CancelOrderParam;
import com.flying.cattle.mt.entity.MatchOrder;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.transaction.TransactionContext;
import com.hazelcast.transaction.TransactionOptions;
import com.lmax.disruptor.RingBuffer;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

/**
 * @ClassName: OrderData
 * @Description: TODO(订单数据请求)
 * @author flying-cattle
 * @date 2019年12月19日
 */

@EnableBinding(MatchSink.class)
@Slf4j
public class OrderData {

	@Autowired
	RingBuffer<MatchOrder> ringBuffer;

	@Autowired
	HazelcastInstance hzInstance;

	@Autowired
	PushData pushData;
	long start = 0;

	/**
	 * @Title: new_order 
	 * @Description: 
	 * TODO(接收委托订单数据，必须在同一个group中，保证分布式下线性撮合) 
	 * @return void 返回类型 
	 */
	@StreamListener(MatchSink.IN_NEW_ORDER)
	public void new_order(Flux<String> flux) {
		flux.subscribe(message -> {
			MatchOrder order = JSON.parseObject(message, MatchOrder.class);
			
			OrderProducer producer = new OrderProducer(ringBuffer);
			if (order.getUid().longValue() == 1) {
				start = System.currentTimeMillis();
			}
			if (order.getUid().longValue() % 10000 == 0) {
				log.info("当前是第：" + order.getUid() + "条数据，耗时：" + (System.currentTimeMillis() - start) + "(毫秒)");
			}
			producer.onData(order);
		});
	}

	/**
	 * @Title: new_order 
	 * @Description: TODO(接收撤销订单数据，不需要线性) 
	 * @param 参数  Flux<String>
	 * @return void 返回类型 
	 */
	@StreamListener(MatchSink.IN_CANCEL_ORDER)
	public void cancel_order(Flux<String> flux) {
		flux.subscribe(message -> {
			log.info("~~~收到删除: {}", message);
			CancelOrderParam cancel = JSON.parseObject(message, CancelOrderParam.class);
			IMap<Long, MatchOrder> order_map = hzInstance
					.getMap(HazelcastUtil.getOrderBookKey(cancel.getCoinTeam(), cancel.getIsBuy()));
			if (order_map.containsKey(cancel.getId())) {
				TransactionOptions options = new TransactionOptions()
						.setTransactionType(TransactionOptions.TransactionType.ONE_PHASE);
				TransactionContext context = hzInstance.newTransactionContext(options);
				context.beginTransaction();
				try {
					IMap<BigDecimal, BigDecimal> map = hzInstance
							.getMap(HazelcastUtil.getMatchKey(cancel.getCoinTeam(), cancel.getIsBuy()));
					MatchOrder cmo = order_map.remove(cancel.getId());
					map.compute(cmo.getPrice(), (k, v) -> v.subtract(cmo.getUnFinishNumber()));
					if (map.get(cmo.getPrice()).compareTo(BigDecimal.ZERO) > -1) {
						context.commitTransaction();
						pushData.updateOrder(cmo); // 推送撤销成功结果
					} else {
						throw new Exception();
					}
				} catch (Exception e) {
					log.info("~~~撤销订单报错原型: {}，异常{}", message,e.getMessage());
					context.rollbackTransaction();
				}
			}
		});
	}
}
