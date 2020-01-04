/**
 * @filename: OrderMatchInputResources.java 2019年12月19日
 * @project exchange  V1.0
 * Copyright(c) 2020 flying-cattle Co. Ltd. 
 * All right reserved. 
 */
package com.flying.cattle.exchange.data;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.flying.cattle.exchange.entity.Order;
import com.flying.cattle.exchange.model.CancelOrderParam;
import com.flying.cattle.exchange.model.JsonResult;
import com.flying.cattle.exchange.model.OrderParam;
import com.flying.cattle.exchange.util.DataUtil;
import com.flying.cattle.exchange.util.SnowflakeIdWorker;
import com.flying.cattle.exchange.util.ValidationResult;
import com.flying.cattle.exchange.util.ValidationUtils;

import lombok.extern.slf4j.Slf4j;
import reactor.netty.http.server.HttpServerRequest;

/**
 * @ClassName: OrderMatchInputResources
 * @Description: 撮合订单输入数据源
 * @author flying-cattle
 * @date 2019年12月19日
 */
@Slf4j
@RestController
@RequestMapping("/exchange/order")
public class OrderMatchInputResources {
	
	@Autowired
	private KafkaTemplate<String, String> template;
	
	/**
	 * @Title: addNewOrder
	 * @Description: TODO(添加新的订单)
	 * @param  参数
	 * @return void 返回类型
	 * @throws
	 */
	@PostMapping("/addNewOrder")
	public JsonResult<Order> addNewOrder(@RequestBody OrderParam param) {
		try {
			JsonResult<Order> res=new JsonResult<Order>();
			ValidationResult vr = ValidationUtils.validateEntity(param);
			if (vr.isHasErrors()) {
				return res.error(vr.getFirstErrors());
			}
			Order order = DataUtil.paramToOrder(param);
			order.setId(SnowflakeIdWorker.generateId());
			order.setUid(SnowflakeIdWorker.generateId());
			//关联用户，资产校验，或其他校验
			
			//校验完成推送消息
			template.send("new_order", order.toJsonString());
			return res.success(order);
		} catch (Exception e) {
			log.error("添加新的订单错误："+e);
			e.printStackTrace();
			return new JsonResult<Order>(e);
		}
	}
	

	/**
	 * @Title: addNewOrder
	 * @Description: TODO(添加新的订单)
	 * @param  参数
	 * @return void 返回类型
	 * @throws
	 */
	@PostMapping("/cancelOrder")
	public JsonResult<Object> cancelOrder(@RequestBody CancelOrderParam param) {
		try {
			JsonResult<Object> res=new JsonResult<Object>();
			ValidationResult vr = ValidationUtils.validateEntity(param);
			if (vr.isHasErrors()) {
				return res.error(vr.getFirstErrors());
			}
			//查看是否是可以撤销的状态
			template.send("cancel_order", param.toJsonString());
			return res.success("操作成功！");
		} catch (Exception e) {
			log.error("添加新的订单错误："+e);
			e.printStackTrace();
			return new JsonResult<Object>(e);
		}
	}
}
