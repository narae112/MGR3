package com.MGR.controller;

import com.MGR.entity.Chat;
import com.MGR.entity.ChatRoom;
import com.MGR.security.PrincipalDetails;
import com.MGR.service.ChatService;
import com.MGR.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class ChatRoomController {
    private final ChatService chatService;
    private final MemberService memberService;

    @GetMapping("/ws/chatList")
    public String roomList(Model model, @AuthenticationPrincipal PrincipalDetails member) {
        List<ChatRoom> roomList = chatService.findAllRoom();
        String nickname = memberService.findById(member.getId()).orElseThrow().getNickname();
        System.out.println("닉네임 찾기 = " + nickname);
        model.addAttribute("roomList", roomList);
        model.addAttribute("nickname", nickname);
        return "ws/chatList";
    }

    @GetMapping("/ws/createRoom")
    public String roomForm() {
        return "ws/roomForm";
    }

    @PostMapping("/ws/createRoom")
    public String createRoom(@RequestParam String name, @RequestParam String nickname,
                             @AuthenticationPrincipal PrincipalDetails member) {

        chatService.createRoom(name, member, nickname);
        System.out.println("name = " + name);
        System.out.println("nickname = " + nickname);
        return "redirect:/ws/chatList";
    }


    @GetMapping("/ws/joinRoom/{roomId}")
    public String joinRoom(@PathVariable Long roomId, Model model) {
        List<Chat> chatList = chatService.findAllChatByRoomId(roomId);

        model.addAttribute("roomId", roomId);
        model.addAttribute("chatList", chatList);
        return "ws/chatList";
    }
}
