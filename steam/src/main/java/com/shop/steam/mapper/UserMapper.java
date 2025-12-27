package com.shop.steam.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.shop.steam.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {
    // MyBatis-Plus 自动实现了增删改查，这里留空即可
}