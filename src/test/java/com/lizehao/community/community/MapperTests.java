package com.lizehao.community.community;

import com.lizehao.community.community.dao.DiscussPostMapper;
import com.lizehao.community.community.dao.LoginTicketMapper;
import com.lizehao.community.community.dao.MessageMapper;
import com.lizehao.community.community.dao.UserMapper;
import com.lizehao.community.community.entity.DiscussPost;
import com.lizehao.community.community.entity.LoginTicket;
import com.lizehao.community.community.entity.Message;
import com.lizehao.community.community.entity.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class MapperTests {

    @Autowired
    private MessageMapper messageMapper;

    @Autowired
    private LoginTicketMapper loginTicketMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private DiscussPostMapper discussPostMapper;

    @Test
    public void testSelectUser(){
        User user = userMapper.selectById(101);
        System.out.println(user);

        user = userMapper.selectByName("liubei");
        System.out.println(user);

        user = userMapper.selectByEmail("nowcoder101@sina.com");
        System.out.println(user);
    }
    @Test
    public void testInnseryUser(){
        //增加的话要实例化一个User对象给usermapper里面的insertUser(User user);
        //id会自动生成
        User user = new User();
        user.setCreateTime(new Date());
        user.setEmail("testsdfsdfsf@qq.com");
        user.setHeaderUrl("http://www.nowcoder.com/101.png");
        user.setPassword("12345");
        user.setSalt("sdf");
        user.setUsername("test");

        int rows = userMapper.insertUser(user);
        System.out.println(rows);
        System.out.println(user.getId());

    }

    @Test
    public  void updateUser(){
        int rows = userMapper.updateStatus(150,1);
        System.out.println(rows);

        rows = userMapper.updateHeader(150,"http://www.nowcoder.com/102.png");
        System.out.println(rows);
    }
    @Test
    public void testSelectPosts(){
        List<DiscussPost> list = discussPostMapper.selectDiscussPosts(149,0,10);
        for(DiscussPost post: list){
            System.out.println(post);
        }
        int rows = discussPostMapper.selectDiscussPostRows(149);
        System.out.println(rows);
    }

    @Test
    public void testInsertLoginTicket(){
        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setUserId(12);
        loginTicket.setStatus(1);
        loginTicket.setTicket("qwerer");
        //10分钟
        loginTicket.setExpired(new Date(System.currentTimeMillis() + 1000 * 60 *10));

        loginTicketMapper.insertLoginTicket(loginTicket);
    }

    @Test
    public void testSelectLoginTicket(){
        LoginTicket loginTicket = loginTicketMapper.selectByTicket("abc");
        System.out.println(loginTicket);
        loginTicketMapper.updateStatus("abc",0);
    }

    @Test
    public void testSelectLetters() {
        //会话列表，最新一条消息
        List<Message> list = messageMapper.selectConversation(111,0,20);
        for(Message message : list) {
            System.out.println(message);
        }

        //会话数量
        int conversationCount = messageMapper.selectConversationCount(111);
        System.out.println(conversationCount);

        //某个会话的所有私信
        list = messageMapper.selectLetters("111-112",0,20);
        for(Message message : list) {
            System.out.println(message);
        }

        //某个会话的私信数量
        int count = messageMapper.selectLetterCount("111-112");
        System.out.println(count);

        //未读消息数量
        int unRead  = messageMapper.selectLetterUnreadCount(111,"111-112");
        System.out.println(unRead);





    }


}