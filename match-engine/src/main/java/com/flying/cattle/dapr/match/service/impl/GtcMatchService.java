package com.flying.cattle.dapr.match.service.impl;

import com.flying.cattle.dapr.match.EngineExecutor;
import com.flying.cattle.dapr.match.domain.MatchOrder;
import com.flying.cattle.dapr.match.factory.MatchStrategyFactory;
import com.flying.cattle.dapr.match.service.AbstractOrderMatchService;
import com.flying.cattle.dapr.plugin.dapr.DaprUtil;
import com.flying.cattle.mt.enums.EnumOrderState;
import com.flying.cattle.mt.enums.EnumOrderType;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;

/**
 * -限价单撮合
 *
 * @author senkyouku
 * @date 2020-07-02
 */
@Service
public class GtcMatchService extends AbstractOrderMatchService implements InitializingBean {

    private final EngineExecutor matchExecutors;

    private final DaprUtil daprUtil;

    public GtcMatchService(EngineExecutor matchExecutors, DaprUtil daprUtil) {
        this.matchExecutors = matchExecutors;
        this.daprUtil = daprUtil;
    }

    /**
     * GTC开始撮合
     * 操作对手盘，及产生trade信息
     *
     * @param order 撮合订单
     * @return 撮合订单
     */
    @Override
    public MatchOrder startMatch(MatchOrder order) {
        return matchExecutors.run(order);
    }

    /**
     * GTC开始撮合
     * 操作对手盘，及产生trade信息
     *
     * @param order 撮合订单
     * @return 撮合订单
     */
    @Override
    public MatchOrder afterTakerMatch(MatchOrder order) {
        if (order.getState() == EnumOrderState.ORDER.getCode() ||
                order.getState() == EnumOrderState.SOME_DEAL.getCode()) {
            return daprUtil.addToOrderBook(order);
        }
        return order;
    }

    /**
     * GTC注册服务
     */
    @Override
    public void afterPropertiesSet() {
        MatchStrategyFactory.register(EnumOrderType.GTC, this);
    }

}
