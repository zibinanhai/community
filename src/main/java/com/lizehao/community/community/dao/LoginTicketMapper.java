package com.lizehao.community.community.dao;

import com.lizehao.community.community.entity.LoginTicket;
import org.apache.ibatis.annotations.*;

@Mapper
public interface LoginTicketMapper {

    @Insert({
            "insert into login_ticket (user_id,ticket,status,expired) ",
            "values(#{userId},#{ticket},#{status},#{expired}) "
    })
    //设定自增属性
    //mysql可以设置自增主键，所以用mybatis配置的时候不用管自增主键，mysql自动自增主键
    //但是这里为什么要设置自增呢？
    //测试了一下，没有这个注解，数据库还是可以生成自增主键
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insertLoginTicket(LoginTicket loginTicket);

    @Select({
            "select id,user_id,ticket,status,expired ",
            "from login_ticket where ticket=#{ticket}"
    })
        //ticket是凭证，发送给浏览器保存，其他数据是服务端自己存
    LoginTicket selectByTicket(String ticket);

    @Update({
            "update login_ticket set status=#{status} where ticket=#{ticket} "
    })
    int updateStatus(String ticket,int  status);

}
