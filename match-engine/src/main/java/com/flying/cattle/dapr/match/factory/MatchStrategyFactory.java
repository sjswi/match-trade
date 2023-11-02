package com.flying.cattle.me.match.factory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.util.Assert;

import com.flying.cattle.me.match.service.AbstractOrderMatchService;
import com.flying.cattle.mt.enums.EnumOrderType;

/**
 * 撮合策略工厂
 * @author senkyouku
 * @date 2020-07-02
 */
public class MatchStrategyFactory {

    private static Map<Integer, AbstractOrderMatchService> services = new ConcurrentHashMap<>();

    public static AbstractOrderMatchService getByOrderType(EnumOrderType orderType) {
        return services.get(orderType.getCode());
    }

    public static void register(EnumOrderType orderType, AbstractOrderMatchService orderMatchService) {
        Assert.notNull(orderType, "userType can't be null");
        services.put(orderType.getCode(), orderMatchService);
    }
}
