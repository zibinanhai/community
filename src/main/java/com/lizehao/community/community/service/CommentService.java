package com.lizehao.community.community.service;

import com.lizehao.community.community.dao.CommentMapper;
import com.lizehao.community.community.entity.Comment;
import com.lizehao.community.community.util.CommunityConstant;
import com.lizehao.community.community.util.SensitiveFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.HtmlUtils;

import java.util.List;

@Service
public class CommentService implements CommunityConstant {

    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private SensitiveFilter sensitiveFilter;

    @Autowired
    private DiscussPostService discussPostService;

    public List<Comment> findCommentByEntity (int entityType, int entityId, int offset, int limit) {
        return commentMapper.selectCommentByEntity(entityType, entityId, offset, limit);
    }

    public int findCommentCount(int entityType, int entityId) {
        return commentMapper.selectCountByentity(entityType, entityId);
    }


    //事务管理（这里用声明式事务）
    //添加评论并且更新帖子的评论数量
    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    public int insertComment(Comment comment) {
        if (comment == null) {
            throw new IllegalArgumentException("参数不能为空");
        }

        //添加评论

        //把文本里的html特殊符号转义，不要让网页把文本里的字符当成标签
        comment.setContent(HtmlUtils.htmlEscape(comment.getContent()));
        //过滤敏感词
        comment.setContent(sensitiveFilter.filter(comment.getContent()));
        int rows = commentMapper.insertComment(comment);


        //更新帖子评论数量
        if(comment.getEntityType() == ENTITY_TYPE_POST) {
            //添加评论以后重新计算帖子的评论数量
            int count = commentMapper.selectCountByentity(comment.getEntityType(),comment.getEntityId());
            //更新帖子的评论数量
            discussPostService.updateCommentCount(comment.getEntityId(),count);

        }

        return rows;

    }
}
