package com.flying.cattle.me.plugin.disruptor;

import com.flying.cattle.me.match.domain.MatchOrder;
import com.flying.cattle.me.plugin.disruptor.factory.DisruptorEventFactory;
import com.flying.cattle.me.plugin.disruptor.producer.DisruptorEventProducer;
import com.lmax.disruptor.BlockingWaitStrategy;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import org.springframework.scheduling.concurrent.CustomizableThreadFactory;

import javax.annotation.PostConstruct;

public abstract class DisruptorInsertService {
    /**
     * 环形队列缓冲区大小，为测试看效果所以下面设置得小点，生产上应该配置大些
     * 必须为2的N次方（能将求模运算转为位运算提高效率）
     * 当超过此大小后，再有生产加入时会进行阻塞，
     * 直到有消费者处理完，有空位后则继续加入
     */
    protected int BUFFER_SIZE = 1024;

    protected Disruptor<MatchOrder> disruptor;

    private DisruptorEventProducer producer;

    @PostConstruct
    private void init() {
        // 实例化，handler- 为线程名
        disruptor = new Disruptor<>(new DisruptorEventFactory(), BUFFER_SIZE,
                new CustomizableThreadFactory("handler-"), ProducerType.SINGLE, new BlockingWaitStrategy());

        handleEvents();

        // 启动
        disruptor.start();

        // 实例化生产者
        producer = new DisruptorEventProducer(disruptor.getRingBuffer());
    }

    /**
     * 发布事件
     * @param value
     * @return
     */
    public void publish(MatchOrder value) {
        producer.publish(value);
    }

    public long getCursor() {
        return disruptor.getCursor();
    }

    /**
     * 留给子类实现具体的事件消费逻辑
     */
    protected abstract void handleEvents();

}
