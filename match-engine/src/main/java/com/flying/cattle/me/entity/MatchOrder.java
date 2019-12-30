/**
 * @filename: MatchOrder.java 2019年12月19日
 * @project match-engine  V1.0
 * Copyright(c) 2020 flying-cattle Co. Ltd. 
 * All right reserved. 
 */
package com.flying.cattle.me.entity;

import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName: MatchOrder
 * @Description: TODO(撮合中使用的订单)
 * @author flying-cattle
 * @date 2019年12月19日
 */
public class MatchOrder extends Order{
	
	private static final long serialVersionUID = -4205367001273335512L;

	private List<LevelMatch> list = new ArrayList<LevelMatch>();

	public List<LevelMatch> getList() {
		return list;
	}

	public void setList(List<LevelMatch> list) {
		this.list = list;
	}
}
