package com.lizehao.community.community.controller;

import com.lizehao.community.community.entity.DiscussPost;
import com.lizehao.community.community.entity.User;
import com.lizehao.community.community.service.DiscussPostService;
import com.lizehao.community.community.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class HomeController {

    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private UserService userService;

    @RequestMapping(path = "/index", method = RequestMethod.GET)
    public String getIndexPage(Model model){
        List<DiscussPost> list = discussPostService.findDiscussPosts(0,0,10);
        //写一个包含用户帖子和用户信息的List，用List嵌套Map实现
        //Map里面只有两行数据，帖子和用户信息
        List<Map<String, Object>> discussPosts = new ArrayList<>();


        for (DiscussPost post:list) {
           //每取一个帖子信息，就创建一个Map，并把这个Map存在List<Map<String, Object>>里面
            Map<String, Object> map = new HashMap<>();
            map.put("post", post);
            //每个Post都是一个DiscussPost对象,获得的外键UserId，就是User里的主键Id
            User user = userService.finndUserById(post.getUserId());
            map.put("user", user);
            discussPosts.add(map);

        }

        model.addAttribute("discussPosts", discussPosts);
        return "/index";
    }
}
