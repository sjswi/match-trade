package com.flying.cattle.me.hazelcast.setting;

import lombok.Data;

/**
 * 创建:Moic
 * 时间:2020/1/3 10:37
 * 功能描述:K8s部署环境网络配置
 */
@Data
public class K8sSetting {
    /**
     * 是否启用该配置
     */
    private Boolean enable;
    /**
     * 服务网关
     */
    private String ServiceDns;
}