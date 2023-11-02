/*
 * @project   V1.0
 * @filename:
 * Copyright(c) 2020 kinbug Co. Ltd.
 * All right reserved.
 */
package com.flying.cattle.me.data.input;

import java.util.Date;

import com.flying.cattle.me.plugin.mysql.MySQLUtil;
import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.configuration.CacheConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * @author senkyouku
 * @ClassName:
 * @Description: (这里用一句话描述这个类的作用)
 * @date
 */
@RestController
@RequestMapping("test")
public class TestController {


    @Autowired
    private MySQLUtil mySQLUtil;

    @GetMapping("/input/{key}")
    public String input(@PathVariable String key) {
//        IgniteCache<String, String> cache = ignite.cache("MatchOrder");
//        if (cache == null) {
//            CacheConfiguration<Long, OrderDTO> orderCache = new CacheConfiguration<>("MatchOrder");
//            orderCache.setIndexedTypes(String.class, OrderDTO.class);
//            ignite.getOrCreateCache(orderCache);
//        }
//
//        IgniteCache<String, String> cache2 = ignite.cache("MatchOrder");
//        cache2.put(key, key);
//        return cache2.get(key);
        return "";
    }

    @GetMapping("/get/{key}")
    public String get(@PathVariable String key) {
//        IgniteCache<String, String> cache = ignite.cache("MatchOrder");
//        return cache.get(key);
        return "";
    }

    @GetMapping("/input2/{key}")
    public String input2(@PathVariable Long key) {
//        IgniteCache<Long, MatchOrder> outOrderBook = igniteUtil.getIgniteOrderBook("1111");
//        MatchOrder order = new MatchOrder(key, 1, 1, 1L, 1, 1, true, 1, 1, 1, 0L,
//                1, 0L, 1, new Date(), null, 10);
//        outOrderBook.put(key, order);
//        return JSON.toJSONString(outOrderBook.get(key));
        return "";
    }

    @GetMapping("/get2/{key}")
    public String get2(@PathVariable Long key) {
//        IgniteCache<Long, MatchOrder> outOrderBook = igniteUtil.getIgniteOrderBook("1111");
//        return JSON.toJSONString(outOrderBook.get(key));
        return "";
    }
}
