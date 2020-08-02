package com.flying.cattle.mt.enums;

import java.util.Arrays;
import java.util.Optional;

/**
 * @author senkyouku
 * @date 2020/06/29
 */
public enum EnumPayStatus {

    // 支付状态
    UNPAID(0, "未支付"),
    PAID(1, "已支付 "),
    ERROR(2, "异常订单"),
    FAIL(3,"支付失败");

    private int code;
    private String desc;

    public int getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    EnumPayStatus(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static Optional<EnumPayStatus> of(int code) {
        return Arrays.stream(values()).filter(i -> i.code == code).findFirst();
    }
}
