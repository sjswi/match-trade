package com.flying.cattle.mt.enums;

import java.util.Arrays;
import java.util.Optional;

/**
 * @author senkyouku
 * @date 2020/06/29
 */
public enum EnumOrderState {

    // 订单状态
    ERROR(-1, "委托异常"),
    REQUEST(0, "委托请求"),
    ORDER(1, "委托中"),
    SOME_DEAL(2, "部分成交 "),
    ALL_DEAL(3, "全部成交"),
    CANCEL(4, "已撤销");

    private int code;
    private String desc;

    public int getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }


    EnumOrderState(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static Optional<EnumOrderState> of(int code) {
        return Arrays.stream(values()).filter(i -> i.code == code).findFirst();
    }
}
