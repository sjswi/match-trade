package com.flying.cattle.mt.entity;

import com.alibaba.fastjson.JSON;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * ps: 优化①：订单排序用id，不用时间
 * 	           优化②：计算的自动不用包装类和BigDecimal，虽然节省不了多少，蚊子腿也是肉啊。而且不会有精度问题
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderBase implements Serializable {

	private static final long serialVersionUID = 2600161949279286241L;

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

	@Override
	public String toString() {
		return JSON.toJSONString(this);
	}

}
