/**
 * @filename: MatchSink.java 2020年1月8日
 * @project match-engine  V1.0
 * Copyright(c) 2020 flying-cattle Co. Ltd. 
 * All right reserved. 
 */
package com.flying.cattle.exchange.plugins.mq;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.SubscribableChannel;

/**
 * @ClassName: MatchSink
 * @Description: TODO(定义管道)
 * @author flying-cattle
 * @date 2020年1月8日
 */
public interface MatchSink {
	
	//发送队列1
    String OUT_NEW_ORDER = "out-new-order";
 
    //发送队列2
    String OUT_CANCEL_ORDER = "out-cancel-order";
    
    //接收队列1
    String IN_ORDER_ALTER = "in-order-alter";
 
    //接收队列2
    String IN_NEW_TRADE = "in-new-trade";
    
    //接收队列3
    String IN_PUSH_DEPTH = "in-push-depth";
 
    @Output(MatchSink.OUT_NEW_ORDER)
    MessageChannel outNewOrder();
 
    @Output(MatchSink.OUT_CANCEL_ORDER)
    MessageChannel outCancelOrder();
    
    @Input(MatchSink.IN_ORDER_ALTER)
    SubscribableChannel inOrderAlter();
    
    @Input(MatchSink.IN_NEW_TRADE)
    SubscribableChannel inNewTrade();
    
    @Input(MatchSink.IN_PUSH_DEPTH)
    SubscribableChannel pushDepth();
}
