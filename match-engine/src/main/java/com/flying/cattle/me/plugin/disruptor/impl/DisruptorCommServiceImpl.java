package com.flying.cattle.me.plugin.disruptor.impl;


import com.flying.cattle.me.plugin.disruptor.DisruptorInsertService;
import com.flying.cattle.me.plugin.disruptor.handler.DisruptorEventCommHandler;
import com.flying.cattle.me.plugin.disruptor.handler.DisruptorMatchHandler;
import com.flying.cattle.me.plugin.mysql.MySQLUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DisruptorCommServiceImpl extends DisruptorInsertService {
    @Autowired
    private MySQLUtil mySQLUtil;
    @Override
    protected void handleEvents() {
        /**
         * 调用handleEventsWithWorkerPool，表示创建的多个消费者以共同消费的模式消费；
         * 单个消费者时可保证其有序性，多个时无法保证其顺序；
         * 或者说每个消费者是有序的，但每个消费者间是并行执行的，所以无法保证整体的有序
         * 共同消费者做的应该是同个事，所以本例中只定义了一个共同消费者DisruptorEventCommHandler
         */
        DisruptorEventCommHandler disruptorMatchHandlerA = new DisruptorEventCommHandler("A", mySQLUtil);
        DisruptorEventCommHandler disruptorMatchHandlerB = new DisruptorEventCommHandler("B", mySQLUtil);
        DisruptorEventCommHandler disruptorMatchHandlerC = new DisruptorEventCommHandler("C", mySQLUtil);
        DisruptorEventCommHandler disruptorMatchHandlerD = new DisruptorEventCommHandler("D", mySQLUtil);
        disruptor.handleEventsWithWorkerPool(disruptorMatchHandlerA,disruptorMatchHandlerB,disruptorMatchHandlerC,disruptorMatchHandlerD);
    }

}
