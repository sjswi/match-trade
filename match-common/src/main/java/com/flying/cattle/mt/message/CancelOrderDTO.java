package com.flying.cattle.mt.message;

import com.alibaba.fastjson.JSON;
import lombok.Data;

/**
 * @author senkyouku
 * @date 2020/07/06
 */
@Data
public class CancelOrderDTO {

    /**
     * -自增id
     * -排序用id,不用时间排序.
     */
    private long id;

    /**
     * -交易队id
     */
    private int symbolId;

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }
}
