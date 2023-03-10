package com.pedro.auth.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.Resource;

/**
 * WebMvcConfig
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Resource
    private UserAuthInterceptor userAuthInterceptor;

    //注册拦截器
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(userAuthInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns("/**/*.css", "/**/*.js", "/**/*.png", "/**/*.jpg", "/**/*.ico", "/**/*.jpeg", "/**/*.ttf", "/**/*.svg", "/**/*.html", "/roleDenied.html");
    }
}