package com.shop.steam.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.shop.steam.entity.OrderItem;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OrderItemMapper extends BaseMapper<OrderItem> {
}