package com.flying.cattle.dapr.data.out;

import com.flying.cattle.dapr.match.domain.MatchOrder;
import com.flying.cattle.dapr.plugin.rocketmq.MatchSource;
import com.flying.cattle.mt.enums.EnumOrderState;
import com.flying.cattle.mt.enums.EnumTradeType;
import com.flying.cattle.mt.message.OrderDTO;
import com.flying.cattle.mt.message.TradeDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.cglib.beans.BeanCopier;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.support.MessageBuilder;

import java.util.Date;

/**
 * @ClassName: SendService
 * @Description: TODO(消息--生产者)
 * @author flying-cattle
 * @date 2020年1月8日
 */
@EnableBinding(MatchSource.class)
@Slf4j
public class SendService {
	
	private final MatchSource source;

	final BeanCopier beanCopier = BeanCopier.create(MatchOrder.class, OrderDTO.class, false);

	public SendService(MatchSource source) {
		this.source = source;
	}

	public void sendUpdateOrder(@Payload MatchOrder order) {
		// 撮合导致订单变化
		//log.info("===发送UpdateOrder: "+order.toString());

		OrderDTO dto = new OrderDTO();
		beanCopier.copy(order, dto, null);

		source.updateOrder().send(MessageBuilder.withPayload(dto).build());
	}
	
	public void sendNewTrade(@Payload TradeDTO trade) {
		// 撮合产生的撮合记录
		//log.info("===发送NewTrade: "+trade.toString());
		source.newTrade().send(MessageBuilder.withPayload(trade).build());
	}


	public void sendCancelOrder(@Payload MatchOrder order){

		OrderDTO maker = new OrderDTO();
		BeanUtils.copyProperties(order, maker);


		// maker 撤销记录，以订单ID取模发送
		maker.setState(EnumOrderState.CANCEL.getCode());
		source.updateOrder().send(MessageBuilder.withPayload(maker).build());
		// maker 清算记录，以uid取模清算
		TradeDTO trade = new TradeDTO(0, 0, 0, maker.getUid(),
				maker.getAccountId(), maker.getId(), maker.isIfBid(), maker.getNoDealNum(), maker.getPrice(), maker.getNoDealAmount(), maker.getSymbolId(),
				new Date(), EnumTradeType.CANCEL.getCode(), "");

		source.newTrade().send(MessageBuilder.withPayload(trade).build());
	}

}
