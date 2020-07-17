package com.lizehao.community.community.util;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

public class CookieUtil {

    //从Cookie里取值的工具
    public static String getValue(HttpServletRequest request, String name){
        if(request == null || name == null){
             throw new IllegalArgumentException("参数为空！");
        }

        Cookie[] cookies = request.getCookies();
        if(cookies != null) {
            for(Cookie cookie : cookies){
                //字符串比较一定要用equals,刚又出错了
                if(cookie.getName().equals(name)){
                    return cookie.getValue();
                }
            }
        }

        return  null;


    }
}
