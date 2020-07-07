package com.lizehao.community.community.controller;


import com.lizehao.community.community.entity.User;
import com.lizehao.community.community.service.UserService;
import com.lizehao.community.community.util.CommunityConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Map;

@Controller
public class LoginController implements CommunityConstant {

    @Autowired
    UserService userService;

    //返回注册页面不需要动态改动，所以不需要model
    @RequestMapping(path = "/register",method = RequestMethod.GET)
    public String getRegisterPage(){
        return  "/site/register";
    }

    @RequestMapping(path = "/login",method = RequestMethod.GET)
    public String getLoginPage(){
        return  "/site/login";
    }

    //避免url太长和暴露密码，这里用post
    //不用传进来三个参数，Spring MVC会自动把与user属性匹配的值注入给user对象
    //是通过注册页面register.html里表单的input里的参数名和user对象的参数名匹配的
    @RequestMapping(path = "/register", method = RequestMethod.POST)
    public String register(Model model, User user){

        Map<String,Object> map = userService.register(user);
        //null判断是否new了map,isEmpty()是判断是否put了键值对
        //map不为空也不为null就代表用户信息已经录入数据库，就返回提示信息和自动跳转首页
        if(map == null||map.isEmpty()){

            model.addAttribute("msg","注册成功，我们已经向您的邮箱发送了一封邮件，请尽快激活！");
            model.addAttribute("target","/index");
            return "/site/operate-result";

        }else {
            //注册有问题的话就把错误信息传给model最后传给客户端
            model.addAttribute("usernameMsg",map.get("usernameMsg"));
            model.addAttribute("passwordMsg",map.get("passwordMsg"));
            model.addAttribute("emailMsg",map.get("emailMsg"));
            return "/site/register";
        }
    }

    /**
     * 用来接收下面这个格式的链接（就是给用户邮箱发的激活链接）
     * //http://localhost:8080/community/activation/101/code
     * 把链接里的userId和激活码传给业务层方法，判断激活码是否正确
     * 根据判断结果返回页面
     */
    @RequestMapping(path ="/activation/{userId}/{code}",method = RequestMethod.GET)
    public String activation(Model model,
                             @PathVariable("userId") int userId,
                             @PathVariable("code") String code){
        int result = userService.activation(userId,code);
        if (result == ACTIVATION_SUCCESS){
            model.addAttribute("msg","激活成功，即将为您跳转到登录页面。");
            model.addAttribute("target","/login");
        }else if(result == ACTIVATION_REPEAT){
            model.addAttribute("msg","您的账号已经激活过了，请勿重复激活！");
            model.addAttribute("target","/index");

        }else {
            model.addAttribute("msg","激活失败，您的激活码不正确，请在邮箱点击链接！");
            model.addAttribute("target","/index");
        }
        return "/site/operate-result";
    }

}
