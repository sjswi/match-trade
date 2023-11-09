package com.flying.cattle.me.plugin.disruptor.factory;

import com.flying.cattle.me.match.domain.MatchOrder;
import com.lmax.disruptor.EventFactory;

public class DisruptorEventFactory implements EventFactory<MatchOrder> {
    @Override
    public MatchOrder newInstance() {
        // TODO Auto-generated method stub
        return new MatchOrder();
    }

}
