package com.lizehao.community.community.dao;

import com.lizehao.community.community.entity.Comment;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CommentMapper {

    /**
     * 实现评论分页
     * @param entityType 评论类型
     * @param entityId   评论目标的Id
     * @param offset   分页
     * @param limit 每页显示评论数量
     */
    List<Comment> selectCommentByEntity(int entityType, int entityId, int offset, int limit);

    //查询条目数
    int selectCountByentity(int entityType, int entityId);

    //添加评论
    int insertComment(Comment comment);


}
