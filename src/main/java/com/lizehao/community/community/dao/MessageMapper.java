package com.lizehao.community.community.dao;

import com.lizehao.community.community.entity.Message;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface MessageMapper {
    //查询当前用户的会话列表，每个会话只显示最新的消息（微信）
    //私信列表需要分页，所以需要传offset和limit
    List<Message> selectConversation(int userId, int offset, int limit);

    //查询当前用户的会话数量
    int selectConversationCount(int userId);

    //查询某个会话包含的私信列表
    List<Message> selectLetters(String conversationId, int offset, int limit);

    //查询某个会话包含的私信数量
    int selectLetterCount(String conversationId);

    //查询未读私信的数量
    //根据是否有conversation参数，可以查总未读数量，和每个会话的未读数量（微信图标的红点数和每个聊天的未读数）
    int selectLetterUnreadCount(int userId, String conversationId);

    //添加私信
    int insertMessage(Message message);
}
