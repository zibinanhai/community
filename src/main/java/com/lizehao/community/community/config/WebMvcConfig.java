package com.lizehao.community.community.config;

import com.lizehao.community.community.controller.interceptor.LoginRequiredInterceptor;
import com.lizehao.community.community.controller.interceptor.LoginTicketInterceptor;
import com.lizehao.community.community.controller.interceptor.TestInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Autowired
    private TestInterceptor testInterceptor;

    @Autowired
    private LoginTicketInterceptor loginTicketInterceptor;

    @Autowired
    private LoginRequiredInterceptor loginRequiredInterceptor;


    //preHandle按拦截器配置顺序地调用
    //postHandle按拦截器配置逆序地调用
    //afterCompletion按拦截器配置逆序地调用
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        //  /* 是拦截所有的文件夹，不包含子文件夹
        //  /** 是拦截所有的文件夹及里面的子文件夹
        registry.addInterceptor(testInterceptor)
                .excludePathPatterns("/**/*.css", "/**/*.js", "/**/*.png", "/**/*.jpg", "/**/*.jpeg")
                .addPathPatterns("/register", "/login");

        //全部页面都要进行登录后处理，所以全部都拦截
        registry.addInterceptor(loginTicketInterceptor)
                .excludePathPatterns("/**/*.css", "/**/*.js", "/**/*.png", "/**/*.jpg", "/**/*.jpeg");

        registry.addInterceptor(loginRequiredInterceptor)
                .excludePathPatterns("/**/*.css", "/**/*.js", "/**/*.png", "/**/*.jpg", "/**/*.jpeg");

    }

}
