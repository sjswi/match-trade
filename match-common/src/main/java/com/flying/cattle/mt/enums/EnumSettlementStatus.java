package com.flying.cattle.mt.enums;

import java.util.Arrays;
import java.util.Optional;

/**
 * @author senkyouku
 * @date 2020/06/29
 */
public enum EnumSettlementStatus {

    // 清结算状态
    UN_DO(0, "未清算"),
    DONE(1, "已清算");

    private int    code;
    private String desc;

    public int getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    EnumSettlementStatus(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static Optional<EnumSettlementStatus> of(int code) {
        return Arrays.stream(values()).filter(i -> i.code == code).findFirst();
    }
}
