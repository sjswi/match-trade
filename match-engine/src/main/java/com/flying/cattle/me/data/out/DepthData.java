/**
 * @project walk-exchange-match  V1.0
 * @filename: DepthData.java 2020年7月10日
 * Copyright(c) 2020 kinbug Co. Ltd. 
 * All right reserved. 
 */
package com.flying.cattle.me.data.out;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.flying.cattle.me.plugin.mysql.MySQLUtil;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.cache.query.QueryCursor;
import org.apache.ignite.cache.query.ScanQuery;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;



import javax.cache.Cache;

/**
 * @ClassName: DepthData
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author kinbug
 * @date 2020年7月10日
 */
@RequestMapping("/depth")
@RestController
@Profile("local")
public class DepthData {

	final MySQLUtil mySQLUtil;

	public DepthData(MySQLUtil mySQLUtil) {
		this.mySQLUtil = mySQLUtil;
	}

	@GetMapping("/symbol/{symbol}")
	public Object addNewOrders(@PathVariable("symbol") int symbol) {
//		IgniteCache<Long, MatchOrder> bid = mySQLUtil.getIgniteOrderBook("BOOK-BID-" + symbol);
//		IgniteCache<Long, MatchOrder> ask = mySQLUtil.getIgniteOrderBook("BOOK-ASK-" + symbol);
//		Map<String, Object> map = new HashMap<String, Object>();
//
//        QueryCursor<Cache.Entry<Long, MatchOrder>> cursorBid = bid.query(new ScanQuery<>());
//        List<Cache.Entry<Long, MatchOrder>> bidList = cursorBid.getAll();
//
//        QueryCursor<Cache.Entry<Long, MatchOrder>> cursorAsk = ask.query(new ScanQuery<>());
//        List<Cache.Entry<Long, MatchOrder>> askList = cursorAsk.getAll();
//		map.put("bid", bidList.stream().map(Cache.Entry::getValue).collect(Collectors.toList()));
//		map.put("ask", askList.stream().map(Cache.Entry::getValue).collect(Collectors.toList()));
//		return map;
		return null;
	}

}
