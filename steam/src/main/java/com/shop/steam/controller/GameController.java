package com.shop.steam.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.shop.steam.common.Result;
import com.shop.steam.entity.Game;
import com.shop.steam.service.GameService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Tag(name = "游戏管理接口", description = "包含游戏的增删改查与图片上传")
@RestController
@RequestMapping("/api/game")
@CrossOrigin
public class GameController {

    @Autowired
    private GameService gameService;

    // 1. 图片上传接口
    @Operation(summary = "上传游戏封面")
    @PostMapping("/upload")
    public Result<String> upload(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) return Result.error("上传文件不能为空");
        String originalFilename = file.getOriginalFilename();
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
        String newFileName = UUID.randomUUID().toString() + suffix;
        String projectPath = System.getProperty("user.dir");
        File saveFile = new File(projectPath + "/uploads/" + newFileName);
        if (!saveFile.getParentFile().exists()) saveFile.getParentFile().mkdirs();
        try {
            file.transferTo(saveFile);
            String fileUrl = "http://localhost:8080/images/" + newFileName;
            return Result.success(fileUrl);
        } catch (IOException e) {
            e.printStackTrace();
            return Result.error("文件上传失败");
        }
    }

    // 2. 新增游戏接口
    @Operation(summary = "发布新游戏")
    @PostMapping("/add")
    public Result<String> addGame(@RequestBody Game game) {
        // 默认设置为上架状态
        if(game.getStatus() == null) game.setStatus(1);
        boolean success = gameService.save(game);
        return success ? Result.success("游戏发布成功") : Result.error("发布失败");
    }

    // 3. 获取所有游戏列表 (首页默认按ID倒序)
    @Operation(summary = "获取所有游戏")
    @GetMapping("/list")
    public Result<List<Game>> list() {
        QueryWrapper<Game> query = new QueryWrapper<>();
        query.eq("status", 1); // 只显示上架的
        query.orderByDesc("id");
        return Result.success(gameService.list(query));
    }

    // ★★★ 4. 新增：搜索与排序接口 ★★★
    @Operation(summary = "搜索与排序")
    @GetMapping("/search")
    public Result<List<Game>> search(
            @RequestParam(required = false) String q,
            @RequestParam(required = false, defaultValue = "relevance") String sort
    ) {
        QueryWrapper<Game> query = new QueryWrapper<>();

        // 1. 只搜上架游戏
        query.eq("status", 1);

        // 2. 关键词模糊查询 (标题、简介、开发商)
        if (StringUtils.isNotBlank(q)) {
            query.and(wrapper -> wrapper
                    .like("title", q)
                    .or().like("short_description", q)
                    .or().like("developer", q)
            );
        }

        // 3. 排序逻辑
        switch (sort) {
            case "released_desc": // 发行日期
                query.orderByDesc("release_date");
                break;
            case "price_asc":     // 价格从低到高
                query.orderByAsc("price");
                break;
            case "price_desc":    // 价格从高到低
                query.orderByDesc("price");
                break;
            case "relevance":     // 相关性 (默认按最新)
            default:
                query.orderByDesc("id");
                break;
        }

        return Result.success(gameService.list(query));
    }

    // 5. 修改游戏信息
    @Operation(summary = "更新游戏信息")
    @PostMapping("/update")
    public Result<String> updateGame(@RequestBody Game game) {
        boolean success = gameService.updateById(game);
        return success ? Result.success("更新成功") : Result.error("更新失败");
    }

    // 6. 删除游戏
    @Operation(summary = "删除游戏")
    @DeleteMapping("/delete/{id}")
    public Result<String> deleteGame(@PathVariable Long id) {
        boolean success = gameService.removeById(id);
        return success ? Result.success("删除成功") : Result.error("删除失败");
    }
}