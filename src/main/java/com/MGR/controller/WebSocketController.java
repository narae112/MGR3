//package com.MGR.controller;
//
//import com.MGR.dto.ChatMessage;
//import com.MGR.dto.OutputMessage;
//import jakarta.servlet.http.HttpSession;
//import org.springframework.messaging.Message;
//import org.springframework.messaging.handler.annotation.MessageMapping;
//import org.springframework.messaging.handler.annotation.SendTo;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.web.bind.annotation.GetMapping;
//
//import java.util.Date;
//
//@Controller
//public class WebSocketController {
//
//    @GetMapping("/chatRoom")
//    public String chatRoom(Model model, HttpSession session) {
//        model.addAttribute("member",session.getAttribute("member"));
//        return "/chatRoom";
//    }
//
//    @MessageMapping("/chat")
//    @SendTo("/topic/messages")
//    public OutputMessage send(ChatMessage message) throws Exception {
//        return new OutputMessage(message.getFrom(), message.getText(), new Date());
//    }
//}
