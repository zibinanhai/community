package com.lizehao.community.community.controller.interceptor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class TestInterceptor implements HandlerInterceptor {
    //接口有三个方法，default修饰，不强制实现


    private static final Logger logger = LoggerFactory.getLogger(TestInterceptor.class);

    //在Controller之前执行
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception{
        logger.debug("preHandle:" + handler.toString());
        return true;
    }

    //Controller之后，模板引擎前执行
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        logger.debug("postHandle:" + handler.toString());
    }

    //在模板引擎之后执行
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

        logger.debug("afterCompletion:" + handler.toString());
    }
}
