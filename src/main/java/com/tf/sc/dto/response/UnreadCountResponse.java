package com.tf.sc.dto.response;

import lombok.Data;

@Data
public class UnreadCountResponse {
    private Long userId;
    private long unreadCount;
}
