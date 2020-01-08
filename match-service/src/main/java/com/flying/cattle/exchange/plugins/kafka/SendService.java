/**
 * @filename: SendService.java 2020年1月8日
 * @project match-service  V1.0
 * Copyright(c) 2020 flying-cattle Co. Ltd. 
 * All right reserved. 
 */
package com.flying.cattle.exchange.plugins.kafka;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.messaging.support.MessageBuilder;

/**
 * @ClassName: SendService
 * @Description: TODO(kafka消息发送类)
 * @author flying-cattle
 * @date 2020年1月8日
 */

@EnableBinding(MatchSink.class)
public class SendService {
	
	@Autowired
	private MatchSink source;

	public void sendNewOrder(String msg) {
		source.outNewOrder().send(MessageBuilder.withPayload(msg).build());
	}
	
	public void sendCancelOrder(String msg) {
		source.outCancelOrder().send(MessageBuilder.withPayload(msg).build());
	}
}
