package com.MGR.controller;

import com.MGR.dto.ChatDTO;
import com.MGR.dto.ChatRoomDTO;
import com.MGR.entity.Chat;
import com.MGR.entity.ChatRoom;
import com.MGR.entity.Member;
import com.MGR.security.PrincipalDetails;
import com.MGR.service.ChatService;
import com.MGR.service.MemberService;
import com.MGR.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
public class ChatController {
    private final NotificationService notificationService;
    private final ChatService chatService;
    private final MemberService memberService;

    @PostMapping("/chat/send")
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
        chatService.createChat(roomId, sender, sender.getEmail(), message, sender.getProfileImgUrl());
        notificationService.sendMessage(roomId, sender.getId(), message);

//        if(!roomId.equals(1L)) {
//            notificationService.sendReadEvent(roomId, receiver.getId());
//        }

        return new ResponseEntity<>(HttpStatus.OK);
    }

//    @GetMapping(value = "/chat/subscribe", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
//    public SseEmitter subscribe(@AuthenticationPrincipal PrincipalDetails member, @RequestParam Long roomId) {
//        if (member == null) {
//            return null;
//        }
//        Long userId = member.getId();
//        return notificationService.subscribeToRoom(userId, roomId);
//    }
//
//    @GetMapping("/chats/{roomId}")
//    public ResponseEntity<Map<String, Object>> getChatHistory(@PathVariable Long roomId, @AuthenticationPrincipal PrincipalDetails member) {
//        List<Chat> chatList = chatService.findAllChatByRoomId(roomId);
//        ChatRoom roomById = chatService.findRoomById(roomId);
//
//        Map<String, Object> response = new HashMap<>();
//        response.put("chatList", chatList);
//        response.put("chatRoom", roomById);
//
//        return new ResponseEntity<>(response, HttpStatus.OK);
//    }

    @GetMapping(value = "/chat/subscribe", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter subscribe(@AuthenticationPrincipal PrincipalDetails member, @RequestParam Long roomId) {
        if (member == null) {
            return null;
        }
        Long userId = member.getId();
        return notificationService.subscribeToRoom(userId, roomId);
    }

    @GetMapping("/chats/{roomId}")
    public ResponseEntity<Map<String, Object>> getChatHistory(@PathVariable Long roomId, @AuthenticationPrincipal PrincipalDetails member) {
        List<Chat> chatList = chatService.findAllChatByRoomId(roomId);
        ChatRoom roomById = chatService.findRoomById(roomId);

        List<ChatDTO> chatDTOList = chatList.stream()
                .map(chatService::convertToChatDTO)
                .collect(Collectors.toList());

        ChatRoomDTO chatRoomDTO = chatService.convertToChatRoomDTO(roomById);

        Map<String, Object> response = new HashMap<>();
        response.put("chatList", chatDTOList);
        response.put("chatRoom", chatRoomDTO);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}