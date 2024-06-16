package com.MGR.controller;

import com.MGR.entity.Chat;
import com.MGR.entity.ChatRoom;
import com.MGR.entity.Member;
import com.MGR.security.PrincipalDetails;
import com.MGR.service.ChatService;
import com.MGR.service.MemberService;
import com.MGR.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class ChatRoomController {
    private final ChatService chatService;
    private final MemberService memberService;

    @GetMapping("/api/chatList")
    public String roomList(Model model, @AuthenticationPrincipal PrincipalDetails member) {
        Long memberId = member.getId();
        List<ChatRoom> roomList = chatService.findAllRoomsByMember(memberId);
        Member findMember = memberService.findById(member.getId()).orElseThrow();
        List<Chat> allChatList = chatService.findAllGlobalChats(); // 전체 채팅 메시지 가져오기

        model.addAttribute("roomList", roomList);
        model.addAttribute("profileImgUrl", findMember.getProfileImgUrl());
        model.addAttribute("nickname", findMember.getNickname());
        model.addAttribute("allChatList", allChatList);
        return "api/chatList";
    }

    @GetMapping("/api/createRoom")
    public String roomForm() {
        return "api/roomForm";
    }

    @PostMapping("/api/createRoom")
    @ResponseBody
    public ResponseEntity<?> createRoom(@RequestParam String name, @RequestParam String nickname,
                                        @AuthenticationPrincipal PrincipalDetails member) {
        try {
            chatService.createRoom(name, member, nickname);
            System.out.println("name = " + name);
            System.out.println("nickname = " + nickname);
            return ResponseEntity.ok().body("Room created successfully");
        } catch (Exception e) {
            // 예외 발생 시 실패 메시지 반환
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to create room");
        }
    }

    @GetMapping("/api/joinRoom/{roomId}")
    public String joinRoom(@PathVariable Long roomId, Model model) {
        List<Chat> chatList = chatService.findAllChatByRoomId(roomId);

        model.addAttribute("roomId", roomId);
        model.addAttribute("chatList", chatList);
        return "api/chatList";
    }
}
