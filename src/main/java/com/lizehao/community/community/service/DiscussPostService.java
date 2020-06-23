package com.lizehao.community.community.service;

import com.lizehao.community.community.dao.DiscussPostMapper;
import com.lizehao.community.community.entity.DiscussPost;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DiscussPostService {

    @Autowired
    private DiscussPostMapper discussPostMapper;

    //即使业务简单也要分层，不要直接从Cotroller调用DAO
    //在UserService里面去通过userId找到用户信息，在用户主页显示
    public List<DiscussPost> findDiscussPosts(int userId, int offset, int limit){
        return discussPostMapper.selectDiscussPosts(userId, offset, limit);
    }

    public int findDiscussPostRows(int userId){
        return discussPostMapper.selectDiscussPostRows(userId);
    }

}
