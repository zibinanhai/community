package com.lizehao.community.community.controller;

import com.lizehao.community.community.entity.DiscussPost;
import com.lizehao.community.community.entity.Page;
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

    /**
     *
     * @param model
     * @param page 页面会通过Page实体传入分页有关的条件，服务器也要对page做一些设置
     * @return
     */
    @RequestMapping(path = "/index", method = RequestMethod.GET)
    public String getIndexPage(Model model, Page page){
        //方法调用前，Spring MVC自动实例化了参数model和page，并且把page注入到了model里面
        //所以不用model.addAttribute，这样page的参数就可以自动获取，不用一个一个声明获取@RequestParam
        page.setRows(discussPostService.findDiscussPostRows(0));
        page.setPath("/index");

        /*
         * 获取一个页面的所有帖子
         * userId：判断要显示的是首页还是用户
         * offset和limit：判断当前页面显示哪一页的帖子
         */
        List<DiscussPost> list = discussPostService.findDiscussPosts(0,page.getOffset(),page.getLimit());
         // 把上面的List遍历，存储在一个包含用户帖子和用户信息的List，用List嵌套Map实现
        List<Map<String, Object>> discussPosts = new ArrayList<>();


        for (DiscussPost post:list) {
           //每取一个帖子信息，就创建一个Map，并把这个Map存在List<Map<String, Object>>里面
            Map<String, Object> map = new HashMap<>();
            map.put("post", post);
            //每个Post都是一个DiscussPost对象,获得的外键UserId，就是User里的主键Id
            User user = userService.findUserById(post.getUserId());
            map.put("user", user);
            discussPosts.add(map);

        }

        model.addAttribute("discussPosts", discussPosts);
        return "/index";
    }
}
