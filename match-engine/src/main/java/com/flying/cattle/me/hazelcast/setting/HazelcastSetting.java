package com.flying.cattle.me.hazelcast.setting;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
/**
 * 创建:Moic
 * 时间:2020/1/3 10:30
 * 功能描述:hazelcast缓存配置
 */
@Component
@ConfigurationProperties(prefix = "hazelcast")
@Data
public class  HazelcastSetting {
    /**
     * 是否使用自定义配置
     */
    private Boolean selfSetting;
    /**
     * 主动发现网络配置
     */
    private TcpIpSetting tcpIp;
    /**
     *
     */
    private K8sSetting kubernetes;
}