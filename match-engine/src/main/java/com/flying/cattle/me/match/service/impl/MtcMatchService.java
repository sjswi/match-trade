package com.flying.cattle.me.match.service.impl;

import com.flying.cattle.me.plugin.DBUtil;
import com.flying.cattle.me.plugin.mysql.MySQLUtil;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.flying.cattle.me.match.EngineExecutor;
import com.flying.cattle.me.match.domain.MatchOrder;
import com.flying.cattle.me.match.factory.MatchStrategyFactory;
import com.flying.cattle.me.match.service.AbstractOrderMatchService;
import com.flying.cattle.mt.enums.EnumOrderType;

/**
 * -市价转撤销单
 *
 * @author senkyouku
 * @date 2020-07-02
 */
@Service
public class MtcMatchService extends AbstractOrderMatchService implements InitializingBean {

    private final EngineExecutor matchExecutors;

    private final DBUtil dbUtil;

    public MtcMatchService(EngineExecutor matchExecutors, @Qualifier("MySQLUtil") DBUtil dbUtil) {
        this.matchExecutors = matchExecutors;
        this.dbUtil = dbUtil;
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
        return dbUtil.sendCancelOrder(order);
    }

    /**
     * MTC注册服务
     */
    @Override
    public void afterPropertiesSet() {
        MatchStrategyFactory.register(EnumOrderType.MTC, this);
    }

}
