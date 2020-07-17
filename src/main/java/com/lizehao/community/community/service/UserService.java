package com.lizehao.community.community.service;

import com.lizehao.community.community.dao.LoginTicketMapper;
import com.lizehao.community.community.dao.UserMapper;
import com.lizehao.community.community.entity.LoginTicket;
import com.lizehao.community.community.entity.User;
import com.lizehao.community.community.util.CommunityConstant;
import com.lizehao.community.community.util.CommunityUtil;
import com.lizehao.community.community.util.MailClient;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
public class UserService implements CommunityConstant {

    @Autowired
    private LoginTicketMapper loginTicketMapper;

    @Autowired
    private UserMapper userMapper;

    //需要给用户发确认邮件
    @Autowired
    private MailClient mailClient;

    //发邮件需要模板引擎
    @Autowired
    private TemplateEngine templateEngine;

    //域名，在配置文件里面配置
    @Value("${community.path.domin}")
    private String domin;

    //项目名
    @Value("${server.servlet.context-path}")
    private String contextPath;

    public User findUserById(int id){
        return userMapper.selectById(id);
    }
    //返回注册信息
    //有错误直接return，不会进入下一个判断
    public Map<String, Object> register(User user){
        Map<String,Object> map = new HashMap<>();

        //判断user是否符合条件，这里直接判断user对象，表单信息Controller层处理
        if(user == null){
            throw new IllegalArgumentException("参数不能为空");
        }
        if(StringUtils.isBlank(user.getUsername())){
            //不是程序的错误，不能抛异常，装到map里面返回给客户端
            map.put("usernameMsg","账号不能为空！");
            return map;
        }
        if(StringUtils.isBlank(user.getPassword())){
            map.put("passwordMsg","密码不能为空！");
            return map;
        }
        if(StringUtils.isBlank(user.getEmail())){
            map.put("emailMsg","邮箱不能为空！");
            return map;
        }

        //检查账号是否存在
        User user1 = userMapper.selectByName(user.getUsername());
        if(user1!=null){
            map.put("usernameMsg","账号已经存在");
            return map;
        }

        //验证邮箱
        //不能用user1调用方法，可能会有空指针异常
        user1 = userMapper.selectByEmail(user.getEmail());
        if(user1 != null){
            map.put("emailMsg","邮箱已经存在");
            return map;
        }

        //注册用户，密码加密(5位salt）
        user.setSalt(CommunityUtil.generateUUID().substring(0,5));

        user.setPassword(CommunityUtil.md5(user.getPassword() + user.getSalt()));
        user.setType(0);
        user.setStatus(0);
        user.setActivationCode(CommunityUtil.generateUUID());
        //随机头像
        user.setHeaderUrl(String.format("http://images.nowcoder.com/head/%dt.png",new Random().nextInt(1000)));
        user.setCreateTime(new Date());
        userMapper.insertUser(user);


        //激活邮件
        //给用户发过去一个链接，这个链接包含了刚才随机生成的激活码
        //然后用一个Controller接收这个链接，如果激活码正确，就激活成功
        Context context = new Context();
        context.setVariable("email",user.getEmail());

        //http://localhost:8080/community/activation/101/code
        String url = domin + contextPath + "/activation/" + user.getId() + "/" + user.getActivationCode();
        context.setVariable("url",url);
        String content = templateEngine.process("/mail/activation",context);
        mailClient.sendMail(user.getEmail(),"激活邮件",content);

        return map;
    }
    /**
     * 检查激活码是否正确
     * 1.成功
     * 2.重复激活
     * 3.激活码不正确
     */
    public int activation(int userId,String code){
        User user = userMapper.selectById(userId);
        if(user.getStatus()==1){
            return ACTIVATION_REPEAT;
        } else if(user.getActivationCode().equals(code)){
            userMapper.updateStatus(userId,1);
            return ACTIVATION_SUCCESS;
        }else {
            return ACTIVATION_FAILURE;
        }
    }

    /**
     * 和注册一样，返回一个Map，返回多种结果
     * 用户传进来的是没有加密的密码，和数据库里的密码不一样，所以也需要加密一次再对比
     * 凭证过期时间
     */
    public Map<String, Object> login(String username, String password, int expiredSeconds){
        Map<String, Object> map = new HashMap<>();

        //空值处理
        if(StringUtils.isBlank(username)){
            map.put("usernameMsg","账号不能为空");
            return map;
        }
        if(StringUtils.isBlank(password)){
            map.put("passwordMsg","密码不能为空");
            return map;
        }

        //验证账号
        User user = userMapper.selectByName(username);

        if(user==null){
            map.put("usernameMsg","账号不存在");
            return map;
        }

        //看有没有激活
        if(user.getStatus()==0){
            map.put("usernameMsg","账号未激活");
            return map;
        }

        //验证密码
        password = CommunityUtil.md5(password + user.getSalt());
        //只要是引用类型，哪怕是String，都用equals来比较,因为可能有不知道的地方用了new String导致==出错
        if(!password.equals(user.getPassword())){
            map.put("passwordMsg","密码不正确");
            return map;
        }

        //以上都通过说明账号密码正确，现在要生成登录凭证
        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setUserId(user.getId());
        loginTicket.setTicket(CommunityUtil.generateUUID());
        loginTicket.setStatus(0);
        loginTicket.setExpired(new Date(System.currentTimeMillis()+ expiredSeconds * 1000));
        loginTicketMapper.insertLoginTicket(loginTicket);

        map.put("ticket",loginTicket.getTicket());

        return map;

    }

    //UUID具有唯一性(MAC地址、时间等元素保证唯一性），所以两个用户不会有同一个ticket
    public void logout(String ticket){
        loginTicketMapper.updateStatus(ticket,1);
    }


    //通过凭证查询用户
    public LoginTicket findLoginTicket(String ticket){
        return  loginTicketMapper.selectByTicket(ticket);
    }

    public int updateHeader(int userId, String headerUrl){
        return userMapper.updateHeader(userId, headerUrl);
    }

    //更改密码
    public int updatePassword(int userId, String password) {
        return userMapper.updatePassword(userId, password);

    }
}
