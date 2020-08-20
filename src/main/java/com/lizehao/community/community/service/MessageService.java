package com.lizehao.community.community.service;

import com.lizehao.community.community.dao.MessageMapper;
import com.lizehao.community.community.entity.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MessageService {
    @Autowired
    private MessageMapper messageMapper;

    public List<Message> selectConversations(int userId, int offset, int limit) {
        return messageMapper.selectConversation(userId, offset, limit);
    }

    public int countConversation (int userId) {
        return messageMapper.selectConversationCount(userId);
    }

    public List<Message> selectLetters (String conversationId, int offset, int limit) {
        return messageMapper.selectLetters(conversationId, offset, limit);
    }

    public int countLetters(String conversationId) {
        return messageMapper.selectLetterCount(conversationId);
    }

    public int unRead(int userId, String conversationId) {
        return messageMapper.selectLetterUnreadCount(userId, conversationId);
    }
}

