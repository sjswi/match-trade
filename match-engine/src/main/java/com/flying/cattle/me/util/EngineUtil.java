package com.flying.cattle.me.util;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.LinkedHashMap;

import com.flying.cattle.me.match.domain.MatchOrder;
import com.flying.cattle.mt.enums.EnumOrderState;
import com.flying.cattle.mt.enums.EnumOrderType;
import com.flying.cattle.mt.exception.ExchangeAssert;
import com.flying.cattle.mt.exception.ExchangeError;
import com.flying.cattle.mt.message.OrderDTO;

/**
 * @author KinBug
 * @date 2020-07-02
 */
public class EngineUtil {

	/**
	 * 买订单簿BID
	 */
	private static final String ORDER_BOOK_BID_MAP = "BOOK-BID-";
	/**
	 * 卖订单簿ASK
	 */
	private static final String ORDER_BOOK_ASK_MAP = "BOOK-ASK-";

	/**
	 * 买订单簿BID
	 */
	private static final String ORDER_BOOK_BID_PRICE_MAP = "BOOK-BID-PRICE-";
	/**
	 * 卖订单簿ASK
	 */
	private static final String ORDER_BOOK_ASK_PRICE_MAP = "BOOK-ASK-PRICE-";

	/**
	 * 卖订单簿ASK
	 */
	private static final String ORDER_DE_WEIGHT = "ORDER-DE-WEIGHT-";

	/**
	 * 市价，或者价格实时变动的
	 */
	private static final EnumOrderType[] MARKETS = { EnumOrderType.MTC, EnumOrderType.MTM, EnumOrderType.MPO };

	/**
	 * 会自动撤销的
	 */
	private static final EnumOrderType[] AUTO_CANCEL = { EnumOrderType.MTC, EnumOrderType.FAK, EnumOrderType.FOK };

	/**
	 * 是否是出价单 是 卖订单簿ASK 否 买订单簿BID
	 *
	 * 对手订单簿订单key获取
	 *
	 * @param order 撮合订单
	 * @return String 订单簿订单key
	 */
	public static String getReOrderBookKey(MatchOrder order) {
		return (!order.isIfBid() ? ORDER_BOOK_BID_MAP : ORDER_BOOK_ASK_MAP) + order.getSymbolId();
	}

	/**
	 * 是否是出价单 是 买订单簿BID 否 卖订单簿ASK
	 * <p>
	 * 订单簿订单key获取
	 *
	 * @param order 撮合订单
	 * @return String 订单簿订单key
	 */
	public static String getOrderBookKey(MatchOrder order) {
		return (order.isIfBid() ? ORDER_BOOK_BID_MAP : ORDER_BOOK_ASK_MAP) + order.getSymbolId();
	}

	/**
	 * 是否是出价单 是 买订单簿BID 否 卖订单簿ASK
	 * <p>
	 * 订单簿订单key获取
	 *
	 * @param symbol 交易币对
	 * @param ifBid  买卖标识
	 * @return String 订单簿订单key
	 */
	public static String getOrderBookKey(long symbol, boolean ifBid) {
		return (ifBid ? ORDER_BOOK_BID_MAP : ORDER_BOOK_ASK_MAP) + symbol;
	}

	/**
	 * 获取对方价格盘map的key
	 *
	 * @param order 撮合订单
	 * @return String 订单簿订单key
	 */
	public static String getReOrderBookPriceKey(MatchOrder order) {
		return (!order.isIfBid() ? ORDER_BOOK_BID_PRICE_MAP : ORDER_BOOK_ASK_PRICE_MAP) + order.getSymbolId();
	}

	/**
	 * 获取自己价格盘map的key
	 *
	 * @param order 撮合订单
	 * @return String 订单簿订单key
	 */
	public static String getOrderBookPriceKey(OrderDTO order) {
		return (order.isIfBid() ? ORDER_BOOK_BID_PRICE_MAP : ORDER_BOOK_ASK_PRICE_MAP) + order.getSymbolId();
	}

