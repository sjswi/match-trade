package com.flying.cattle.mt.message;

import com.alibaba.fastjson.JSON;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;
import java.util.Date;

/**
 * @author senkyouku
 * @date 2020/06/29
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TradeDTO implements Serializable {

	private static final long serialVersionUID = 2089077473475988535L;

	/**
	 * taker user id
	 */
	private long takerUid;

	/**
	 * taker账户id
	 */
	private long takerAccountId;

	/**
	 * taker order id
	 */
	private long takerOid;

	/**
	 * maker user ID
	 */
	private long makerUid;
	
	/**
	 * maker账户id
	 */
	private long makerAccountId;

	/**
	 * 对手委托单分布式自增ID
	 */
	private long makerOid;
	
	/**
	 * 是否是出价单
	 */
	private boolean ifBid;
	
	/**
	 * 交易数量
	 */
	private long num;
	
	/**
	 * 实际成交价
	 */
	private long price;
	
	/**
	 * 成交总价
	 */
	private long amount;
	
	/**
	 * 交易对ID
	 */
	private int symbolId;
	
	/**
	 * 成交时间
	 */
	private Date tradeTime;
	
	/**
	 * 撮合方式
	 */
	private int tradeWay;
	
	/**
	 * more maker order id
	 */
	private String makerOids;

	@Override
	public String toString() {
		return JSON.toJSONString(this);
	}
}
