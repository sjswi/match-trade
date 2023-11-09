package com.flying.cattle.me.plugin.mysql;

import com.flying.cattle.me.data.out.SendService;
import com.flying.cattle.me.match.domain.MatchOrder;
import com.flying.cattle.me.plugin.DBUtil;
import com.flying.cattle.me.util.EngineUtil;
import com.flying.cattle.mt.enums.EnumOrderState;
import com.flying.cattle.mt.message.DepthDTO;
import com.flying.cattle.mt.message.OrderDTO;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.cache.CacheAtomicityMode;
import org.apache.ignite.cache.QueryEntity;
import org.apache.ignite.cache.QueryIndex;
import org.apache.ignite.cache.query.QueryCursor;
import org.apache.ignite.cache.query.ScanQuery;
import org.apache.ignite.cache.query.SqlFieldsQuery;
import org.apache.ignite.configuration.CacheConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.beans.BeanCopier;
import org.springframework.context.annotation.Bean;
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

import javax.cache.Cache;
import javax.cache.expiry.CreatedExpiryPolicy;
import javax.cache.expiry.Duration;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @program: match-trade
 * @description: dapr 支持相关接口实现
 * @author: yuxiaobing
 * @mail：a17281293@gmail.com
 * @date: 2023-10-29 16:36
 **/
@Component("MySQLUtil")
public class MySQLUtil implements DBUtil {

    @Autowired
    private PlatformTransactionManager transactionManager;
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private ConcurrentHashMap<String, String> cacheKeys;


    final SendService sendService;

    public MySQLUtil(ConcurrentHashMap<String, String> cacheKeys, SendService sendService) {
        this.cacheKeys = cacheKeys;
        this.sendService = sendService;
    }

//    private
    /**
     * TODO 判断是否通过唯一性校验
     *
     * @param order 委托单
     * @return Boolean 返回类型（true：通过，false：为通过）
     */
    @Override
    public synchronized Boolean passUniqueVerify(MatchOrder order) {
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
        jdbcTemplate.execute(sql);
    }



    public boolean idExist(Long id, String tableName) {
        String sql = "SELECT COUNT(*) FROM " + tableName + " WHERE id = ?";
        Integer count = jdbcTemplate.queryForObject(sql, new Object[]{id}, Integer.class);
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
    @Override
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

    @Override
    public List<Long> getOrderBookHead(String tableName, boolean ifBid, int limitNum) {
        String query;
        if (ifBid) {
            query = "SELECT id FROM " + tableName + " ORDER BY price DESC LIMIT ?";
        } else {
            query = "SELECT id FROM " + tableName + " ORDER BY price ASC LIMIT ?";
        }
        return jdbcTemplate.queryForList(query, Long.class, limitNum);
    }

    /**
     * TODO 获取前N单
     *
     * @param ifBid 是否是买单
     * @param order 委托单
     * @return Boolean 返回类型（true：通过，false：为通过）
     */
    @Override
    public List<MatchOrder> getOrderBookHot(String tableName, boolean ifBid, int limitNum) {
        String orderBy = ifBid ? "DESC" : "ASC";
        String sql = "SELECT id, price FROM " + tableName + " ORDER BY price " + orderBy + " LIMIT " + limitNum;

        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(MatchOrder.class));
    }


