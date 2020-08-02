package com.flying.cattle.mt.enums;

import lombok.Getter;

import java.util.Arrays;
import java.util.Optional;

/**
 * 订单动作
 * @author kinbug 
 */
@Getter
public enum EnumOrderAction {
    BID(1),
    ASK(0);

    private byte code;

    EnumOrderAction(int code) {
        this.code = (byte) code;
    }

    public static Optional<EnumOrderAction> of(int code) {
        return Arrays.stream(values()).filter(i -> i.code == code).findFirst();
    }

    public EnumOrderAction opposite() {
        return this == BID ? ASK : BID;
    }

}
