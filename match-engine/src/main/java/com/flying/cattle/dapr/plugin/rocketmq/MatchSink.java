package com.flying.cattle.dapr.plugin.rocketmq;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.messaging.SubscribableChannel;

/**
 * @ClassName: MatchSink
 * @Description: TODO(定义管道)
 * @author kinbug
 * @date 2020年6月29日
 */
public interface MatchSink {

	// 接收队列（新委托单）
	String INPUT_NEW_ORDER = "input-new-order";

	// 接收队列（新撤销单）
	String INPUT_CANCEL_ORDER = "input-cancel-order";

	@Input(MatchSink.INPUT_NEW_ORDER)
    SubscribableChannel newOrder();
 
    @Input(MatchSink.INPUT_CANCEL_ORDER)
    SubscribableChannel cancel();

}
