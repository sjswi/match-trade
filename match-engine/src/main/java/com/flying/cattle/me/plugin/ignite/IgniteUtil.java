package com.flying.cattle.me.plugin.ignite;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import javax.cache.Cache.Entry;
import javax.cache.expiry.CreatedExpiryPolicy;
import javax.cache.expiry.Duration;

import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.cache.CacheAtomicityMode;
import org.apache.ignite.cache.QueryEntity;
import org.apache.ignite.cache.QueryIndex;
import org.apache.ignite.cache.query.QueryCursor;
import org.apache.ignite.cache.query.ScanQuery;
import org.apache.ignite.cache.query.SqlFieldsQuery;
import org.apache.ignite.configuration.CacheConfiguration;
import org.springframework.cglib.beans.BeanCopier;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.flying.cattle.me.data.out.SendService;
import com.flying.cattle.me.match.domain.MatchOrder;
import com.flying.cattle.me.util.EngineUtil;
import com.flying.cattle.mt.message.DepthDTO;
import com.flying.cattle.mt.message.OrderDTO;

@Component
public class IgniteUtil {

	final Ignite ignite;

	final ConcurrentHashMap<String, String> igniteCacheKeys;

	final SendService sendService;

	public IgniteUtil(Ignite ignite, ConcurrentHashMap<String, String> igniteCacheKeys, SendService sendService) {
		this.ignite = ignite;
		this.igniteCacheKeys = igniteCacheKeys;
		this.sendService = sendService;
	}

	/**
	 * TODO 判断是否通过唯一性校验
	 * 
	 * @param order 委托单
	 * @return Boolean 返回类型（true：通过，false：为通过）
	 */
	public Boolean passUnioueVerify(MatchOrder order) {
		IgniteCache<Long, Long> orderIds = this.getIgniteCacheIdskey(EngineUtil.getOrderDeWeigtKey(order));
		if (!orderIds.containsKey(order.getId())) {
			orderIds.put(order.getId(), order.getId());
			return Boolean.TRUE;
		}
		return Boolean.FALSE;
	}

	/**
	 * TODO 获取对应币对去重的缓存
	 * 
	 * @param idsKeyName 委托单
	 * @return Boolean 返回类型（true：通过，false：为通过）
	 */
	public IgniteCache<Long, Long> getIgniteCacheIdskey(String idsKeyName) {
		if (igniteCacheKeys.contains(idsKeyName)) {
			return ignite.cache(idsKeyName);
		} else {
			igniteCacheKeys.put(idsKeyName, idsKeyName);
			CacheConfiguration<Long, Long> idsCache = new CacheConfiguration<Long, Long>(idsKeyName);
			idsCache.setExpiryPolicyFactory(CreatedExpiryPolicy.factoryOf(new Duration(TimeUnit.MINUTES, 30)));
			idsCache.setAtomicityMode(CacheAtomicityMode.TRANSACTIONAL);
			return ignite.getOrCreateCache(idsCache);
		}
	}

	/**
	 * TODO 获取对应币对的订单薄
	 * 
	 * @param orderBookKey 委托单
	 * @return Boolean 返回类型（true：通过，false：为通过）
	 */
	public IgniteCache<Long, MatchOrder> getIgniteOrderBook(String orderBookKey) {
		if (igniteCacheKeys.contains(orderBookKey)) {
			return ignite.cache(orderBookKey);
		} else {
			igniteCacheKeys.put(orderBookKey, orderBookKey);
			CacheConfiguration<Long, MatchOrder> orderBookCache = new CacheConfiguration<Long, MatchOrder>(orderBookKey);
			// 构建查询对象
			QueryEntity queryEntity = new QueryEntity();
			queryEntity.setKeyType(Long.class.getName());
			queryEntity.setValueType(MatchOrder.class.getName());
			// 构建结果字段
			LinkedHashMap<String, String> fields = EngineUtil.propertyToMap(MatchOrder.class);
			queryEntity.setFields(fields);
			// 构建price的索引
			boolean sort = true; // 由小到大
			if (orderBookKey.contains("BID")) {
				sort = false;
			}
			Collection<QueryIndex> indexes = new ArrayList<>(1);
			indexes.add(new QueryIndex("price", sort));
			queryEntity.setIndexes(indexes);
			orderBookCache.setQueryEntities(Arrays.asList(queryEntity));
			// 副本级配置
			orderBookCache.setBackups(1);
			orderBookCache.setAtomicityMode(CacheAtomicityMode.ATOMIC);
			return ignite.getOrCreateCache(orderBookCache);
		}
	}

