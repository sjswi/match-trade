/**
 * @filename: SendService.java 2020年1月8日
 * @project match-service  V1.0
 * Copyright(c) 2020 flying-cattle Co. Ltd. 
 * All right reserved. 
 */
package com.flying.cattle.me.plugins.mq;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.messaging.support.MessageBuilder;

/**
 * @ClassName: SendService
 * @Description: TODO(消息--生产者)
 * @author flying-cattle
 * @date 2020年1月8日
 */

@EnableBinding(MatchSink.class)
public class SendService {
	
	@Autowired
	private MatchSink source;

	public void sendOrderAlter(String msg) {
		source.outOrderAlter().send(MessageBuilder.withPayload(msg).build());
	}
	
	public void sendNewTrade(String msg) {
		source.outNewTrade().send(MessageBuilder.withPayload(msg).build());
	}
	
	public void sendPushDepth(String msg) {
		source.pushDepth().send(MessageBuilder.withPayload(msg).build());
	}
}
