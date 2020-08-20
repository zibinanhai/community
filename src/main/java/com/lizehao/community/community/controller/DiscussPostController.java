package com.lizehao.community.community.controller;


import com.lizehao.community.community.entity.Comment;
import com.lizehao.community.community.entity.DiscussPost;
import com.lizehao.community.community.entity.Page;
import com.lizehao.community.community.entity.User;
import com.lizehao.community.community.service.CommentService;
import com.lizehao.community.community.service.DiscussPostService;
import com.lizehao.community.community.service.UserService;
import com.lizehao.community.community.util.CommunityConstant;
import com.lizehao.community.community.util.CommunityUtil;
import com.lizehao.community.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.*;

@Controller
@RequestMapping("/discuss")
public class DiscussPostController implements CommunityConstant {

    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private UserService userService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private CommentService commentService;

    @RequestMapping(path = "/add", method = RequestMethod.POST)
    @ResponseBody
    public String addDiscussPost (String title, String content) {

        User user = hostHolder.getUser();
        //403代表没有权限
        if (user == null)  {
             return CommunityUtil.getJSONString(403, "你还没有登录!");
        }

        DiscussPost post = new DiscussPost();
        post.setUserId(user.getId());
        post.setTitle(title);
        post.setContent(content);
        post.setCreateTime(new Date());
        discussPostService.addDiscussPost(post);


        System.out.println(CommunityUtil.getJSONString(0, "发布成功"));

        //报错统一处理
        return CommunityUtil.getJSONString(0, "发布成功");

    }


    /**
     * 查询评论直接在查询帖子详情页里面一起做，因为是在同一个页面
     * @param discussPostId
     * @param page 和首页帖子分页用的同一个逻辑，用同一个分页对象处理
     * @return
     */
    @RequestMapping(path = "/detail/{discussPostId}", method = RequestMethod.GET)
    public String getDiscussPost(@PathVariable("discussPostId") int discussPostId, Model model, Page page) {
        //查询帖子
        DiscussPost post = discussPostService.findDiscussPostById(discussPostId);
        model.addAttribute("post", post);

        //查询帖子作者信息
        User user = userService.findUserById(post.getUserId());
        model.addAttribute("user", user);

        //评论分页信息
        //默认current=1,当点第n页的时候，链接是path+ ?current=n
        //这个current就传给了这个Controller得Page对象里
        //根据current得出offset，查询某一页的List
        page.setLimit(5);
        page.setPath("/discuss/detail/" + discussPostId);
        page.setRows(post.getCommentCount());


        //评论：帖子的评论
        //回复：给评论的评论
        //先查找到某个帖子里的某一页的全部评论
        List<Comment> commentList = commentService.findCommentByEntity(ENTITY_TYPE_POST, post.getId(), page.getOffset(), page.getLimit());

        //评论里要显示用户头像名字，所以用一个map来存评论和用户信息（遍历前面的评论）
        List<Map<String, Object>> commentUserList = new ArrayList<>();
        if(commentList != null) {
            for (Comment comment : commentList) {
                Map<String, Object>  commentMap = new HashMap<>();
                commentMap.put("user", userService.findUserById(comment.getUserId()));
                commentMap.put("comment", comment);

                //指定评论下的所有回复(不分页）
                List<Comment> replyList =  commentService.findCommentByEntity(
                        ENTITY_TYPE_COMMENT, comment.getId(), 0, Integer.MAX_VALUE);
                //回复的map列表
                List<Map<String, Object>> replyUserList = new ArrayList<>();
                //回复的map里要包括，回复，回复的用户，回复的目标用户
                if(replyList != null) {
                    for (Comment reply : replyList) {
                        Map<String, Object> replyMap = new HashMap<>();
                        replyMap.put("reply", reply);
                        replyMap.put("user", userService.findUserById(reply.getUserId()));

                        //回复目标
                        User target = reply.getTargetId() == 0 ? null : userService.findUserById(reply.getUserId());
                        replyMap.put("target", target);

                        replyUserList.add(replyMap);
                    }
                }
                //把评论下的回复信息集合添加到评论map里
                //最终给页面展现的数据都存在这个map里
                commentMap.put("replies", replyUserList);

                //回复数量
                int replyCount = commentService.findCommentCount(ENTITY_TYPE_COMMENT, comment.getId());
                commentMap.put("replyCount", replyCount);

                //把所有数据都添加到commentMap里面以后，把map放进List
                commentUserList.add(commentMap);


            }

        }

        model.addAttribute("comments", commentUserList);

        return "/site/discuss-detail";

    }
}
