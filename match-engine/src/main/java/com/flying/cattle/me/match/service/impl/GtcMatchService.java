package com.flying.cattle.me.match.service.impl;

import com.flying.cattle.me.plugin.mysql.MySQLUtil;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;

import com.flying.cattle.me.match.EngineExecutor;
import com.flying.cattle.me.match.domain.MatchOrder;
import com.flying.cattle.me.match.factory.MatchStrategyFactory;
import com.flying.cattle.me.match.service.AbstractOrderMatchService;

import com.flying.cattle.mt.enums.EnumOrderState;
import com.flying.cattle.mt.enums.EnumOrderType;

/**
 * -限价单撮合
 *
 * @author senkyouku
 * @date 2020-07-02
 */
@Service
public class GtcMatchService extends AbstractOrderMatchService implements InitializingBean {

    private final EngineExecutor matchExecutors;

    private final MySQLUtil mySQLUtil;

    public GtcMatchService(EngineExecutor matchExecutors, MySQLUtil igniteUtil) {
        this.matchExecutors = matchExecutors;
        this.mySQLUtil = igniteUtil;
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
            return mySQLUtil.addToOrderBook(order);
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
