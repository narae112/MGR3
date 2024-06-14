package com.MGR.controller;

import com.MGR.entity.Chat;
import com.MGR.entity.ChatRoom;
import com.MGR.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class ChatRoomController {
    private final ChatService chatService;

    /**
     * 채팅방 참여하기
     * @param roomId 채팅방 id
     */
//    @GetMapping("/ws/{roomId}")
//    public String joinRoom(@PathVariable(required = false) Long roomId, Model model) {
//        List<Chat> chatList = chatService.findAllChatByRoomId(roomId);
//
//        model.addAttribute("roomId", roomId);
//        model.addAttribute("chatList", chatList);
//        return "ws/chatList";
//    }

    @GetMapping("/chat/{roomId}")
    public String joinRoom(@PathVariable Long roomId, Model model) {
        List<Chat> chatList = chatService.findAllChatByRoomId(roomId);

        model.addAttribute("roomId", roomId);
        model.addAttribute("chatList", chatList);
        return "ws/chatList";
    }

    /**
     * 채팅방 리스트 보기
     */
    @GetMapping("/ws/chatList")
    public String roomList(Model model) {
        List<ChatRoom> roomList = chatService.findAllRoom();
        model.addAttribute("roomList", roomList);
        return "ws/chatList";
    }

    /**
     * 방만들기 폼
     */
    @GetMapping("/ws/roomForm")
    public String roomForm2() {
        return "ws/chatList";
    }

    @GetMapping("/ws/createRoom")
    public String roomForm() {
        return "ws/roomForm";
    }

    @PostMapping("/ws/createRoom")
    public String createRoom(@RequestParam String name) {
        chatService.createRoom(name);
        return "redirect:/ws/chatList";
    }

}