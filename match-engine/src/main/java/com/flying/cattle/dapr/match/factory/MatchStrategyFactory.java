package com.flying.cattle.dapr.match.factory;

import com.flying.cattle.dapr.match.service.AbstractOrderMatchService;
import com.flying.cattle.mt.enums.EnumOrderType;
import org.springframework.util.Assert;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

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
