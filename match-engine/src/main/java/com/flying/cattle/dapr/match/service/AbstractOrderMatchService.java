package com.flying.cattle.me.match.service;

import com.flying.cattle.me.match.domain.MatchOrder;

/**
 * @author senkyouku
 * @date 2020-07-02
 * 订单撮合
 */
public abstract class AbstractOrderMatchService {

    /**
     * 开始撮合
     * 撮合模板
     *
     * @param matchOrder 撮合订单
     */
    public final MatchOrder match(MatchOrder matchOrder) {
        // step1: 操作对手盘及产生trade信息
        MatchOrder o = this.startMatch(matchOrder);
        // step2: 操作当前订单盘口
        return this.afterTakerMatch(o);
    }

    /**
     * 开始撮合
     * 操作对手盘，及产生trade信息
     *
     * @param matchOrder 撮合订单
     * @return 撮合订单
     */
    public abstract MatchOrder startMatch(MatchOrder matchOrder);

    /**
     * 撮合后
     * 操作当前订单盘口
     *
     * @param matchOrder 撮合订单
     * @return 撮合订单
     */
    public abstract MatchOrder afterTakerMatch(MatchOrder matchOrder);
}
