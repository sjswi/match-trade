/**
 * @filename: CancelOrderParam.java 2019年12月19日
 * @project exchange  V1.0
 * Copyright(c) 2020 flying-cattle Co. Ltd. 
 * All right reserved. 
 */
package com.flying.cattle.exchange.model;

import java.io.Serializable;
import javax.validation.constraints.NotNull;
import com.alibaba.fastjson.JSON;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassName: CancelOrderParam
 * @Description: TODO(撤单参数)
 * @author flying-cattle
 * @date 2019年12月19日
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CancelOrderParam implements Serializable {
	
	private static final long serialVersionUID = -2129129168099491810L;

	//@ApiModelProperty(name = "id", value = "挂单ID")
	@NotNull(message = "id不能为空")
	private Long id;
	
	//@ApiModelProperty(name = "isBuy", value = "是买挂单")
	@NotNull(message = "buy不能为空")
	private Boolean isBuy;
	
	//(name = "coinTeam" , value = "交易队")
	@NotNull(message = "coinTeam不能为空")
	private String coinTeam;
	
	public String toJsonString() {
		return JSON.toJSONString(this);
	}
}
