package com.MGR.controller;

import com.MGR.entity.Chat;
import com.MGR.entity.ChatRoom;
import com.MGR.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class ChatRoomController {
    private final ChatService chatService;

    @GetMapping("/ws/chatList")
    public String roomList(Model model) {
        List<ChatRoom> roomList = chatService.findAllRoom();
        model.addAttribute("roomList", roomList);
        return "ws/chatList";
    }

    @GetMapping("/ws/createRoom")
    public String roomForm() {
        return "ws/roomForm";
    }

    @PostMapping("/ws/createRoom")
    public String createRoom(@RequestParam String name, @RequestParam List<String> memberEmails) {
        chatService.createRoom(name, memberEmails);
        System.out.println("name = " + name);
        System.out.println("memberEmails = " + memberEmails.toString());
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
