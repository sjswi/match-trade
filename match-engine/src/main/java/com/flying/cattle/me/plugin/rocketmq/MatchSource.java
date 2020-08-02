package com.flying.cattle.me.plugin.rocketmq;

import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;

/**
 * @author <a href="mailto:syenging@gmail.com">Thirtyfat</a>
 * @see MatchSource
 * @since
 **/
public interface MatchSource {

    // 发送队列（撮合导致订单变化）
    String OUTPUT_UPDATE_ORDER = "output-update-order";

    // 发送队列（撮合产生的撮合记录）
    String OUTPUT_TRADE = "output-trade";


    @Output(MatchSource.OUTPUT_UPDATE_ORDER)
    MessageChannel updateOrder();

    @Output(MatchSource.OUTPUT_TRADE)
    MessageChannel newTrade();
}
