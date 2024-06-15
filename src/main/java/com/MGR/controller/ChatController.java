package com.MGR.controller;

import com.MGR.entity.Chat;
import com.MGR.dto.ChatMessage;
import com.MGR.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;
    private static final Logger logger = LoggerFactory.getLogger(ChatController.class);

    @MessageMapping("/{roomId}") //여기로 전송되면 메서드 호출 -> WebSocketConfig prefixes 에서 적용한건 앞에 생략
    @SendTo("/room/{roomId}")   //구독하고 있는 장소로 메시지 전송 (목적지)  -> WebSocketConfig Broker 에서 적용한건 앞에 붙어줘야됨
    public ChatMessage chat(@DestinationVariable Long roomId, ChatMessage message) {
        logger.info("Received message: " + message.getMessage() + " from: " + message.getSender());
        try {
            // 채팅 저장
            Chat chat = chatService.createChat(roomId, message.getSender(), message.getSenderEmail(), message.getMessage());
            return ChatMessage.builder()
                    .roomId(roomId)
                    .sender(chat.getSender())
                    .senderEmail(chat.getSenderEmail())
                    .message(chat.getMessage())
                    .build();
        } catch (Exception e) {
            logger.error("Error processing message", e);
            throw new RuntimeException("Error processing message", e);
        }
    }

    @GetMapping("/ws/chats/{roomId}")
    public List<Chat> getChats(@PathVariable Long roomId) {
        try {
            logger.info("Fetching chats for room ID: " + roomId);
            List<Chat> chatList = chatService.findAllChatByRoomId(roomId);
            logger.info("Number of chats fetched: " + chatList.size());
            return chatList;
        } catch (Exception e) {
            logger.error("Error fetching chats for room ID: " + roomId, e);
            throw new RuntimeException("Error fetching chats for room ID: " + roomId, e);
        }
    }
}
