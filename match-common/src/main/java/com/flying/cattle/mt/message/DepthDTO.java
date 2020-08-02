package com.flying.cattle.mt.message;

import com.alibaba.fastjson.JSON;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author senkyouku
 * @date 2020/06/29
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DepthDTO implements Serializable{
    private static final long serialVersionUID = -6105219041319811826L;
    /**
     * 单价
     */
    private Long    price;
    /**
     * 交易数量
     */
    private Long    num;
    /**
     * 累计
     */
    private Long    total;

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }
}
