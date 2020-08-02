//package com.flying.cattle.me.plugin.disruptor;
//
//import java.util.concurrent.ThreadFactory;
//
//import com.lmax.disruptor.RingBuffer;
//import com.lmax.disruptor.YieldingWaitStrategy;
//import com.lmax.disruptor.dsl.Disruptor;
//import com.lmax.disruptor.dsl.ProducerType;
//import com.flying.cattle.me.match.domain.MatchOrder;
//import com.flying.cattle.me.plugin.disruptor.factory.OrderFactory;
//import com.flying.cattle.me.plugin.disruptor.handler.MatchAfterTakerHandler;
//import com.flying.cattle.me.plugin.disruptor.handler.MatchHandler;
//import com.flying.cattle.me.plugin.disruptor.producer.OrderProducer;
//
//public class DisruptorConfig {
//
//	private static final Disruptor<MatchOrder> DISRUPTOR;
//
//	static{
//		OrderFactory factory = new OrderFactory();
//		int ringBufferSize = 1024 * 1024;
//		ThreadFactory threadFactory = Thread::new;
//		DISRUPTOR = new Disruptor<>(factory, ringBufferSize, threadFactory,ProducerType.SINGLE, new YieldingWaitStrategy());
//		DISRUPTOR.handleEventsWithWorkerPool(new MatchHandler()).then(new MatchAfterTakerHandler());
//		DISRUPTOR.start();
//	}
//
//	public static void producer(MatchOrder input){
//		RingBuffer<MatchOrder> ringBuffer = DISRUPTOR.getRingBuffer();
//		OrderProducer producer = new OrderProducer(ringBuffer);
//		producer.onData(input);
//	}
//}
