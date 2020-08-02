package com.flying.cattle.mt.enums;

import java.util.Arrays;
import java.util.Optional;

/**
 * @author senkyouku
 * @date 2020/06/29
 */
public enum EnumTradeType {

    // 订单状态
    TAKER(1, "吃单"),
    MAKER(2, "挂单"),
    CANCEL(3, "撤单");

    private int    code;
    private String desc;

    public int getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    EnumTradeType(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static Optional<EnumTradeType> of(int code) {
        return Arrays.stream(values()).filter(i -> i.code == code).findFirst();
    }
}
