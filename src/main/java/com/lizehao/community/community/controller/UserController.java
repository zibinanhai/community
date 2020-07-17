package com.lizehao.community.community.controller;

import com.lizehao.community.community.annotation.LoginRequired;
import com.lizehao.community.community.entity.User;
import com.lizehao.community.community.service.UserService;
import com.lizehao.community.community.util.CommunityUtil;
import com.lizehao.community.community.util.HostHolder;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

@Controller
@RequestMapping("/user")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Value("${community.path.upload}")
    private String uploadPath;

    @Value("${community.path.domin}")
    private String domin;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    @Autowired
    private UserService userService;

    @Autowired
    private HostHolder hostHolder;


    @LoginRequired
    @RequestMapping(path = "/setting",method = RequestMethod.GET)
    public String getSettingPage(){
        return "/site/setting";
    }

    @LoginRequired
    @RequestMapping(path = "/upload", method = RequestMethod.POST)
    public String uploadHeader(MultipartFile headerImage, Model model){
        if(headerImage == null) {
            model.addAttribute("error","您还没有选择图片");
            return "/site/setting";
        }
        //文件后缀名
        String fileName = headerImage.getOriginalFilename();
        String suffix = fileName.substring(fileName.lastIndexOf("."));
        if(StringUtils.isBlank(suffix)) {
            model.addAttribute("error","图片的格式不正确");
            return "/site/setting";
        }

        //生成随机文件名
        fileName = CommunityUtil.generateUUID() + suffix;

        //文件存放路径
        File dest = new File(uploadPath + "/" + fileName);
        try {
            //存储文件
            headerImage.transferTo(dest);
        } catch (IOException e) {
            logger.error("上传文件失败：" + e.getMessage());
            throw new RuntimeException("上传文件失败：" + e.getMessage());
        }

        //更新当前用户头像的路径
        User user = hostHolder.getUser();
        String headerUrl = domin + contextPath + "/user/header/" + fileName;
        userService.updateHeader(user.getId(), headerUrl);

        return  "redirect:/index";
    }

    //不登陆也可以看别人的头像，所以没必要拦截
    @RequestMapping(path = "/header/{fileName}", method = RequestMethod.GET)
    public void getHeader(@PathVariable("fileName") String fileName, HttpServletResponse response){
        //服务器存放头像的路径
        fileName = uploadPath + "/" + fileName;

        String suffix = fileName.substring(fileName.lastIndexOf("."));

        //响应图片
        response.setContentType("image/" + suffix);
        try (
                //os是Spring MVC获取的，会自动关流
                //fis是自己定义的，要自己关，这里用java7标准IO流异常控制写法，可以自动关流
                FileInputStream fis = new FileInputStream(fileName);
            ) {

            OutputStream os = response.getOutputStream();

            byte[] buffer = new byte[1024];
            int b = 0;
            while((b = fis.read(buffer)) != -1){

                os.write(buffer, 0, b);

            }
        } catch (IOException e) {
            logger.error("获取头像失败:" + e.getMessage());
        }


    }
    @RequestMapping(path = "/changePassword", method = RequestMethod.POST)
    public String changePassword(Model model, String oldPassword, String newPassword, String confirm) {

        User user = hostHolder.getUser();
        oldPassword = CommunityUtil.md5(oldPassword + user.getSalt());
        if (!oldPassword.equals(user.getPassword())){
            model.addAttribute("passwordMsg", "原始密码不正确");
            return "/site/setting";
        }

        if (!newPassword.equals(confirm)) {
            model.addAttribute("confirmMsg", "两次输入的密码不一致");
            return "/site/setting";
        }

        //更新密码
        newPassword = CommunityUtil.md5(newPassword + user.getSalt());
        userService.updatePassword(user.getId(), newPassword);

        return "redirect:/index";
    }
}