	/**
	 * -订单临时记录
	 *
	 * @param order 撮合订单
	 * @return String 订单key
	 */
	public static String getOrderDeWeigtKey(MatchOrder order) {
		return ORDER_DE_WEIGHT + order.getSymbolId();
	}

	/**
	 * 判断是否可以交易
	 *
	 * @param maker 当前委托单信息
	 * @param taker 最优匹配订单
	 * @return
	 *
	 */
	public static boolean tradable(MatchOrder taker, MatchOrder maker) {
		// 市价交易
		if (belongToMarketTrading(EnumOrderType.of(taker.getOrderType()))) {
			// taker price source maker price
			taker.setPrice(maker.getPrice());
			// 是市价买
			if (taker.isIfBid()) {
				taker.setNoDealNum(Math.floorDiv(taker.getNoDealAmount(), maker.getPrice()));
			}
			return true;
		}
		// if bid
		if (taker.isIfBid()) {
			return Long.compare(taker.getPrice(), maker.getPrice()) > -1;
		} else {
			return Long.compare(taker.getPrice(), maker.getPrice()) < 1;
		}
	}

	/**
	 *  处理taker撮合结果
	 *
	 * @param taker   计价单
	 * @param maker   交易单 
	 * @param contrast taker对比maker的数量
	 * @param dealNum 交易数量
	 * @param dealNum 当前操作数量
	 * @return
	 */
	public static void takerHandle(MatchOrder taker, long dealPrice, long dealNum, int contrast) {
		if (contrast > 0) {
			// taker 有余
			taker.setState(EnumOrderState.SOME_DEAL.getCode());
		} else if (contrast == 0) {
			// 都无剩余
			taker.setState(EnumOrderState.ALL_DEAL.getCode());
			// 发送maker，和trade
		} else {
			// maker有剩余
			taker.setState(EnumOrderState.ALL_DEAL.getCode());
			// 发送maker，和trade
		}
		// calculate taker
		taker.setDealNum(taker.getDealNum() + dealNum);
		taker.setNoDealNum(taker.getNoDealNum() - dealNum);
		taker.setDealAmount(taker.getDealAmount() + (dealPrice * dealNum));
		taker.setNoDealAmount(taker.getNoDealAmount() - (dealPrice * dealNum));
	}

	/**
	 *  处理maker撮合结果
	 *
	 * @param taker   计价单
	 * @param maker   交易单 
	 * @param contrast taker对比maker的数量
	 * @param dealNum 交易数量
	 * @param dealNum 当前操作数量
	 * @return
	 */
	public static void makerHandle(MatchOrder maker, long dealNum, int contrast) {
		if (contrast > 0) {
			// taker 有余
			maker.setState(EnumOrderState.ALL_DEAL.getCode());
		} else if (contrast == 0) {
			// 都无剩余
			maker.setState(EnumOrderState.ALL_DEAL.getCode());
			// 发送maker，和trade
		} else {
			// maker有剩余
			maker.setState(EnumOrderState.SOME_DEAL.getCode());
			// 发送maker，和trade
		}
		// calculate maker
		maker.setDealNum(maker.getDealNum() + dealNum);
		maker.setNoDealNum(maker.getNoDealNum() - dealNum);
		maker.setDealAmount(maker.getDealAmount() + (maker.getPrice() * dealNum));
		maker.setNoDealAmount(maker.getNoDealAmount() - (maker.getPrice() * dealNum));
	}

	/**
	 * -用于取卖盘最优价订单
	 *
	 * @param o1
	 * @param o2
	 * @return
	 */
	public static int comparatorToMin(MatchOrder o1, MatchOrder o2) {
		// 先比较价格，再比较优先级，优先级相等的情况下比较时间
		int rsg = Long.compare(o1.getPrice(), o2.getPrice());
		// 价格相等的情况下
		if (rsg == 0) {
			// 比优先级：因为优先级取最大，和卖相反
			rsg = Integer.compare(o2.getPriority(), o1.getPriority());
			// 优先级一样
			if (rsg == 0) {
				rsg = Long.compare(o1.getId(), o2.getId());
			}
		}
		return rsg;
	}

