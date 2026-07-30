package com.tf.sc.dto.response;

import lombok.Data;

@Data
public class ConversationResponse {
    private Long peerUserId;
    private String peerUsername;
    private String peerNickname;
    private String peerAvatar;
    private String peerEmployeeNo;
    private Long lastMessageId;
    private String lastContent;
    private Integer lastType;
    private Integer lastIsRead;
    private String lastCreatedAt;
    private long unreadCount;
}
