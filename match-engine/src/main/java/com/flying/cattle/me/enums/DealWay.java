/**
 * @filename: DealWay.java 2019年12月19日
 * @project match-engine  V1.0
 * Copyright(c) 2020 flying-cattle Co. Ltd. 
 * All right reserved. 
 */
package com.flying.cattle.me.enums;

/**
 * @ClassName: DealWay
 * @Description: TODO(交易方式)
 * @author flying-cattle
 * @date 2019年12月19日
 */
public enum DealWay {
    TAKER("taker", "市价"),
    MAKER("maker", "限价"),
    CANCEL("cancel", "系统撤单"),
    ;

    public final String value;
    public final String label;

    DealWay(String value, String label) {
        this.value = value;
        this.label = label;
    }
}
