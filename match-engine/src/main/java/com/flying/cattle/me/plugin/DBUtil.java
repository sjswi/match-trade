package com.flying.cattle.me.plugin;

import com.flying.cattle.me.match.domain.MatchOrder;
import com.flying.cattle.me.util.EngineUtil;
import com.flying.cattle.mt.enums.EnumOrderState;
import com.flying.cattle.mt.message.OrderDTO;
import lombok.Synchronized;
import org.springframework.cglib.beans.BeanCopier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.List;

/**
 * @program: match-trade
 * @description:
 * @author: yuxiaobing
 * @mail：a17281293@gmail.com
 * @date: 2023-11-09 18:21
 **/
public interface DBUtil {
    /**
     * TODO 判断是否通过唯一性校验
     *
     * @param order 委托单
     * @return Boolean 返回类型（true：通过，false：为通过）
     */
    Boolean passUniqueVerify(MatchOrder order);




//    public MatchOrder getOrder(Long id){
//
//    }
    /**
     * TODO 获取对应币对的订单薄
     *
     * @param orderBookKey 委托单
     * @return Boolean 返回类型（true：通过，false：为通过）
     */
    String getIgniteOrderBook(String orderBookKey);

    /**
     * TODO 获取前100单
     *
     * @param ifBid 是否是买单
     * @param order 委托单
     * @return Boolean 返回类型（true：通过，false：为通过）
     */


    List<Long> getOrderBookHead(String tableName, boolean ifBid, int limitNum);

    /**
     * TODO 获取前N单
     *
     * @param ifBid 是否是买单
     * @param order 委托单
     * @return Boolean 返回类型（true：通过，false：为通过）
     */

    List<MatchOrder> getOrderBookHot(String tableName, boolean ifBid, int limitNum);


    /**
     * TODO 执行撤销2
     *
     * @param order 委托单
     * @return Boolean 返回类型
     */
    @Async
    void doCancelOrder(MatchOrder order);

    /**
     * TODO 发送撤销2
     *
     * @param order 委托单
     * @return Boolean 返回类型
     */
    @Async
    MatchOrder sendCancelOrder(MatchOrder order);

    /**
     * TODO 添加到对应的订单薄
     *
     * @param order 委托单
     * @return Boolean 返回类型（true：通过，false：为通过）
     */
    MatchOrder addToOrderBook(MatchOrder order);






    /**
     * 获取币对下
     *
     * @param symbol 币对标识
     * @param ifBid  买卖标识
     * @return List<OrderDTO>
     * @author senkyouku
     */
    List<MatchOrder> listAll(int symbolId, boolean ifBid);




    MatchOrder get(Long id, String tableName);
    MatchOrder updateOrderInDB(MatchOrder maker, String tableName);
    List<MatchOrder> getOrders(int symbol, int orderType, boolean ifBid, long min, long max, long number);
}
