package com.MGR.controller;

import com.MGR.entity.Chat;
import com.MGR.entity.ChatRoom;
import com.MGR.entity.Member;
import com.MGR.security.PrincipalDetails;
import com.MGR.service.ChatService;
import com.MGR.service.MemberService;
import com.MGR.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class ChatController {
    private final NotificationService notificationService;
    private final ChatService chatService;
    private final MemberService memberService;

    @PostMapping("/api/chat/send")
    public ResponseEntity<Void> sendMessage(
            @RequestParam Long roomId,
            @RequestParam String message,
            @AuthenticationPrincipal PrincipalDetails member) {

        if (roomId == null) {
            roomId = 1L; // 전체 채팅방 ID를 1로 설정
        }

        Member sender = memberService.findById(member.getId()).orElseThrow();
        ChatRoom chatRoom = chatService.findRoomById(roomId);
        Member receiver;

        if (sender.equals(chatRoom.getSender())) {
            receiver = chatRoom.getReceiver();
        } else {
            receiver = chatRoom.getSender();
        }
        chatService.createChat(roomId, sender.getNickname(), sender.getEmail(), message);
        notificationService.sendMessage(roomId, sender.getId(), message);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping(value = "/api/chat/subscribe", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter subscribe(@AuthenticationPrincipal PrincipalDetails member, @RequestParam Long roomId) {
        if (member == null) {
            return null;
        }
        Long userId = member.getId();
        return notificationService.subscribeToRoom(userId, roomId);
    }

    @GetMapping("/api/chats/{roomId}")
    public ResponseEntity<List<Chat>> getChatHistory(@PathVariable Long roomId) {
        List<Chat> chatList = chatService.findAllChatByRoomId(roomId);
        return new ResponseEntity<>(chatList, HttpStatus.OK);
    }

//    private final ChatService chatService;
//    private static final Logger logger = LoggerFactory.getLogger(ChatController.class);
//
//    @MessageMapping("/{roomId}")
//    @SendTo("/room/{roomId}")
//    public ChatMessage chat(@DestinationVariable Long roomId, ChatMessage message) {
//        logger.info("Received message: " + message.getMessage() + " from: " + message.getSender());
//        try {
//            Chat chat = chatService.createChat(roomId, message.getSender(), message.getSenderEmail(), message.getMessage());
//            return ChatMessage.builder()
//                    .roomId(roomId)
//                    .sender(chat.getSender())
//                    .senderEmail(chat.getSenderEmail())
//                    .message(chat.getMessage())
//                    .build();
//        } catch (Exception e) {
//            logger.error("Error processing message", e);
//            throw new RuntimeException("Error processing message", e);
//        }
//    }
//
//    @GetMapping("/ws/chats/{roomId}")
//    public List<Chat> getChats(@PathVariable Long roomId) {
//        try {
//            logger.info("Fetching chats for room ID: " + roomId);
//            List<Chat> chatList = chatService.findAllChatByRoomId(roomId);
//            logger.info("Number of chats fetched: " + chatList.size());
//            return chatList;
//        } catch (Exception e) {
//            logger.error("Error fetching chats for room ID: " + roomId, e);
//            throw new RuntimeException("Error fetching chats for room ID: " + roomId, e);
//        }
//    }
}

