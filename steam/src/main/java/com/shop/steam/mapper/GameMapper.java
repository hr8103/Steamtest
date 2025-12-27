package com.shop.steam.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.shop.steam.entity.Game;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface GameMapper extends BaseMapper<Game> {
    // 继承 BaseMapper 后，你自动拥有了 insert, delete, update, selectById, selectList 等方法
}