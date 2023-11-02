package com.flying.cattle.me.plugin.dapr;

import com.flying.cattle.me.match.domain.MatchOrder;
import com.flying.cattle.me.util.EngineUtil;
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
import org.springframework.cglib.beans.BeanCopier;
import org.springframework.scheduling.annotation.Async;

import javax.cache.Cache;
import javax.cache.expiry.CreatedExpiryPolicy;
import javax.cache.expiry.Duration;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @program: match-trade
 * @description: dapr 支持相关接口实现
 * @author: yuxiaobing
 * @mail：a17281293@gmail.com
 * @date: 2023-10-29 16:36
 **/
public class DaprUtil {

}
