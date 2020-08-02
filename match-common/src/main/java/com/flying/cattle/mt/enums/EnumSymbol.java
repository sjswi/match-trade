package com.flying.cattle.mt.enums;

import lombok.Getter;

import java.util.Arrays;
import java.util.Optional;

@Getter
public enum EnumSymbol {
	
	BTC_USDT(0,"BTC","USDT");
	
	private int code;
	
	private String one;
	
	private String two;

	private EnumSymbol(int code, String one, String two) {
		this.code = code;
		this.one = one;
		this.two = two;
	}

	public static Optional<EnumSymbol> of(int code) {
		return Arrays.stream(values()).filter(i -> i.code == code).findFirst();
	}
}
