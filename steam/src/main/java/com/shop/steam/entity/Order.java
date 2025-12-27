package com.shop.steam.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("sys_order")
public class Order {
    @TableId(type = IdType.AUTO)
    private Long id;

    // 用户ID
    @TableField("user_id")
    private Long userId;

    // 订单号
    @TableField("order_no")
    private String orderNo;

    // PayPal订单ID
    @TableField("paypal_order_id")
    private String paypalOrderId;

    // ★★★ 核心修复：确保有这个字段，且名字是 totalAmount ★★★
    @TableField("total_amount")
    private BigDecimal totalAmount;

    // 货币类型
    private String currency;

    // 状态: 0-待支付, 1-已完成
    private Integer status;

    @TableField("create_time")
    private LocalDateTime createTime;

    @TableField("update_time")
    private LocalDateTime updateTime;
}