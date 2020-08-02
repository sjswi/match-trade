package com.flying.cattle.mt.message;

import java.io.Serializable;
import java.util.Date;

import com.alibaba.fastjson.JSON;
import com.flying.cattle.mt.entity.OrderBase;

import lombok.Data;

/**
 * @author xu.qian
 * @date 2020/06/29
 */
@Data
public class OrderDTO extends OrderBase implements Serializable {

	private static final long serialVersionUID = -2157151705152128003L;

	public OrderDTO() {
	}

	public OrderDTO(long id, long accountId, long uid, long price, long num, long amount, boolean ifBid, int orderType,
			int symbolId, int state, long dealNum, long noDealNum, long dealAmount, long noDealAmount, Date createTime,
			Date alterTime, int priority) {
		super(id, accountId, uid, price, num, amount, ifBid, orderType, symbolId, state, dealNum, noDealNum, dealAmount,
				noDealAmount, createTime, alterTime, priority);
	}

	@Override
	public String toString() {
		return JSON.toJSONString(this);
	}

	public int compareTo(OrderDTO o) {
		// 编写一个比较规则
		return Long.compare(this.getId(), o.getId());
	}
}
