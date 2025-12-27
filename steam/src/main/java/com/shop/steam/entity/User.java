package com.shop.steam.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("sys_user")
public class User {
    @TableId(type = IdType.AUTO)
    private Long id;

    private String username;
    private String password;
    private String nickname;
    private String email;
    private String avatar;

    // 核心字段：0-玩家, 1-管理员
    private Integer role;

    private LocalDateTime createTime;
}