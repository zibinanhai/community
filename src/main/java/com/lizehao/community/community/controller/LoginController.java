package com.lizehao.community.community.controller;


import com.google.code.kaptcha.Producer;
import com.lizehao.community.community.entity.User;
import com.lizehao.community.community.service.UserService;
import com.lizehao.community.community.util.CommunityConstant;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.imageio.ImageIO;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

@Controller
public class LoginController implements CommunityConstant {

    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    UserService userService;

    @Autowired
    private Producer kaptchaProducer;

    @Value("${server.servlet.context-path}")
    private String contextPath;

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
        //map为空就代表用户信息已经录入数据库，就返回提示信息和自动跳转首页
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

    /**
     * 生成验证码，不在getLoginPage()里写
     * 那个方法只是返回一个html，浏览器会根据图片路径再次访问服务器
     * 由于返回的是一个图片，不是模板也不是RespondBody，所以要手动
     * 服务器要记住这个验证码来验证，所以要用Session存（不用Cookie是因为这个是验证信息，属于敏感信息）
     */
    @RequestMapping(path = "/kaptcha",method = RequestMethod.GET)
    public void getKaptcha(HttpServletResponse response, HttpSession session){
        //生成验证码
        //已经配置类里配置过，所以直接获得Text就是一个4位字符串
        String text = kaptchaProducer.createText();
        BufferedImage image = kaptchaProducer.createImage(text);

        // 把验证码存进session
        session.setAttribute("kaptcha",text);

        //把图片输出给浏览器
        response.setContentType("image/png");
        try {
            //Spring MVC会自动关流
            OutputStream os = response.getOutputStream();
            ImageIO.write(image,"png",os);
        } catch (IOException e) {
            logger.error("响应验证码失败："+ e.getMessage());
        }
    }


    /**
     * @param code 输入的验证码
     * @param rememberme 勾选"记住我"
     * @param session 服务端需要用Session存验证码
     * @param response 需要用Cookie在客户端保存ticket,服务端不用保存，数据库有
     * @return
     */
    //两个方法路径可以一样，但是get、post等方法必须不一样
    //第一次是这样，用户保持登录状态后，怎么处理？
    @RequestMapping(path = "/login",method = RequestMethod.POST)
    public String login(String username,String password, String code, boolean rememberme,
                        Model model, HttpSession session, HttpServletResponse response){

        //表现层直接判断验证码

        //session可以存各种类型，所以获取的时候需要强转
        String kaptcha = (String)session.getAttribute("kaptcha");
        //equalsIgnoreCase是不管大小写
        if(StringUtils.isBlank(kaptcha)|| StringUtils.isBlank(code)||!kaptcha.equalsIgnoreCase(code)){
            model.addAttribute("codeMsg","验证码不正确！");
            return "site/login";

        }

        //检查账号密码
        int expiredSeconds = rememberme ? REMEMBER_EXPIRED_SECONDS : DEFAULF_EXPIRED_SECONDS;

        Map<String, Object> map = userService.login(username,password,expiredSeconds);
        if(map.containsKey("ticket")){
            //把ticket放在cookie里发送给客户端，让客户端保存
            //cookie范围是整个项目，所以客户端访问所有页面都会携带这个cookie（也即ticket）
            Cookie cookie = new Cookie("ticket", map.get("ticket").toString());
            cookie.setPath(contextPath);
            cookie.setMaxAge(expiredSeconds);
            response.addCookie(cookie);
            //重定向首页
            return "redirect:/index";
        }else{
            model.addAttribute("usernameMsg",map.get("usernameMsg"));
            model.addAttribute("passwordMsg",map.get("passwordMsg"));
            return "/site/login";
        }


    }
    @RequestMapping(path = "/logout", method = RequestMethod.GET)
    public String logout(@CookieValue("ticket") String ticket){
        userService.logout(ticket);
        return "redirect:/login";

    }

}
