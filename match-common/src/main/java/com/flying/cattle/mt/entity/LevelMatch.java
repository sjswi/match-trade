/**
 * @filename: LevelMatch.java 2019年12月19日
 * @project match-engine  V1.0
 * Copyright(c) 2020 flying-cattle Co. Ltd. 
 * All right reserved. 
 */
package com.flying.cattle.mt.entity;

import java.io.Serializable;
import java.math.BigDecimal;

import com.alibaba.fastjson.JSON;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassName: LevelMatch
 * @Description: TODO(水平已撮合记录数据)
 * @author flying-cattle
 * @date 2019年12月19日
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LevelMatch implements Serializable {
	
	private static final long serialVersionUID = -4911741995736837242L;

	private BigDecimal price;
	
	private BigDecimal number;
	
	private Boolean eatUp;
	
	public String toJsonString() {
		return JSON.toJSONString(this);
	}



	public LevelMatch(BigDecimal price, BigDecimal number) {
		super();
		this.price = price;
		this.number = number;
	}
}
