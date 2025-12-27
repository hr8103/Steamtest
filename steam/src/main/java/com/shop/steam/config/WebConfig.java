package com.shop.steam.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web通用配置
 * 作用：配置静态资源映射，让上传的图片可以被浏览器访问
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 获取当前项目根目录
        String path = System.getProperty("user.dir") + "/uploads/";

        // 映射规则：访问 http://localhost:8080/images/xxx.jpg
        // 就会去本地项目的 uploads 文件夹下找 xxx.jpg
        registry.addResourceHandler("/images/**")
                .addResourceLocations("file:" + path);
    }
}