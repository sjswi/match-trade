package com.flying.cattle.dapr.match.service.impl;

import com.flying.cattle.dapr.match.EngineExecutor;
import com.flying.cattle.dapr.match.domain.MatchOrder;
import com.flying.cattle.dapr.match.factory.MatchStrategyFactory;
import com.flying.cattle.dapr.match.service.AbstractOrderMatchService;
import com.flying.cattle.dapr.plugin.dapr.DaprUtil;
import com.flying.cattle.mt.enums.EnumOrderType;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;

/**
 * -市价转撤销单
 *
 * @author senkyouku
 * @date 2020-07-02
 */
@Service
public class MtcMatchService extends AbstractOrderMatchService implements InitializingBean {

    private final EngineExecutor matchExecutors;

    private final DaprUtil daprUtil;

    public MtcMatchService(EngineExecutor matchExecutors, DaprUtil daprUtil) {
        this.matchExecutors = matchExecutors;
        this.daprUtil = daprUtil;
    }

    /**
     * MTC开始撮合
     * 操作对手盘，及产生trade信息
     *
     * @param order 撮合订单
     * @return 撮合订单
     */
    @Override
    public MatchOrder startMatch(MatchOrder order) {
        //不同的订单，可以选择不同的撮合逻辑，和不同处理
        return matchExecutors.run(order);
    }

    /**
     * MTC撮合后
     * 操作当前订单盘口,MTC转撤销
     *
     * @param order 撮合订单
     * @return 撮合订单
     */
    @Override
    public MatchOrder afterTakerMatch(MatchOrder order) {
        if (order.getNoDealNum() == 0 && order.getNoDealAmount() == 0) {
            return order;
        }
        return daprUtil.sendCancelOrder(order);
    }

    /**
     * MTC注册服务
     */
    @Override
    public void afterPropertiesSet() {
        MatchStrategyFactory.register(EnumOrderType.MTC, this);
    }

}
