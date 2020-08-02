package com.flying.cattle.mt.enums;

import java.util.Arrays;
import java.util.Optional;

/***
 * Order type collection
 * @author kinbug 
 */
public enum EnumOrderType {
	/* 订单类型 */
	GTC(1, "正常的限价单"),
	MTC(2, "市价转撤销，无对手价时撤销"),
	GTD(3, "易者指定交易日之前有效，之后撤销"),
	IOC(4, "立即成交否则取消指令"),
	FAK(5, "指定价位成交"),
	FOK(6,"指定价位全部成交"),
	MTM(7, "市价转限价"),
	MPO(8, "市价保护单，成交到设置的保护价位置，未成交部分转为临界值得限价单"),
	LCE(9, "冰山单"),
	SLO(10, "止损限价单"),
	SWP(11, "止损保护单");

	private int code;
	private String desc;

	public int getCode() {
		return code;
	}

	public String getDesc() {
		return desc;
	}

	EnumOrderType(int code, String desc) {
		this.code =  code;
		this.desc =  desc;

	}

	public static EnumOrderType of(int code) {
		Optional<EnumOrderType> optional =  Arrays.stream(values()).filter(i -> i.code == code).findFirst();
		return optional.orElse(null);
	}
}
