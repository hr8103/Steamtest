package com.shop.steam.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shop.steam.entity.Game;
import com.shop.steam.mapper.GameMapper;
import com.shop.steam.service.GameService;
import org.springframework.stereotype.Service;

@Service // 必须加这个注解，Spring 才能扫描到
public class GameServiceImpl extends ServiceImpl<GameMapper, Game> implements GameService {
    // 目前先用默认逻辑，后续可以在这里覆盖重写
}