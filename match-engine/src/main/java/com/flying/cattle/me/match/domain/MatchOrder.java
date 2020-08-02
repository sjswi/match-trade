/*
 * @project   V1.0
 * @filename:
 * Copyright(c) 2020 kinbug Co. Ltd.
 * All right reserved.
 */
package com.flying.cattle.me.match.domain;

import java.io.Serializable;
import java.util.Date;
import org.apache.ignite.cache.affinity.AffinityKey;
import com.alibaba.fastjson.JSON;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author senkyouku
 * @ClassName:
 * @Description: 撮合订单DTO
 * @date
 */
@Data
@NoArgsConstructor
public class MatchOrder implements Serializable {

	private static final long serialVersionUID = -7351851998452714516L;

	/**
	 * -自增id
	 * -排序用id,不用时间排序.
	 */
	private long id;

	/** 账户id*/
	private long accountId;

	/**
	 * -用户ID
	 */
	private long uid;

	/**
	 * -单价
	 */
	private Long price;

	/**
	 * -数量
	 */
	private long num;

	/**
	 * -总额
	 */
	private long amount;

	/**
	 * -是否是出价单（买价）
	 */
	private boolean ifBid;

	/**
	 * -订单类型
	 */
	private int orderType;

	/**
	 * -交易队
	 */
	private int symbolId;

	/**
	 * -状态 见：EnumOrderState
	 */
	private int state;

	/** 已成交数量 */
	private long dealNum;

	/** 未成交交数量(市价买没有未成交数量) */
	private long noDealNum;

	/** 已成交总额 */
	private long dealAmount;

	/** 未成交总额（市价卖没有未成交总额） */
	private long noDealAmount;

	/**
	 * 创建时间
	 */
	private Date createTime;

	/**
	 * 修改时间
	 */
	private Date alterTime;

	/**
	 * 优先级
	 */
	private int priority;

	private transient AffinityKey<Long> key;

	public AffinityKey<Long> key() {
		if (key == null)
			key = new AffinityKey<>(id, symbolId);
		return key;
	}

	public MatchOrder(long id, Long price) {
		this.id = id;
		this.price = price;
	}

	public MatchOrder(long id, long accountId, long uid, Long price, long num, long amount, boolean ifBid,
			int orderType, int symbolId, int state, long dealNum, long noDealNum, long dealAmount, long noDealAmount,
			Date createTime, Date alterTime, int priority) {
		super();
		this.id = id;
		this.accountId = accountId;
		this.uid = uid;
		this.price = price;
		this.num = num;
		this.amount = amount;
		this.ifBid = ifBid;
		this.orderType = orderType;
		this.symbolId = symbolId;
		this.state = state;
		this.dealNum = dealNum;
		this.noDealNum = noDealNum;
		this.dealAmount = dealAmount;
		this.noDealAmount = noDealAmount;
		this.createTime = createTime;
		this.alterTime = alterTime;
		this.priority = priority;
	}

	@Override
	public String toString() {
		return JSON.toJSONString(this);
	}

}
