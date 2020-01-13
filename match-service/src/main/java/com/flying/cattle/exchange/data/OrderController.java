/**
 * @filename: OrderMatchInputResources.java 2019年12月19日
 * @project exchange  V1.0
 * Copyright(c) 2020 flying-cattle Co. Ltd. 
 * All right reserved. 
 */
package com.flying.cattle.exchange.data;

import java.math.BigDecimal;
import java.util.Random;

import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.flying.cattle.exchange.model.JsonResult;
import com.flying.cattle.exchange.order.OrderFactory;
import com.flying.cattle.exchange.order.StateHandler;
import com.flying.cattle.exchange.util.DataUtil;
import com.flying.cattle.exchange.util.SnowflakeIdWorker;
import com.flying.cattle.exchange.util.ValidationResult;
import com.flying.cattle.exchange.util.ValidationUtils;
import com.flying.cattle.mt.entity.CancelOrderParam;
import com.flying.cattle.mt.entity.Order;
import com.flying.cattle.mt.entity.OrderParam;
import com.flying.cattle.mt.enums.OrderState;

import lombok.extern.slf4j.Slf4j;

/**
 * @ClassName: OrderMatchInputResources
 * @Description: 撮合订单输入数据源
 * @author flying-cattle
 * @date 2019年12月19日
 */
@Slf4j
@RestController
@RequestMapping("/exchange/order")
public class OrderController {
	
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
			//校验完成推送消息
			StateHandler stateHandler = OrderFactory.getByOrderState(OrderState.PAID);
			stateHandler.handler(order);
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
			Order order = new Order();
			order.setId(param.getId());
			order.setCoinTeam(param.getCoinTeam());
			//实现-查看是否是可以撤销的状态
			StateHandler stateHandler = OrderFactory.getByOrderState(OrderState.TO_UNDO);
			stateHandler.handler(order);
			return res.success("操作成功！");
		} catch (Exception e) {
			log.error("撤销订单错误："+e);
			e.printStackTrace();
			return new JsonResult<Object>(e);
		}
	}
	
	
	/**
	 * @Title: addNewOrder
	 * @Description: TODO(添加新的订单)
	 * @param  参数
	 * @return void 返回类型
	 * @throws
	 */
	@Profile("local")
	@PostMapping("/addNewOrders/{size}")
	public JsonResult<Order> addNewOrders(@PathVariable("size") long size) {
		try {
			JsonResult<Order> res=new JsonResult<Order>();
			for (long i = 1; i <= size; i++) {
				Random random = new Random();
				int price = random.nextInt(100)+1;
				OrderParam param = new OrderParam(Boolean.TRUE, new BigDecimal(price), BigDecimal.ONE, BigDecimal.ONE, "XBIT-USDT", Boolean.FALSE);
				Order order = DataUtil.paramToOrder(param);
				order.setId(SnowflakeIdWorker.generateId());
				order.setUid(i);
				StateHandler stateHandler = OrderFactory.getByOrderState(OrderState.PAID);
				stateHandler.handler(order);
			}
			return res.success("成功:"+size);
		} catch (Exception e) {
			log.error("添加新的订单错误："+e);
			e.printStackTrace();
			return new JsonResult<Order>(e);
		}
	}
}