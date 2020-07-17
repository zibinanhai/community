package com.lizehao.community.community.util;

import com.lizehao.community.community.entity.User;
import org.springframework.stereotype.Component;

/**
 * 持有用户信息，用于代替Session对象
 */
@Component
public class HostHolder {

    //相当于一个局部变量，这个局部变量是Thread的ThreadLocalMap里面的Key
    private ThreadLocal<User> users = new ThreadLocal<>();

    public void setUsers (User user) {
        users.set(user);
    }

    public User getUser(){
        return  users.get();
    }

    //清除掉当前Thread的ThreadLocalMap
    //这也是JDK8设计ThreadLocal的好处，每个线程自己维护ThreadLocalMap，用完就可以销毁
    public void clear(){
        users.remove();

    }
}
