/**
 * @filename: OrderState.java 2019年12月19日
 * @project match-engine  V1.0
 * Copyright(c) 2020 flying-cattle Co. Ltd. 
 * All right reserved. 
 */
package com.flying.cattle.mt.enums;

import java.util.Arrays;
import java.util.Optional;

/**
 * @ClassName: OrderState
 * @Description: TODO(Order状态)
 * @author flying-cattle
 * @date 2019年12月19日
 */
public enum OrderState {
	UNPAID(-1, "支付失败"),
    PAID(0, "已支付"),
    SOME_DEAL(1, "部分成交"),
    FINISH_DEAL(2, "全部成交"),
    CANCEL(3, "已撤销"),
    TO_UNDO(4, "撤销申请"),
    ERROR(5, "异常订单"),
    ;

    public final int value;
    public final String label;

    OrderState(int value, String label) {
        this.value = value;
        this.label = label;
    }

    public static Optional<OrderState> of(int value) {
        return Arrays.stream(values()).filter(i -> i.value == value).findFirst();
    }
}

