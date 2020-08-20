package com.lizehao.community.community.dao;

import com.lizehao.community.community.entity.DiscussPost;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface DiscussPostMapper {

    //查询帖子页面方法
    //实现查询功能，查询返回的是一页，所以要是一个List，多条数据
    //为什么传入userId是因为，以后要开发用户主页，显示用户发过的帖子
    //传入offset（这一页起始行号）和limit（一页最多多少数据）实现分页
    List<DiscussPost> selectDiscussPosts(int userId,int offset, int limit);

    //显示页数方法
    //为了显示页数，要查到一共多少帖子，然后除以每页显示的帖子数
    //@Param用于给参数取别名
    //注意：如果只有一个参数并且sql里要动态地(比如<if>)使用这个参数时，必须加上Param
    int selectDiscussPostRows(@Param("userId") int userId);

    int insertDiscussPost(DiscussPost discussPost);

    //查询特定帖子
    DiscussPost selectDiscussPostById(int id);

    //更新帖子评论数量
    int updateCommentCount(int id, int commentCount);



}
