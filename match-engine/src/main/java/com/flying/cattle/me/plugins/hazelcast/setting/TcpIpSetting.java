package com.flying.cattle.me.plugins.hazelcast.setting;

import lombok.Data;

import java.util.List;

/**
 * 创建:Moic
 * 时间:2020/1/3 10:32
 * 功能描述:主动发现配置
 */
@Data
public class TcpIpSetting {
    /**
     * 是否启用该配置
     */
    private Boolean enable;
    /**
     * 网关表达式
     */
    private String  interfaceEx;
    /**
     * 实例集合
     */
    private List<String> members;
}