    /**
     * TODO 执行撤销2
     *
     * @param order 委托单
     * @return Boolean 返回类型
     */
    @Override
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
    @Override
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
    @Override
    public MatchOrder addToOrderBook(MatchOrder order) {
        String orderBookKey = EngineUtil.getOrderBookKey(order);
        String tableName = getIgniteOrderBook(orderBookKey);
        return this.insertOrUpdateOrder(order, tableName);

    }
    private MatchOrder insertOrUpdateOrder(MatchOrder order, String tableName) {
        String selectSql = "SELECT * FROM " + tableName + " WHERE id = ?";
        try {
            MatchOrder oldOrder = jdbcTemplate.queryForObject(selectSql, new BeanPropertyRowMapper<>(MatchOrder.class), order.getId());

            // If oldOrder is not null, then update the existing order.
            String updateSql = "UPDATE " + tableName + " SET " +
                    "accountId = ?, " +
                    "uid = ?, " +
                    "price = ?, " +
                    "num = ?, " +
                    "amount = ?, " +
                    "ifBid = ?, " +
                    "orderType = ?, " +
                    "symbolId = ?, " +
                    "state = ?, " +
                    "dealNum = ?, " +
                    "noDealNum = ?, " +
                    "dealAmount = ?, " +
                    "noDealAmount = ?, " +
                    "createTime = ?, " +
                    "alterTime = ?, " +
                    "priority = ? " +
                    "WHERE id = ?";
            jdbcTemplate.update(updateSql,
                    order.getAccountId(),
                    order.getUid(),
                    order.getPrice(),
                    order.getNum(),
                    order.getAmount(),
                    order.isIfBid(),
                    order.getOrderType(),
                    order.getSymbolId(),
                    order.getState(),
                    order.getDealNum(),
                    order.getNoDealNum(),
                    order.getDealAmount(),
                    order.getNoDealAmount(),
                    order.getCreateTime(),
                    order.getAlterTime(),
                    order.getPriority(),
                    order.getId()
            );

            return oldOrder;
        } catch (EmptyResultDataAccessException e) {
            // This exception is thrown if the result set is empty, i.e., the order doesn't exist.
            // Insert the new order.
            String insertSql = "INSERT INTO " + tableName + " " +
                    "(id, accountId, uid, price, num, amount, ifBid, orderType, symbolId, state, dealNum, noDealNum, dealAmount, noDealAmount, createTime, alterTime, priority) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            jdbcTemplate.update(insertSql,
                    order.getId(),
                    order.getAccountId(),
                    order.getUid(),
                    order.getPrice(),
                    order.getNum(),
                    order.getAmount(),
                    order.isIfBid(),
                    order.getOrderType(),
                    order.getSymbolId(),
                    order.getState(),
                    order.getDealNum(),
                    order.getNoDealNum(),
                    order.getDealAmount(),
                    order.getNoDealAmount(),
                    order.getCreateTime(),
                    order.getAlterTime(),
                    order.getPriority()
            );
            return null;
        }
    }


    private MatchOrder getAndRemove(Long id, String tableName) {
        String selectSql = "SELECT * FROM " + tableName + " WHERE id = ?";
        try {
            MatchOrder oldOrder = jdbcTemplate.queryForObject(selectSql, new BeanPropertyRowMapper<>(MatchOrder.class), id);

            // If oldOrder is not null, then delete the order from the table.
            if (oldOrder != null) {
                String deleteSql = "DELETE FROM " + tableName + " WHERE id = ?";
                jdbcTemplate.update(deleteSql, id);
            }

            return oldOrder;
        } catch (EmptyResultDataAccessException e) {
            // This exception is thrown if the result set is empty, i.e., the order doesn't exist.
            return null;
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
    @Override
    public List<MatchOrder> listAll(int symbolId, boolean ifBid) {
        String sql = "SELECT * FROM MatchOrder WHERE symbolId = ? AND ifBid = ?";
        List<MatchOrder> result = jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(MatchOrder.class), symbolId, ifBid);
        return result;
    }




    final BeanCopier beanCopier = BeanCopier.create(OrderDTO.class, MatchOrder.class, false);
    public MatchOrder get(Long id, String tableName) {
        String sql = "SELECT * FROM "+tableName+" WHERE id = ?";
        try {
            MatchOrder order = jdbcTemplate.queryForObject(sql, new BeanPropertyRowMapper<>(MatchOrder.class), id);
            return order;
        } catch (EmptyResultDataAccessException e) {
            // 这个异常会在查询结果为空时抛出，即没有找到匹配的订单
            return null;
        }
    }
    @Override
    public MatchOrder updateOrderInDB(MatchOrder maker, String tableName) {
        TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);
        transactionTemplate.setIsolationLevel(TransactionDefinition.ISOLATION_SERIALIZABLE);

        return transactionTemplate.execute(new TransactionCallback<MatchOrder>() {
            @Override
            public MatchOrder doInTransaction(TransactionStatus status) {
                String selectSql = "SELECT * FROM "+tableName+" WHERE id = ?";
                MatchOrder order = jdbcTemplate.queryForObject(selectSql, new BeanPropertyRowMapper<>(MatchOrder.class), maker.getId());

                if (order != null) {
                    order.setState(maker.getState());
                    order.setDealNum(maker.getDealNum());
                    order.setNoDealNum(maker.getNoDealNum());
                    order.setDealAmount(maker.getDealAmount());
                    order.setNoDealAmount(maker.getNoDealAmount());

                    String updateSql = "UPDATE "+tableName+" SET state = ?, dealNum = ?, noDealNum = ?, dealAmount = ?, noDealAmount = ? WHERE id = ?";
                    jdbcTemplate.update(updateSql, order.getState(), order.getDealNum(), order.getNoDealNum(), order.getDealAmount(), order.getNoDealAmount(), order.getId());

                    if (maker.getState() == EnumOrderState.ALL_DEAL.getCode()) {
                        String deleteSql = "DELETE FROM "+tableName+" WHERE id = ?";
                        jdbcTemplate.update(deleteSql, order.getId());
                    }
                }
                return order;
            }
        });
    }
    @Override
    public List<MatchOrder> getOrders(int symbol, int orderType, boolean ifBid, long min, long max, long number){
        String table = EngineUtil.getOrderTable(ifBid, symbol);
        String sql = "SELECT * FROM " + table + " WHERE orderType = " + orderType + " AND price >= " + min + " AND price <= " + max + " ORDER BY price ASC LIMIT " + number;
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(MatchOrder.class));
    }

}
