package com.flying.cattle.dapr.plugin.dapr;

import com.alibaba.fastjson.JSON;
import com.flying.cattle.dapr.data.out.SendService;
import com.flying.cattle.dapr.match.domain.MatchOrder;
import com.flying.cattle.dapr.util.EngineUtil;
import com.flying.cattle.mt.enums.EnumOrderState;
import com.flying.cattle.mt.message.OrderDTO;
import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.beans.BeanCopier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @program: match-trade
 * @description: dapr 支持相关接口实现
 * @author: yuxiaobing
 * @mail：a17281293@gmail.com
 * @date: 2023-10-29 16:36
 **/
@Component
public class DaprUtil {

    @Autowired
    private DaprClient daprClient;

    private ConcurrentHashMap<String, String> cacheKeys;


    final SendService sendService;


    public DaprUtil(ConcurrentHashMap<String, String> cacheKeys, SendService sendService) {
        this.sendService = sendService;
        this.cacheKeys = cacheKeys;
    }
//    private
    /**
     * TODO 判断是否通过唯一性校验
     *
     * @param order 委托单
     * @return Boolean 返回类型（true：通过，false：为通过）
     */
    public Boolean passUnioueVerify(MatchOrder order) {
        // 获取表名
        String tableName = EngineUtil.getOrderDeWeigtKey(order);

        if (!this.idExist(order.getId(), tableName)) {
//            orderIds.put(order.getId(), order.getId());
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }



    public void createTable(String tableName){
        String sql = "CREATE TABLE `" + tableName + "` (" +
                "`id` bigint NOT NULL AUTO_INCREMENT," +
                "`accountId` bigint NOT NULL," +
                "`uid` bigint NOT NULL," +
                "`price` bigint DEFAULT NULL," +
                "`num` bigint NOT NULL," +
                "`amount` bigint NOT NULL," +
                "`ifBid` tinyint(1) NOT NULL," +
                "`orderType` int NOT NULL," +
                "`symbolId` int NOT NULL," +
                "`state` int NOT NULL," +
                "`dealNum` bigint NOT NULL," +
                "`noDealNum` bigint NOT NULL," +
                "`dealAmount` bigint NOT NULL," +
                "`noDealAmount` bigint NOT NULL," +
                "`createTime` datetime DEFAULT NULL," +
                "`alterTime` datetime DEFAULT NULL," +
                "`priority` int NOT NULL," +
                "PRIMARY KEY (`id`)" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci";
        try {
            daprClient.exec(sql);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }



    public boolean idExist(Long id, String tableName) {
        String sql = "SELECT COUNT(*) FROM " + tableName + " WHERE id = ?";
        Integer count = null;
        try {
            String invoke = daprClient.query(sql);

        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return count != null && count > 0;
    }

//    public MatchOrder getOrder(Long id){
//
//    }
    /**
     * TODO 获取对应币对的订单薄
     *
     * @param orderBookKey 委托单
     * @return Boolean 返回类型（true：通过，false：为通过）
     */
    public String getIgniteOrderBook(String orderBookKey) {
        if (cacheKeys.contains(orderBookKey)) {
            return cacheKeys.get(orderBookKey);
        } else {
            cacheKeys.put(orderBookKey, orderBookKey);
            this.createTable(orderBookKey );

            return cacheKeys.get(orderBookKey);
        }
    }

    /**
     * TODO 获取前100单
     *
     * @param ifBid 是否是买单
     * @param order 委托单
     * @return Boolean 返回类型（true：通过，false：为通过）
     */


    public List<Long> getOrderBookHead(String tableName, boolean ifBid, int limitNum) {
        String query;
        if (ifBid) {
            query = "SELECT id FROM " + tableName + " ORDER BY price DESC LIMIT "+limitNum;
        } else {
            query = "SELECT id FROM " + tableName + " ORDER BY price ASC LIMIT "+limitNum;
        }
        List<Long> ans = new ArrayList<>();
        try {
            String invoke = daprClient.query(query);
//            JSONArray jsonArray = JSON.parseArray(invoke);

        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return ans;
    }

    /**
     * TODO 获取前N单
     *
     * @param ifBid 是否是买单
     * @param order 委托单
     * @return Boolean 返回类型（true：通过，false：为通过）
     */

    public List<MatchOrder> getOrderBookHot(String tableName, boolean ifBid, int limitNum) {
        String orderBy = ifBid ? "DESC" : "ASC";
        String sql = "SELECT id, price FROM " + tableName + " ORDER BY price " + orderBy + " LIMIT " + limitNum;
        List<MatchOrder> ans = new ArrayList<>();
        try {
            String invoke = daprClient.query(sql);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return ans;
    }


    /**
     * TODO 执行撤销2
     *
     * @param order 委托单
     * @return Boolean 返回类型
     */
    @Async
    public void doCancelOrder(MatchOrder order) {

//        IgniteCache<Long, MatchOrder> map = ignite.cache(EngineUtil.getOrderBookKey(order));
        MatchOrder cancel = this.getAndRemove(order.getId(), EngineUtil.getOrderBookKey(order));
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
        String tableName = getIgniteOrderBook(orderBookKey);
        return this.insertOrUpdateOrder(order, tableName);

    }
    private MatchOrder insertOrUpdateOrder(MatchOrder order, String tableName) {
        String selectSql = "SELECT * FROM " + tableName + " WHERE id = " + order.getId();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            String invoke = daprClient.query(selectSql);
            if (invoke.equals("null")) {

                String formattedCreateTime = (order.getCreateTime() != null) ? "'" + sdf.format(order.getCreateTime()) + "'" : "NULL";
                String formattedAlterTime = (order.getAlterTime() != null) ? "'" + sdf.format(order.getAlterTime()) + "'" : "NULL";



                String insertSql = "INSERT INTO " + tableName + " " +
                        "(id, accountId, uid, price, num, amount, ifBid, orderType, symbolId, state, dealNum, noDealNum, dealAmount, noDealAmount, createTime, alterTime, priority) " +
                        "VALUES (" +
                        order.getId() + ", " +
                        order.getAccountId() + ", " +
                        order.getUid() + ", " +
                        order.getPrice() + ", " +
                        order.getNum() + ", " +
                        order.getAmount() + ", " +
                        order.isIfBid() + ", " +
                        order.getOrderType() + ", " +
                        order.getSymbolId() + ", " +
                        order.getState() + ", " +
                        order.getDealNum() + ", " +
                        order.getNoDealNum() + ", " +
                        order.getDealAmount() + ", " +
                        order.getNoDealAmount() + ", " +
                        formattedCreateTime + ", " +
                        formattedAlterTime + ", " +
                        order.getPriority() + ")";

                daprClient.exec(insertSql);
                return order;
            }else{
                String formattedCreateTime = (order.getCreateTime() != null) ? "'" + sdf.format(order.getCreateTime()) + "'" : "NULL";
                String formattedAlterTime = (order.getAlterTime() != null) ? "'" + sdf.format(order.getAlterTime()) + "'" : "NULL";
                // If oldOrder is not null, then update the existing order.
                String updateSql = "UPDATE " + tableName + " SET " +
                        "accountId = " + order.getAccountId() + ", " +
                        "uid = " + order.getUid() + ", " +
                        "price = " + order.getPrice() + ", " +
                        "num = " + order.getNum() + ", " +
                        "amount = " + order.getAmount() + ", " +
                        "ifBid = " + order.isIfBid() + ", " +
                        "orderType = " + order.getOrderType() + ", " +
                        "symbolId = " + order.getSymbolId() + ", " +
                        "state = " + order.getState() + ", " +
                        "dealNum = " + order.getDealNum() + ", " +
                        "noDealNum = " + order.getNoDealNum() + ", " +
                        "dealAmount = " + order.getDealAmount() + ", " +
                        "noDealAmount = " + order.getNoDealAmount() + ", " +
                        "createTime = " + formattedCreateTime + ", " +
                        "alterTime = " + formattedAlterTime + ", " +
                        "priority = " + order.getPriority() + " " +
                        "WHERE id = " + order.getId();
                daprClient.exec(updateSql);
                MatchOrder oldOrder = new MatchOrder();
                return oldOrder;
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }



    private MatchOrder getAndRemove(Long id, String tableName) {
        String selectSql = "SELECT * FROM " + tableName + " WHERE id = "+id;
        MatchOrder ans= new MatchOrder();
        MatchOrder oldOrder = new MatchOrder();
        try {

            String invoke = daprClient.query(selectSql);
//            MatchOrder oldOrder = jdbcTemplate.queryForObject(selectSql, new BeanPropertyRowMapper<>(MatchOrder.class), id);

            // If oldOrder is not null, then delete the order from the table.
            if (oldOrder != null) {

                String deleteSql = "DELETE FROM " + tableName + " WHERE id = "+id;
                daprClient.exec(deleteSql);
//                jdbcTemplate.update(deleteSql, id);
            }

            return ans;
        } catch (EmptyResultDataAccessException e) {
            // This exception is thrown if the result set is empty, i.e., the order doesn't exist.
            return null;
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * 获取币对下
     *
     * @param symbol 币对标识
     * @param ifBid  买卖标识
     * @return List<OrderDTO>
     * @author senkyouku
     */
    public List<MatchOrder> listAll(int symbolId, boolean ifBid) {
        List<MatchOrder> ans= new ArrayList<>();
        String sql = "SELECT * FROM MatchOrder WHERE symbolId = "+symbolId+" AND ifBid = "+ifBid;
        try {
            String invoke = daprClient.query(sql);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
//        List<MatchOrder> result = jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(MatchOrder.class), symbolId, ifBid);
        return ans;
    }




    final BeanCopier beanCopier = BeanCopier.create(OrderDTO.class, MatchOrder.class, false);
    public MatchOrder get(Long id, String tableName) {
        String sql = "SELECT * FROM "+tableName+" WHERE id = "+id;
        try {
            String invoke = daprClient.query(sql);
//            MatchOrder order = jdbcTemplate.queryForObject(sql, new BeanPropertyRowMapper<>(MatchOrder.class), id);
            MatchOrder order = new MatchOrder();
            return order;
        } catch (EmptyResultDataAccessException | IOException | InterruptedException e) {
            // 这个异常会在查询结果为空时抛出，即没有找到匹配的订单
            return null;
        }
    }
    public MatchOrder updateOrderInDB(MatchOrder maker, String tableName) {
        String selectSql = "SELECT * FROM " + tableName + " WHERE id = " + maker.getId();
        try {
            String invoke = daprClient.query(selectSql);
            MatchOrder order = new MatchOrder();
            if (order != null) {
                order.setState(maker.getState());
                order.setDealNum(maker.getDealNum());
                order.setNoDealNum(maker.getNoDealNum());
                order.setDealAmount(maker.getDealAmount());
                order.setNoDealAmount(maker.getNoDealAmount());

                String updateSql = "UPDATE " + tableName + " SET " +
                        "state = " + maker.getState() + ", " +
                        "dealNum = " + maker.getDealNum() + ", " +
                        "noDealNum = " + maker.getNoDealNum() + ", " +
                        "dealAmount = " + maker.getDealAmount() + ", " +
                        "noDealAmount = " + maker.getNoDealAmount() + " " +
                        "WHERE id = " + maker.getId();
                daprClient.exec(updateSql);

                if (maker.getState() == EnumOrderState.ALL_DEAL.getCode()) {
                    String deleteSql = "DELETE FROM " + tableName + " WHERE id = " + maker.getId();
                    daprClient.exec(deleteSql);
                }
            }
            return order;
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }


}
