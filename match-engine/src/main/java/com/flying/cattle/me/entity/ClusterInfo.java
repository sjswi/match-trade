/**
 * @filename: ClusterInfo.java 2020年1月4日
 * @project match-service  V1.0
 * Copyright(c) 2020 flying-cattle Co. Ltd. 
 * All right reserved. 
 */
package com.flying.cattle.me.entity;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassName: ClusterInfo
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author flying-cattle
 * @date 2020年1月4日
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClusterInfo  implements Serializable{
	
	private static final long serialVersionUID = 381831908688998689L;
	//ip
	String ip;
	//开始时间
	long startTime;
	//启动时间
	long updateTime;
}
