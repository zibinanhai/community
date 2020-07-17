package com.lizehao.community.community.controller.interceptor;

import com.lizehao.community.community.dao.LoginTicketMapper;
import com.lizehao.community.community.entity.LoginTicket;
import com.lizehao.community.community.entity.User;
import com.lizehao.community.community.service.UserService;
import com.lizehao.community.community.util.CookieUtil;
import com.lizehao.community.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

@Component
public class LoginTicketInterceptor implements HandlerInterceptor {

    @Autowired
    LoginTicketMapper loginTicketMapper;

    @Autowired
    UserService userService;

    @Autowired
    HostHolder hostHolder;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //接口的参数不能改，所以这里不能用CookieValue注解获得Cookie数据
        //所以这里用request获取Cookie，这个功能很常用，所以封装在CookieUtil里面

        //从Cookie中获取ticket
        String ticket = CookieUtil.getValue(request,"ticket");

        //从ticket里找到user
        if(ticket != null) {
            // 业务都从Service层调用，简单也要把Mapper里的方法封装到Service层
            // int userId = loginTicketMapper.selectByTicket(ticket).getUserId();

            //查询凭证
            LoginTicket loginTicket = userService.findLoginTicket(ticket);

            //检查过期时间
            //凭证存在，有效，没过期
            if(loginTicket !=null && loginTicket.getStatus() == 0 && loginTicket.getExpired().after(new Date())){

                //根据凭证查用户
                User user = userService.findUserById(loginTicket.getUserId());
                System.out.println(user+"ddd");

                //在本次请求中持有该用户
                //因为服务器要并发，所以user不应该存在容器里，容易冲突
                //应该隔离存放

                //存在当前线程的ThreadLocalMap里，key是users
                hostHolder.setUsers(user);

            }
        }
        return true;
    }

    //在模板引擎前就要注入user
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        User user = hostHolder.getUser();
        if(user != null && modelAndView != null){
            modelAndView.addObject("loginUser",user);


        }
    }

    //模板结束后，清理当前线程的ThreadLocalMap

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        hostHolder.clear();
    }
}