	/**
	 * - 相同价格时取最优
	 *
	 * @param o1
	 * @param o2
	 * @return
	 */
	public static int comparatorOptimal(MatchOrder o1, MatchOrder o2) {
		// 比优先级：因为优先级取最大，和卖相反
		int rsg = Integer.compare(o2.getPriority(), o1.getPriority());
		// 优先级一样
		if (rsg == 0) {
			rsg = Long.compare(o1.getId(), o2.getId());
		}
		return rsg;
	}

	/**
	 * -用于取买盘最优价订单
	 *
	 * @param o1
	 * @param o2
	 * @return
	 */
	public static int comparatorToMax(MatchOrder o1, MatchOrder o2) {
		// 先比较价格，再比较优先级，优先级相等的情况下比较时间
		int rsg = Long.compare(o1.getPrice(), o2.getPrice());
		// 价格相等的情况下
		if (rsg == 0) {
			// 比优先级：因为优先级取最大
			rsg = Integer.compare(o1.getPriority(), o2.getPriority());
			// 优先级一样
			if (rsg == 0) {
				// 比时间，要取最小，和卖相反
				rsg = Long.compare(o2.getId(), o1.getId());
			}
		}
		return rsg;
	}

	/**
	 * @Title: belongToMarketTrading @Description: TODO(用于市价计算数量) @param orderType
	 * 参数 @return Boolean 返回类型 @throws
	 */
	public static Boolean belongToMarketTrading(EnumOrderType orderType) {
		if (Arrays.asList(MARKETS).contains(orderType)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * @Title: autoToCancel @Description: TODO(用于自动转撤销) @param orderType 参数 @return
	 * Boolean 返回类型 @throws
	 */
	public static Boolean autoToCancel(EnumOrderType orderType) {
		if (Arrays.asList(AUTO_CANCEL).contains(orderType)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 订单接收消息check
	 */
	public static void checkOrderMqInput(MatchOrder dto) {
		ExchangeAssert.isTrue(dto.getState() == EnumOrderState.ORDER.getCode()
				|| dto.getState() == EnumOrderState.SOME_DEAL.getCode(), ExchangeError.Arg, "订单状态无法撮合");
		ExchangeAssert.notNull(EnumOrderType.of(dto.getOrderType()), ExchangeError.Arg, "订单类型不支持撮合");

		// 限价
		if (dto.getOrderType() == EnumOrderType.GTC.getCode()) {
			ExchangeAssert.isTrue(dto.getNoDealNum() > 0 && dto.getNoDealNum() < dto.getNum(), ExchangeError.Arg,
					"未成交数量异常");
			// 金额前端算好，实际成交额可能小于前端计算额度
			ExchangeAssert.isTrue(dto.getNoDealAmount() > 0 && dto.getNoDealAmount() < dto.getAmount(),
					ExchangeError.Arg, "未成交总额异常");

		}
		// 市价
		if (dto.getOrderType() == EnumOrderType.MTC.getCode() && dto.isIfBid()) {
			ExchangeAssert.isTrue(dto.getNoDealAmount() > 0 && dto.getNoDealAmount() < dto.getAmount(),
					ExchangeError.Arg, "未成交总额异常");
		}
		if (dto.getOrderType() == EnumOrderType.MTC.getCode() && !dto.isIfBid()) {
			ExchangeAssert.isTrue(dto.getNoDealNum() > 0 && dto.getNoDealNum() < dto.getNum(), ExchangeError.Arg,
					"未成交数量异常");
		}
	}

	/**
	 * TODO 订单接收消息check
	 * @author kinbug
	 */
	public static LinkedHashMap<String, String> propertyToMap(Class<?> beanClass) {
		LinkedHashMap<String, String> fieldsMap = new LinkedHashMap<String, String>();
		Field[] fields = beanClass.getDeclaredFields();
		for (Field field : fields) {
			fieldsMap.put(field.getName(), field.getType().getName());
		}
		return fieldsMap;
	}
}
