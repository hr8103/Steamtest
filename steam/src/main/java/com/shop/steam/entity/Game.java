package com.shop.steam.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.math.BigDecimal;

@Data
@TableName("game_info")
public class Game {
    @TableId(type = IdType.AUTO)
    private Long id;

    private String title;
    private BigDecimal price;
    private String shortDescription;
    private String coverUrl;
    private String developer;
    private String releaseDate;
    private String screenshots;
    private String aboutTheGame;
    private String tags;
    private String gameFeatures;
    private String supportedLanguages;

    private String legalNotice;
    private String website;
    private Integer metacriticScore;
    private String metacriticUrl;
    private String steamId;
    private Integer status;
}