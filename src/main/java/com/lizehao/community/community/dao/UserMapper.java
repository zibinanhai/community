package com.lizehao.community.community.dao;

import com.lizehao.community.community.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper {

    //要实现这些方法，需要提供一个配置文件，给每一个方法提供它需要的sql
    //然后mybatis会自动生成实现类
    User selectById(int id);

    User selectByName(String username);

    User selectByEmail(String email);

    int insertUser(User user);

    int updateStatus(int id, int status);

    int updateHeader(int id, String headerUrl);

    int updatePassword(int id, String password);
}
