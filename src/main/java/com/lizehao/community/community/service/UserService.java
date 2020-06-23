package com.lizehao.community.community.service;

import com.lizehao.community.community.dao.UserMapper;
import com.lizehao.community.community.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    @Autowired
    private UserMapper userMapper;

    public User finndUserById(int id){
        return userMapper.selectById(id);
    }
}
