package com.shop.steam.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.shop.steam.common.Result;
import com.shop.steam.entity.User;
import com.shop.steam.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user")
@CrossOrigin // 允许跨域
public class UserController {

    @Autowired
    private UserMapper userMapper;

    // === 真实登录接口 ===
    @PostMapping("/login")
    public Result<User> login(@RequestBody User loginForm) {
        // 1. 构造查询条件：where username = ?
        QueryWrapper<User> query = new QueryWrapper<>();
        query.eq("username", loginForm.getUsername());

        // 2. 查询数据库
        User user = userMapper.selectOne(query);

        // 3. 校验账号是否存在
        if (user == null) {
            return Result.error("用户名不存在");
        }

        // 4. 校验密码 (这里演示用明文比对)
        if (!user.getPassword().equals(loginForm.getPassword())) {
            return Result.error("密码错误");
        }

        // 5. 登录成功，为了安全，把密码设为空再返回给前端
        user.setPassword(null);
        return Result.success(user);
    }

    // === 获取用户列表 (管理员后台用) ===
    @GetMapping("/list")
    public Result<List<User>> list() {
        return Result.success(userMapper.selectList(null));
    }

    // === 删除/封禁用户 (管理员后台用) ===
    @DeleteMapping("/delete/{id}")
    public Result<String> deleteUser(@PathVariable Long id) {
        userMapper.deleteById(id);
        return Result.success("操作成功");
    }
}