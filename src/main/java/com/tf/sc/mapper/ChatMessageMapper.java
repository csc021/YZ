package com.tf.sc.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tf.sc.entity.ChatMessage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ChatMessageMapper extends BaseMapper<ChatMessage> {
    List<ChatMessage> selectChatHistory(@Param("senderId") Long senderId, @Param("receiverId") Long receiverId);

    int deleteBatchMsgIds(@Param("ids") List<Long> ids);
}
