package com.lizehao.community.community.dao;

import com.lizehao.community.community.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper {

    //要实现这些方法，需要提供一个配置文件，给每一个方法提供它需要的sql
    //然后mybatis会自动生成实现类
    //insert、update、delete语句的返回值类型：对数据库执行修改操作时，数据库会返回受影响的行数。
    //在MyBatis中insert、update、delete语句的返回值可以是Integer、Long和Boolean。
    // 在定义Mapper接口时直接指定需要的类型即可，无需在对应的<insert><update><delete>标签中显示声明。
    //对应的代码在 org.apache.ibatis.binding.MapperMethod类中
    //对于返回类型为Boolean的情况，如果返回的值大于0，则返回True，否则返回False
    User selectById(int id);

    User selectByName(String username);

    User selectByEmail(String email);

    int insertUser(User user);

    int updateStatus(int id, int status);

    int updateHeader(int id, String headerUrl);

    int updatePassword(int id, String password);
}
