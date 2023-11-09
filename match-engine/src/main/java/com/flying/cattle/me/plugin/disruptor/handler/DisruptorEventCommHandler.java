package com.flying.cattle.me.plugin.disruptor.handler;

import com.flying.cattle.me.match.domain.MatchOrder;
import com.flying.cattle.me.plugin.mysql.MySQLUtil;
import com.lmax.disruptor.WorkHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * 共同消费者
 */
@Slf4j
public class DisruptorEventCommHandler implements WorkHandler<MatchOrder> {
    private String name;

    private MySQLUtil mySQLUtil;

    public DisruptorEventCommHandler(String name, MySQLUtil mySQLUtil) {
        this.name = name;
        this.mySQLUtil = mySQLUtil;
    }


    @Override
    public void onEvent(MatchOrder disruptorEvent) throws Exception {
        //模拟事件处理时间
        long s = System.currentTimeMillis();
        mySQLUtil.addToOrderBook(disruptorEvent);
        long e = System.currentTimeMillis();
        log.info("共同消费者{} :插入一条数据，用时{}", name, e-s);
    }
}
