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
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class ChatRoomController {
    private final ChatService chatService;
    private final MemberService memberService;

    @GetMapping("/chat/chatList")
    public String roomList(Model model, @AuthenticationPrincipal PrincipalDetails member) {
        Long memberId = member.getId();
        List<ChatRoom> roomList = chatService.findAllRoomsByMember(memberId);
        Member findMember = memberService.findById(member.getId()).orElseThrow();
        List<Chat> allChatList = chatService.findAllGlobalChats(); // 전체 채팅 메시지 가져오기

        ChatRoom groupChat = chatService.findRoomById(1L);
        model.addAttribute("groupChat",groupChat);

        model.addAttribute("roomList", roomList);
        model.addAttribute("profileImgUrl", findMember.getProfileImgUrl());
        model.addAttribute("nickname", findMember.getNickname());
        model.addAttribute("member", findMember);
        model.addAttribute("allChatList", allChatList);
        return "chat/chatList";
    }

    @GetMapping("/chat/createRoom")
    public String roomForm() {
        return "chat/roomForm";
    }

    @PostMapping("/chat/createRoom")
    @ResponseBody
    public ResponseEntity<?> createRoom(@RequestParam String name, @RequestParam String nickname,
                                        @AuthenticationPrincipal PrincipalDetails member) {
        try {
            chatService.createRoom(name, member, nickname);
            System.out.println("name = " + name);
            System.out.println("nickname = " + nickname);
            return ResponseEntity.ok().body("새로운 채팅방이 생성되었습니다");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("일치하는 닉네임이 없습니다");
        }
    }

    @PostMapping("/chat/deleteRoom")
    public ResponseEntity<?> deleteRoom(@RequestBody Map<String, Long> request,
                                        @AuthenticationPrincipal PrincipalDetails member) {
        Long roomId = request.get("roomId");
        Long memberId = member.getId();

        chatService.deleteChatRoomMemberId(roomId, memberId);

        return ResponseEntity.ok().body("채팅방이 삭제되었습니다.");
    }

    @GetMapping("/chat/joinRoom/{roomId}")
    public String joinRoom(@PathVariable Long roomId, Model model) {
        List<Chat> chatList = chatService.findAllChatByRoomId(roomId);

        model.addAttribute("roomId", roomId);
        model.addAttribute("chatList", chatList);
        return "chat/chatList";
    }


}