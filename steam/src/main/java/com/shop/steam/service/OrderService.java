package com.shop.steam.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.paypal.core.PayPalHttpClient;
import com.paypal.http.HttpResponse;
import com.paypal.orders.*;
import com.shop.steam.entity.Game;
import com.shop.steam.entity.Order;
import com.shop.steam.entity.OrderItem;
import com.shop.steam.mapper.GameMapper;
import com.shop.steam.mapper.OrderItemMapper;
import com.shop.steam.mapper.OrderMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class OrderService {

    @Autowired
    private PayPalHttpClient payPalHttpClient;

    @Autowired
    private GameMapper gameMapper;

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private OrderItemMapper orderItemMapper;

    @Value("${paypal.return-url}")
    private String returnUrl;

    @Value("${paypal.cancel-url}")
    private String cancelUrl;

    /**
     * 创建订单（支持购物车批量结算）
     */
    @Transactional
    public Map<String, String> createOrder(Long userId, List<Long> gameIds) {
        // 1. 校验与查询
        if (gameIds == null || gameIds.isEmpty()) {
            throw new RuntimeException("购物车为空");
        }
        List<Game> games = gameMapper.selectBatchIds(gameIds);
        if (games.isEmpty()) throw new RuntimeException("未找到游戏信息");

        // 2. 计算总价
        BigDecimal totalAmount = BigDecimal.ZERO;
        for (Game game : games) {
            BigDecimal price = game.getPrice() != null ? game.getPrice() : BigDecimal.ZERO;
            totalAmount = totalAmount.add(price);
        }

        // 3. 生成本地订单
        String orderNo = UUID.randomUUID().toString().replace("-", "");

        // 3.1 插入主表
        Order order = new Order();
        order.setUserId(userId);
        order.setOrderNo(orderNo);
        order.setTotalAmount(totalAmount);
        order.setCurrency("USD");
        order.setStatus(0); // 0-待支付
        order.setCreateTime(LocalDateTime.now());
        orderMapper.insert(order);

        // 3.2 插入明细表
        for (Game game : games) {
            OrderItem item = new OrderItem();
            item.setOrderId(order.getId());
            item.setGameId(game.getId());
            item.setGameName(game.getTitle());
            item.setPrice(game.getPrice());
            orderItemMapper.insert(item);
        }

        // 4. 请求 PayPal 创建远程订单
        OrderRequest orderRequest = new OrderRequest();
        orderRequest.checkoutPaymentIntent("CAPTURE");

        ApplicationContext context = new ApplicationContext()
                .brandName("Steam Store")
                .landingPage("BILLING")
                .returnUrl(returnUrl)
                .cancelUrl(cancelUrl);
        orderRequest.applicationContext(context);

        List<PurchaseUnitRequest> purchaseUnits = new ArrayList<>();
        PurchaseUnitRequest unit = new PurchaseUnitRequest()
                .referenceId(orderNo)
                .amountWithBreakdown(new AmountWithBreakdown()
                        .currencyCode("USD")
                        .value(totalAmount.toString()));
        purchaseUnits.add(unit);
        orderRequest.purchaseUnits(purchaseUnits);

        try {
            OrdersCreateRequest request = new OrdersCreateRequest().requestBody(orderRequest);
            HttpResponse<com.paypal.orders.Order> response = payPalHttpClient.execute(request);

            String paypalOrderId = response.result().id();
            String approveUrl = response.result().links().stream()
                    .filter(link -> "approve".equals(link.rel()))
                    .findFirst().orElseThrow().href();

            // 5. 更新本地主单的 PayPal ID
            order.setPaypalOrderId(paypalOrderId);
            orderMapper.updateById(order);

            Map<String, String> res = new HashMap<>();
            res.put("approveUrl", approveUrl);
            return res;

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("PayPal 连接失败: " + e.getMessage());
        }
    }

    /**
     * 支付回调捕获（扣款）
     */
    @Transactional
    public boolean captureOrder(String paypalOrderId) {
        // 1. 查找本地订单
        Order localOrder = orderMapper.selectOne(new LambdaQueryWrapper<Order>()
                .eq(Order::getPaypalOrderId, paypalOrderId));

        if (localOrder == null) {
            return false;
        }

        // 如果已经是完成状态，直接返回成功
        if (localOrder.getStatus() == 1) {
            return true;
        }

        OrdersCaptureRequest request = new OrdersCaptureRequest(paypalOrderId);
        request.requestBody(new OrderRequest());

        try {
            HttpResponse<com.paypal.orders.Order> response = payPalHttpClient.execute(request);
            String status = response.result().status();

            // 只要是完成或批准状态，都视为成功
            if ("COMPLETED".equals(status) || "APPROVED".equals(status)) {
                localOrder.setStatus(1); // 修改为已支付
                localOrder.setUpdateTime(LocalDateTime.now());
                orderMapper.updateById(localOrder);
                return true;
            }
        } catch (Exception e) {
            // 处理 "Order already captured" 重复扣款错误（视为成功）
            if (e.getMessage().contains("Order already captured")) {
                localOrder.setStatus(1);
                localOrder.setUpdateTime(LocalDateTime.now());
                orderMapper.updateById(localOrder);
                return true;
            }
            e.printStackTrace();
        }
        return false;
    }
}