	/**
	 * TODO 获取前100单
	 * 
	 * @param ifBid 是否是买单
	 * @param order 委托单
	 * @return Boolean 返回类型（true：通过，false：为通过）
	 */
	public List<Long> getOrderBookHead(IgniteCache<Long, MatchOrder> order, boolean ifBid, int limitNum) {
//		String sql = "from MatchOrder ORDER BY price limit "+limitNum;
//		if (ifBid) {
//			sql = "from MatchOrder ORDER BY price DESC limit "+limitNum;
//		}
//		SqlQuery<Long, MatchOrder> query = new SqlQuery<>(MatchOrder.class, sql);

		SqlFieldsQuery query;
		if (ifBid) {
			query = new SqlFieldsQuery("SELECT id from MatchOrder ORDER BY price DESC limit " + limitNum);
		} else {
			query = new SqlFieldsQuery("SELECT id from MatchOrder ORDER BY price limit " + limitNum);
		}
		return order.query(query).getAll().stream().map(lit -> (Long) lit.get(0)).collect(Collectors.toList());
	}

	/**
	 * TODO 获取前N单
	 *
	 * @param ifBid 是否是买单
	 * @param order 委托单
	 * @return Boolean 返回类型（true：通过，false：为通过）
	 */
	public List<MatchOrder> getOrderBookHot(IgniteCache<Long, MatchOrder> order, boolean ifBid, int limitNum) {
		SqlFieldsQuery query = new SqlFieldsQuery("SELECT id,price from MatchOrder ORDER BY price limit " + limitNum);
		return order.query(query).getAll().stream().map(lit -> new MatchOrder((Long) lit.get(0), (Long) lit.get(1))
		).collect(Collectors.toList());
	}


	/**
	 * TODO 执行撤销2
	 *
	 * @param order 委托单
	 * @return Boolean 返回类型
	 */
	@Async
	public void doCancelOrder(MatchOrder order) {
		IgniteCache<Long, MatchOrder> map = ignite.cache(EngineUtil.getOrderBookKey(order));
		MatchOrder cancel = map.getAndRemove(order.getId());
		if (null != cancel) {
			sendService.sendCancelOrder(cancel);
		}
	}

	/**
	 * TODO 发送撤销2
	 * 
	 * @param order 委托单
	 * @return Boolean 返回类型
	 */
	@Async
	public MatchOrder sendCancelOrder(MatchOrder order) {
		sendService.sendCancelOrder(order);
		return order;
	}

	/**
	 * TODO 添加到对应的订单薄
	 *
	 * @param order 委托单
	 * @return Boolean 返回类型（true：通过，false：为通过）
	 */
	public MatchOrder addToOrderBook(MatchOrder order) {
		String orderBookKey = EngineUtil.getOrderBookKey(order);
		IgniteCache<Long, MatchOrder> orderBook = getIgniteOrderBook(orderBookKey);
		return orderBook.getAndPut(order.getId(), order);
	}

	/**
	 * 获取币对下
	 *
	 * @param symbol 币对标识
	 * @param ifBid  买卖标识
	 * @return List<OrderDTO>
	 * @author senkyouku
	 */
	public List<MatchOrder> listAll(int symbol, boolean ifBid) {
		IgniteCache<Long, MatchOrder> cache = ignite.cache(EngineUtil.getOrderBookKey(symbol, ifBid));
		QueryCursor<Entry<Long, MatchOrder>> cursor = cache.query(new ScanQuery<>());
		List<Entry<Long, MatchOrder>> list = cursor.getAll();
		return list.stream().map(Entry::getValue).collect(Collectors.toList());
	}

	/**
	 * 获取第一档深度
	 *
	 * @param symbol 币对标识
	 * @param ifBid  买卖标识
	 * @return List<OrderDTO>
	 * @author senkyouku
	 */
	public List<DepthDTO> listFirstDepth(int symbol, boolean ifBid) {
		IgniteCache<Long, MatchOrder> cache = ignite.cache(EngineUtil.getOrderBookKey(symbol, ifBid));

		String sql = ifBid ? "select price,SUM(noDealNum) from MatchOrder GROUP BY price ORDER BY price DESC"
				: "select price,SUM(noDealNum) from MatchOrder GROUP BY price ORDER BY price ASC";

		SqlFieldsQuery query = new SqlFieldsQuery(sql);
		QueryCursor<List<?>> cursor = cache.query(query);

		List<DepthDTO> rt = new ArrayList<>();
		for (List<?> row : cursor) {
			rt.add(new DepthDTO((Long) row.get(0), Long.parseLong(row.get(1).toString()), 0L));
		}
		return rt;
	}


    final BeanCopier beanCopier = BeanCopier.create(OrderDTO.class, MatchOrder.class, false);

//    public void putOrder(OrderDTO order) {
//        MatchOrder matchOrder = new MatchOrder();
//        beanCopier.copy(order, matchOrder, null);
//        if (passUnioueVerify(matchOrder)) {
//            DisruptorConfig.producer(matchOrder);
//        }
//    }
}
