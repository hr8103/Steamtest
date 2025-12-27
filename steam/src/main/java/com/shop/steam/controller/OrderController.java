package com.shop.steam.controller;

import com.shop.steam.common.Result;
import com.shop.steam.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/order")
@CrossOrigin
public class OrderController {

    @Autowired
    private OrderService orderService;

    /**
     * 购物车批量结算接口
     */
    @PostMapping("/create/batch")
    public Result<Map<String, String>> createBatchOrder(@RequestBody Map<String, Object> params) {
        try {
            // 安全解析 userId
            Object userIdObj = params.get("userId");
            if (userIdObj == null) return Result.error("User ID is missing");
            Long userId = Long.valueOf(userIdObj.toString());

            // 安全解析 gameIds
            Object gameIdsObj = params.get("gameIds");
            if (gameIdsObj == null) return Result.error("Game list is missing");

            List<Long> gameIds = ((List<?>) gameIdsObj).stream()
                    .map(id -> Long.valueOf(id.toString()))
                    .toList();

            return Result.success(orderService.createOrder(userId, gameIds));
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("订单创建失败：" + e.getMessage());
        }
    }

    /**
     * 单个购买接口 (兼容旧代码)
     */
    @PostMapping("/create")
    public Result<Map<String, String>> createOne(@RequestParam Long userId, @RequestParam Long gameId) {
        try {
            return Result.success(orderService.createOrder(userId, Collections.singletonList(gameId)));
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 支付成功回调 (捕获订单)
     */
    @PostMapping("/capture")
    public Result<String> capture(@RequestParam String token) {
        try {
            boolean success = orderService.captureOrder(token);
            if (success) {
                return Result.success("支付成功");
            } else {
                return Result.error("支付确认失败");
            }
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}