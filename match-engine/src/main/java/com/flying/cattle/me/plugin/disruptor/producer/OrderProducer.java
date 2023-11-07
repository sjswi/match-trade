package com.flying.cattle.me.plugin.disruptor.producer;

import org.springframework.beans.BeanUtils;

import com.lmax.disruptor.EventTranslatorOneArg;
import com.lmax.disruptor.RingBuffer;
import com.flying.cattle.me.match.domain.MatchOrder;

public class OrderProducer {

	private final RingBuffer<MatchOrder> ringBuffer;

	public OrderProducer(RingBuffer<MatchOrder> ringBuffer) {
		this.ringBuffer = ringBuffer;
	}

	private static final EventTranslatorOneArg<MatchOrder, MatchOrder> TRANSLATOR = new EventTranslatorOneArg<MatchOrder, MatchOrder>() {

		@Override
		public void translateTo(MatchOrder event, long sequence, MatchOrder input) {
			BeanUtils.copyProperties(input,event);
		}
	};

	public void onData(MatchOrder input) {
		ringBuffer.publishEvent(TRANSLATOR, input);
	}
}
