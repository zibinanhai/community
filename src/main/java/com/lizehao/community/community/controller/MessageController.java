package com.lizehao.community.community.controller;

import com.lizehao.community.community.entity.Message;
import com.lizehao.community.community.entity.Page;
import com.lizehao.community.community.entity.User;
import com.lizehao.community.community.service.MessageService;
import com.lizehao.community.community.service.UserService;
import com.lizehao.community.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class MessageController {

    @Autowired
    private MessageService messageService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private UserService userService;

    //私信列表
    @RequestMapping(path = "/letter/list", method = RequestMethod.GET)
    public String getLetterList(Model model, Page page) {

        User user = hostHolder.getUser();
        if(user == null ) {

        }

        //设置分页信息
        page.setLimit(5);
        page.setPath("/letter/list");
        page.setRows(messageService.countConversation(user.getId()));

        //会话列表
        List<Message> conversationList = messageService.selectConversations(user.getId(), page.getOffset(), page.getLimit());

        List<Map<String, Object>> conversation = new ArrayList<>();
        if(conversationList != null ) {
            for (Message message : conversationList) {
                Map<String, Object> map = new HashMap<>();
                //每个会话的最后一条消息
                map.put("conversation", message);
                //每个会话的私信数量
                map.put("letterCount", messageService.countLetters(message.getConversationId()));
                //每个会话的未读数量
                map.put("unReadCount", messageService.unRead(user.getId(),message.getConversationId()));
                //显示与当前用户对话的用户头像(如果是自己发送的，那目标Id就是toId,如果是自己收到的，那目标Id就是fromId）
                int targetId = user.getId() == message.getFromId() ? message.getToId() : message.getFromId();
                map.put("target", userService.findUserById(targetId));

                conversation.add(map);

            }
        }
        model.addAttribute("conversation", conversation);

        //总未读消息数量
        int totalUnreadCount = messageService.unRead(user.getId(),null);
        model.addAttribute("totalUnreadCount", totalUnreadCount);

        return "/site/letter";



    }

    @RequestMapping(path = "/letter/detail/{conversationId}", method = RequestMethod.GET)
    public String letterDetail(@PathVariable("conversationId") String conversationId, Page page, Model model) {

        User user = hostHolder.getUser();

        page.setLimit(10);
        page.setPath("/letter/detail/" + conversationId);
        page.setRows(messageService.countLetters(conversationId));

        List<Message> list =  messageService.selectLetters(conversationId, page.getOffset(), page.getLimit());

        List<Map<String, Object>> letterList = new ArrayList<>();
        if(list != null) {
            for (Message message: list) {
                Map<String, Object> map = new HashMap<>();
                //私信详情里，每条消息只要发送人的用户信息，所以不需要找target
                map.put("letter", message);
                map.put("fromUser", userService.findUserById(message.getFromId()));
                letterList.add(map);
            }

        }
        model.addAttribute("letterList", letterList);
        //在私信详情的顶部显示目标用户的信息
        model.addAttribute("target", getLetterTarget(conversationId));

        return "/site/letter-detail";
    }


    //把会话id转化为两个用户id，然后找到目标用户
    private User getLetterTarget(String conversationId) {
        String[] ids = conversationId.split("_");
        int id1 = Integer.parseInt(ids[0]);
        int id2 = Integer.parseInt(ids[1]);

        if( hostHolder.getUser().getId() == id1) {
            return userService.findUserById(id1);
        }else{
            return userService.findUserById(id2);
        }

    }
}
