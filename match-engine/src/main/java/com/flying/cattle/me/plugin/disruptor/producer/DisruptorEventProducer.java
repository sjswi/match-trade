package com.flying.cattle.me.plugin.disruptor.producer;

import com.flying.cattle.me.match.domain.MatchOrder;
import com.lmax.disruptor.RingBuffer;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DisruptorEventProducer {
    private final RingBuffer<MatchOrder> ringBuffer;

    public DisruptorEventProducer(RingBuffer<MatchOrder> ringBuffer) {
        this.ringBuffer = ringBuffer;
    }

    public void publish(MatchOrder msg){
        // ringBuffer是个队列，其next方法返回的是下最后一条记录之后的位置，这是个可用位置
        long next = ringBuffer.next();
        try {
            MatchOrder event = ringBuffer.get(next);
            event = msg;
        } catch (Exception e) {
            log.error("向RingBuffer队列存入数据[{}]出现异常=>{}", msg, e.getStackTrace());
        } finally {
            ringBuffer.publish(next);
        }
    }
}
