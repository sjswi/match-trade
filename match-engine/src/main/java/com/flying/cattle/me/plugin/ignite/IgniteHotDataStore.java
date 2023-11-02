///*
// * @project   V1.0
// * @filename:
// * Copyright(c) 2020 kinbug Co. Ltd.
// * All right reserved.
// */
//package com.flying.cattle.me.plugin.ignite;
//
//import com.flying.cattle.me.match.domain.MatchOrder;
//import com.flying.cattle.me.util.EngineUtil;
//import org.apache.ignite.IgniteCache;
//import org.springframework.stereotype.Component;
//import org.springframework.util.CollectionUtils;
//
//import java.util.List;
//import java.util.TreeMap;
//import java.util.concurrent.ConcurrentHashMap;
//
///**
// * @author senkyouku
// * @ClassName:
// * @Description: ignite 热点数据, treeMap 与 ignite 结合
// * @date
// */
//@Component
//public class IgniteHotDataStore {
//
//    private final IgniteUtil igniteUtil;
//
//    /**
//     * 热点数据
//     */
//    private final        ConcurrentHashMap<String, TreeMap<Long, TreeMap<Long, Long>>> hotSpot;
//    /**
//     * 订单簿热点数据大小
//     */
//    private final static int                                                           HOT_ORDER_NUM = 10000;
//
//    public IgniteHotDataStore(IgniteUtil igniteUtil) {
//        this.igniteUtil = igniteUtil;
//        this.hotSpot = new ConcurrentHashMap<>();
//    }
//
//    /**
//     * 获取对手-热点数据订单簿最优订单ID
//     *
//     * @param symbol 交易币对
//     * @param ifBid  买卖标识
//     * @return 热点订单ID
//     */
//    public Long getRivalHotOrderBook(long symbol, boolean ifBid) {
//        String rSymbolKey = EngineUtil.getOrderBookKey(symbol, !ifBid);
//        TreeMap<Long, TreeMap<Long, Long>> rOrderBook = hotSpot.get(rSymbolKey);
//        if (CollectionUtils.isEmpty(rOrderBook)) {
//            // 初始化对手盘热点数据
//            rOrderBook = this.loadHotOrderBook(symbol, !ifBid);
//        }
//        if (CollectionUtils.isEmpty(rOrderBook)) {
//            // 对手盘无数据，获取最优订单为空
//            return null;
//        }
//        return !ifBid ? rOrderBook.firstEntry().getValue().firstKey() : rOrderBook.lastEntry().getValue().lastKey();
//    }
//
//    /**
//     * 添加订单
//     *
//     * @param order 交易订单
//     * @return 待添加订单
//     */
//    public boolean addOrder(MatchOrder order) {
//        // 添加ignite数据
//        String symbolKey = EngineUtil.getOrderBookKey(order.getSymbolId(), order.isIfBid());
//        IgniteCache<Long, MatchOrder> igOrderBook = igniteUtil.getIgniteOrderBook(symbolKey);
//        igOrderBook.put(order.getId(), order);
//        // 缓存中添加热点订单数据
//        return this.addOrderHotData(order);
//    }
//
//    /**
//     * 缓存中添加热点订单数据
//     *
//     * @param order 交易订单
//     * @return 待添加订单
//     */
//    private boolean addOrderHotData(MatchOrder order) {
//        // 添加热点数据
//        String symbolKey = EngineUtil.getOrderBookKey(order.getSymbolId(), order.isIfBid());
//        TreeMap<Long, TreeMap<Long, Long>> orderBook = hotSpot.get(symbolKey);
//        if (CollectionUtils.isEmpty(orderBook)) {
//            // 种子数据，第一次添加数据的price的大小，将决定缓存数据的多少，卖盘第一条订单价格正无穷时或买盘第一条订单价格0时，缓存对应盘口的全量ignite数据
//            TreeMap<Long, Long> inOrderMap = new TreeMap<>();
//            inOrderMap.put(order.getId(), order.getId());
//            orderBook.put(order.getPrice(), inOrderMap);
//            return true;
//        }
//        boolean bidAddCdt = order.isIfBid() && order.getPrice() > orderBook.firstKey();
//        boolean askAddCdt = !order.isIfBid() && order.getPrice() < orderBook.lastKey();
//        if (bidAddCdt || askAddCdt) {
//            // 当前订单大于买盘最差匹配订单时插入热点订单簿,当前订单小于卖盘最差匹配订单价格时插入热点订单簿
//            if (!orderBook.containsKey(order.getPrice())) {
//                TreeMap<Long, Long> inOrderMap = new TreeMap<>();
//                inOrderMap.put(order.getId(), order.getId());
//                orderBook.put(order.getPrice(), inOrderMap);
//            } else {
//                // 强制添加热点数据，若热点数据存在，做覆盖
//                TreeMap<Long, Long> inOrderMap = orderBook.get(order.getPrice());
//                inOrderMap.put(order.getId(), order.getId());
//            }
//        }
//        return true;
//    }
//
//
//    /**
//     * 删除订单
//     *
//     * @param order 交易订单
//     * @return 待删除订单
//     */
//    public MatchOrder removeOrder(MatchOrder order) {
//        // 删除ignite数据
//        String symbolKey = EngineUtil.getOrderBookKey(order.getSymbolId(), order.isIfBid());
//        IgniteCache<Long, MatchOrder> igOrderBook = igniteUtil.getIgniteOrderBook(symbolKey);
//        MatchOrder orderRt = igOrderBook.getAndRemove(order.getId());
//        // 删除热点订单
//        TreeMap<Long, TreeMap<Long, Long>> orderBook = hotSpot.get(symbolKey);
//        if (!orderBook.containsKey(order.getPrice())) {
//            return order;
//        }
//        TreeMap<Long, Long> inOrderMap = orderBook.get(order.getPrice());
//        inOrderMap.remove(order.getId());
//        return orderRt;
//    }
//
//    /**
//     * 初始化热点数据
//     *
//     * @param symbol 订单簿key
//     * @param ifBid  买卖盘标识
//     * @return 热点订单簿
//     */
//    private TreeMap<Long, TreeMap<Long, Long>> loadHotOrderBook(long symbol, boolean ifBid) {
//        // 热点订单簿
//        String symbolKey = EngineUtil.getOrderBookKey(symbol, ifBid);
//        TreeMap<Long, TreeMap<Long, Long>> orderBook = hotSpot.computeIfAbsent(symbolKey, k -> new TreeMap<>());
//        // 初始化热点数据
//        IgniteCache<Long, MatchOrder> igOrderBook = igniteUtil.getIgniteOrderBook(symbolKey);
//        List<MatchOrder> list = igniteUtil.getOrderBookHot(igOrderBook, ifBid, HOT_ORDER_NUM);
//        if (CollectionUtils.isEmpty(list)) {
//            // 盘口无数据
//            return orderBook;
//        }
//
//        // 设置缓存数据结构
//        for (MatchOrder order : list) {
//            if (!orderBook.containsKey(order.getPrice())) {
//                TreeMap<Long, Long> inOrderMap = new TreeMap<>();
//                inOrderMap.put(order.getId(), order.getId());
//                orderBook.put(order.getPrice(), inOrderMap);
//            } else {
//                TreeMap<Long, Long> inOrderMap = orderBook.get(order.getPrice());
//                if (!inOrderMap.containsKey(order.getId())) {
//                    // 热点数据订单簿中含有的订单，不做覆盖
//                    inOrderMap.put(order.getId(), order.getId());
//                }
//            }
//        }
//        return orderBook;
//    }
//
//}
