package com.lizehao.community.community.controller.interceptor;


import com.lizehao.community.community.annotation.LoginRequired;
import com.lizehao.community.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

@Component
public class LoginRequiredInterceptor implements HandlerInterceptor {

    @Autowired
    private HostHolder hostHolder;

    //让没有登录的用户不能访问特定网页
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        //先判断拦截的是不是一个方法
        if(handler instanceof HandlerMethod) {

            //通过反射获取注解，拦截到的请求里只处理带注解的，配置里只用排除静态资源
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            Method method = handlerMethod.getMethod();
            LoginRequired loginRequired = method.getAnnotation(LoginRequired.class);
            //preHandle 是按配置顺序调用的，所以先调用LoginTicketinterception
            //如果用户是已登录用户，hostHolder就已经添加了user对象
            //所以，当注解存在，并且用户没有登录的情况下，拦截方法不让用户访问
            if(loginRequired != null && hostHolder.getUser() == null) {

                //不让用户访问该界面，并且重定向到登录页面
                //Controller里面的重定向底层也是这样写的
                response.sendRedirect(request.getContextPath() + "/login");
                return false;

            }



        }
        return true;
    }
}
