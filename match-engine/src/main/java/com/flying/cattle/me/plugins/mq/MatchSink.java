/**
 * @filename: MatchSink.java 2020年1月8日
 * @project match-engine  V1.0
 * Copyright(c) 2020 flying-cattle Co. Ltd. 
 * All right reserved. 
 */
package com.flying.cattle.me.plugins.mq;

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
	
	//接收队列1
    String IN_NEW_ORDER = "in-new-order";
 
    //接收队列2
    String IN_CANCEL_ORDER = "in-cancel-order";
    
    //发送队列1
    String OUT_ORDER_ALTER = "out-order-alter";
 
    //发送队列2
    String OUT_NEW_TRADE = "out-new-trade";
    
    //发送队列3
    String OUT_PUSH_DEPTH = "out-push-depth";
 
    @Input(MatchSink.IN_NEW_ORDER)
    SubscribableChannel inNewOrder();
 
    @Input(MatchSink.IN_CANCEL_ORDER)
    SubscribableChannel inCancelOrder();
    
    @Output(MatchSink.OUT_ORDER_ALTER)
    MessageChannel outOrderAlter();
    
    @Output(MatchSink.OUT_NEW_TRADE)
    MessageChannel outNewTrade();
    
    @Output(MatchSink.OUT_PUSH_DEPTH)
    MessageChannel pushDepth();
}
