package com.lizehao.community.community.controller;


import com.lizehao.community.community.entity.Comment;
import com.lizehao.community.community.service.CommentService;
import com.lizehao.community.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Date;

@Controller
@RequestMapping("/comment")
public class CommentController {

    @Autowired
    private CommentService commentService;

    @Autowired
    private HostHolder hostHolder;


    //重定向需要帖子id，所以可以直接把帖子id也传过来
    //用Comment实体来接收各种评论的参数
    @RequestMapping(value = "/add/{discussPostId}", method = RequestMethod.POST)
    public String insertComment(@PathVariable("discussPostId") int discussPostId, Comment comment){
        //可能没有登录，获取不到User,后面统一做异常处理
        comment.setUserId(hostHolder.getUser().getId());
        comment.setStatus(0);
        comment.setCreateTime(new Date());
        commentService.insertComment(comment);

        return "redirect:/discuss/detail/" + discussPostId;
    }
}
