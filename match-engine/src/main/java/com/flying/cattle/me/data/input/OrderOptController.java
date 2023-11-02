/*
 * @project   V1.0
 * @filename:
 * Copyright(c) 2020 kinbug Co. Ltd.
 * All right reserved.
 */
package com.flying.cattle.me.data.input;

import com.flying.cattle.me.plugin.mysql.MySQLUtil;
import com.flying.cattle.mt.enums.EnumOrderType;
import com.flying.cattle.me.match.EngineExecutor;
import com.flying.cattle.me.match.domain.MatchOrder;
import com.flying.cattle.me.match.factory.MatchStrategyFactory;
import com.flying.cattle.me.match.service.AbstractOrderMatchService;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.Date;

/**
 * @author senkyouku
 * @ClassName: OrderTestController
 * @Description: 订单操作测试，仅测试环境及开发环境使用
 * @date r2020-07-20
 */
@RestController
@RequestMapping("order")
@Slf4j
public class OrderOptController {

    @Autowired
    MySQLUtil mySQLUtil;
    @Autowired
    EngineExecutor matchExecutors;
    @Autowired
    private AbstractOrderMatchService gtcMatchService;

//    @GetMapping("/newOrder")
//    public void newOrder(@Valid OrderDTO order) {
//        if (EnumOrderType.MTC.getCode() == (order.getOrderType())) {
//            order.setNoDealAmount(order.getAmount());
//            order.setDealAmount(0L);
//            order.setNoDealNum(0L);
//            order.setDealNum(0L);
//            log.info("order send start {} ", order.toString());
//            igniteUtil.putOrder(order);
//            log.info("推送撮合成功.....");
//        }else{
//            order.setNoDealAmount(order.getPrice() * order.getNum());
//            order.setAmount(order.getPrice() * order.getNum());
//            order.setDealAmount(0L);
//            order.setNoDealNum(order.getNum());
//            order.setDealNum(0L);
//            log.info("order send start {} " , order.toString());
//            igniteUtil.putOrder(order);
//            log.info("推送撮合成功.....");
//        }
//    }


    @GetMapping("/input/{symbol}/{orderNum}/{orderType}/{max}/{min}")
    public String input(@PathVariable("symbol") int symbol,
                        @PathVariable("orderNum") int orderNum,
                        @PathVariable("orderType") int orderType,
                        @PathVariable("max") int max,
                        @PathVariable("min") int min) {
        Long startTime = System.currentTimeMillis();
        for (long i = 0; i < orderNum; i++) {
            MatchOrder bidOrder = this.builtOrder(i, true, symbol, max, min, orderType);
            MatchOrder askOrder = this.builtOrder(i, false, symbol, max, min, orderType);
            mySQLUtil.addToOrderBook(bidOrder);
            mySQLUtil.addToOrderBook(askOrder);
        }
        Long endTime = System.currentTimeMillis();
        log.info("初始,数量:{},耗时:{}", orderNum, endTime - startTime);
        return "初始,数量:"+orderNum+",耗时:"+(endTime - startTime);
    }

    @GetMapping("/eat-order/{symbol}/{num}/{startNum}/{orderType}/{max}/{min}")
    public String eat(@PathVariable("symbol") int symbol,
                      @PathVariable("num") int num,
                      @PathVariable("startNum") int startNum,
                      @PathVariable("orderType") int orderType,
                      @PathVariable("max") int max,
                      @PathVariable("min") int min) {
        long startTime = System.currentTimeMillis();
        Long[] latencies = new Long[num];
        for (int i = 0; i < num; i++, startNum++) {
            long startTime1 = System.currentTimeMillis();
            MatchOrder matchOrder = this.builtOrder(startNum, true, symbol, max, min, orderType);
            // 根据订单类型获取撮合策略
            AbstractOrderMatchService service = MatchStrategyFactory.getByOrderType(EnumOrderType.of(matchOrder.getOrderType()));
            // 开始撮合
            service.match(matchOrder);
            long endTime1 = System.currentTimeMillis();
            latencies[i] = endTime1 - startTime1;
        }
        Arrays.sort(latencies);
        long endTime = System.currentTimeMillis();
        long totalTime = endTime - startTime;
        double rps = num / (totalTime / 1000.0); // 转换为秒
        // 计算平均耗时
        double avgLatency = Arrays.stream(latencies).mapToLong(Long::longValue).average().orElse(0);
        // 获取P90, P95, P99和中位数耗时
        long p90Latency = latencies[(int) Math.ceil(90.0 / 100 * num) - 1];
        long p95Latency = latencies[(int) Math.ceil(95.0 / 100 * num) - 1];
        long p99Latency = latencies[(int) Math.ceil(99.0 / 100 * num) - 1];
        long medianLatency = latencies[num / 2];
        // 获取最大和最小耗时
        long maxLatency = latencies[num - 1];
        long minLatency = latencies[0];
        // 构建返回字符串
        String result = String.format("撮合数量: %d, 总耗时: %d ms, RPS: %.2f, 平均耗时: %.2f ms, 中位数耗时: %d ms, P90耗时: %d ms, P95耗时: %d ms, P99耗时: %d ms, 最大耗时：%d ms, 最小耗时：%d ms.",
                num, totalTime, rps, avgLatency, medianLatency, p90Latency, p95Latency, p99Latency, maxLatency, minLatency);
        log.info(result);
        return result;
    }



    @GetMapping("/ask/{ifBid}/{num}/{price}/{startNum}/{size}")
    public String ask(@PathVariable("ifBid") int ifBid,
    				  @PathVariable("num") int num,
                      @PathVariable("price") int price,
                      @PathVariable("startNum") int startNum,
                      @PathVariable("size") int size) {
        Long startTime = System.currentTimeMillis();
        for (long i = 0; i < size; i++, startNum++) {
        	boolean isbid = ifBid>0 ? true:false;
        	long number = num;
        	long price1 = price;
        	MatchOrder matchOrder = new MatchOrder(startNum, startNum, startNum, price1, number, number*price1, isbid, 1, 10, 1, 0L,
        			num, 0L, num*price, new Date(), null, 10);
            // 根据订单类型获取撮合策略
            AbstractOrderMatchService service = MatchStrategyFactory.getByOrderType(EnumOrderType.of(matchOrder.getOrderType()));
            // 开始撮合
            service.match(matchOrder);
        }
        Long endTime = System.currentTimeMillis();
        log.info("撮合,数量:{},耗时:{}", num, endTime - startTime);
        return "耗时:" + (endTime - startTime);
    }
    @GetMapping("/test")
    public String test() {
        Long startTime = System.currentTimeMillis();

        Long endTime = System.currentTimeMillis();
        log.info("撮合,数量:{},耗时:{}", 1, endTime - startTime);
        return "耗时:" + (endTime - startTime);
    }

    /**
     * TODO 创建订单信息
     */
    private MatchOrder builtOrder(long counter, boolean ifBid, int symbol, int max, int min,int orderType) {
        long ran = (long) (Math.random() * (max - min) + min);
        long number = (long) (Math.random()*99 + 1);

        long amount = ran * number;
        return new MatchOrder(counter, counter, counter, ran, number, amount, ifBid, orderType, symbol, 1, 0L,
                number, 0L, amount, new Date(), null, 10);
    }
    
}
