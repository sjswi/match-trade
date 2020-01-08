/**
 * @filename: MatchSink.java 2020年1月8日
 * @project match-engine  V1.0
 * Copyright(c) 2020 flying-cattle Co. Ltd. 
 * All right reserved. 
 */
package com.flying.cattle.me.data.sink;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.messaging.SubscribableChannel;

/**
 * @ClassName: MatchSink
 * @Description: TODO(定义管道)
 * @author flying-cattle
 * @date 2020年1月8日
 */
public interface MatchSink {
	
	//接收队列1
    String NEW_ORDER = "new-order";
 
    //接收队列1
    String CANCEL_ORDER = "cancel-order";
 
    @Input(MatchSink.NEW_ORDER)
    SubscribableChannel newrder();
 
    @Input(MatchSink.CANCEL_ORDER)
    SubscribableChannel cancelrder();
}
