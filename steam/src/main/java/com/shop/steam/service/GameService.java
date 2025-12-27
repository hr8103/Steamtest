package com.shop.steam.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.shop.steam.entity.Game;

// 继承 IService，MyBatis-Plus 会自动帮你写好几十个常用方法
public interface GameService extends IService<Game> {
    // 这里以后可以定义复杂的业务接口，比如“计算打折后的价格”
}