package com.tf.sc.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tf.sc.annotation.RequireRole;
import com.tf.sc.common.Constants;
import com.tf.sc.common.Result;
import com.tf.sc.dto.request.ChatMessageRequest;
import com.tf.sc.dto.response.ConversationResponse;
import com.tf.sc.dto.response.UnreadCountResponse;
import com.tf.sc.entity.ChatMessage;
import com.tf.sc.service.ChatMessageService;
import com.tf.sc.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RequireRole({"0", "1", "2"})
@RestController
@RequestMapping("/chatMessage")
public class ChatController {

    @Autowired
    private ChatMessageService chatMessageService;

    @GetMapping("/page")
    public Result<Page<ChatMessage>> page(@RequestParam(defaultValue = "1") Long pageNum,
                                          @RequestParam(defaultValue = "10") Long pageSize) {
        return Result.success(chatMessageService.page(new Page<>(pageNum, pageSize)));
    }

    @GetMapping("/list")
    public Result<List<ChatMessage>> list() {
        return Result.success(chatMessageService.list());
    }

    @GetMapping("/{id}")
    public Result<ChatMessage> getById(@PathVariable Long id) {
        ChatMessage message = chatMessageService.getById(id);
        return message == null ? Result.error("Message not found") : Result.success(message);
    }

    @PostMapping({"", "/send"})
    public Result<ChatMessage> save(@RequestBody ChatMessageRequest request) {
        Long currentUserId = currentUserId();
        if (currentUserId == null) {
            return Result.error(401, "Unauthorized");
        }
        request.setSenderId(currentUserId);
        return Result.success(chatMessageService.send(request));
    }

    @PutMapping
    public Result<Boolean> update(@RequestBody ChatMessage chatMessage) {
        return Result.success(chatMessageService.updateById(chatMessage));
    }

    @DeleteMapping("/{id}")
    public Result<Boolean> remove(@PathVariable Long id) {
        return Result.success(chatMessageService.removeById(id));
    }

    @GetMapping("/conversations")
    public Result<List<ConversationResponse>> conversations() {
        return Result.success(chatMessageService.conversations(currentUserId()));
    }

    @GetMapping("/history")
    public Result<List<ChatMessage>> history(@RequestParam Long senderId, @RequestParam Long receiverId) {
        Long currentUserId = currentUserId();
        if (currentUserId == null || (!currentUserId.equals(senderId) && !currentUserId.equals(receiverId))) {
            return Result.error(403, "Forbidden");
        }
        return Result.success(chatMessageService.history(senderId, receiverId));
    }

    @GetMapping("/unread/{userId}")
    public Result<UnreadCountResponse> unreadCount(@PathVariable Long userId) {
        Long currentUserId = currentUserId();
        if (currentUserId == null || !currentUserId.equals(userId)) {
            return Result.error(403, "Forbidden");
        }
        UnreadCountResponse response = new UnreadCountResponse();
        response.setUserId(userId);
        response.setUnreadCount(chatMessageService.unreadCount(userId));
        return Result.success(response);
    }

    @PostMapping("/read")
    public Result<Boolean> markRead(@RequestParam Long senderId, @RequestParam Long receiverId) {
        Long currentUserId = currentUserId();
        if (currentUserId == null || !currentUserId.equals(receiverId)) {
            return Result.error(403, "Forbidden");
        }
        return Result.success(chatMessageService.markRead(senderId, receiverId));
    }

    private Long currentUserId() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) return null;
        HttpServletRequest request = attributes.getRequest();
        Object value = request.getAttribute("userId");
        if (value instanceof Long) return (Long) value;
        String header = request.getHeader(Constants.AUTH_HEADER);
        if (header == null || !header.startsWith(Constants.JWT_PREFIX)) return null;
        try {
            return Long.valueOf(JwtUtil.parseSubject(header.substring(Constants.JWT_PREFIX.length())));
        } catch (RuntimeException ex) {
            return null;
        }
    }
}
