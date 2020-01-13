/**
 * @filename: OrderType.java 2019年12月19日
 * @project match-engine  V1.0
 * Copyright(c) 2020 flying-cattle Co. Ltd. 
 * All right reserved. 
 */
package com.flying.cattle.mt.enums;

/**
 * @ClassName: OrderType
 * @Description: TODO(Order类型)
 * @author flying-cattle
 * @date 2019年12月19日
 */
public enum OrderType {
	MARKET("market", "市价"),
    LIMIT("limit", "限价"),
    ;

    public final String value;
    public final String label;

    OrderType(String value, String label) {
        this.value = value;
        this.label = label;
    }
}
