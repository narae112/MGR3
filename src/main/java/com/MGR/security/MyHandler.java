package com.MGR.security;

import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

public class MyHandler extends TextWebSocketHandler {

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        // 세션에서 사용자 정보 확인
        Object user = session.getAttributes().get("user");
        if (user != null) {
            System.out.println("사용자 Authenticated user: " + user);
        } else {
            System.out.println("사용자 User not authenticated");
        }
        // 메시지를 처리하고 응답을 보냅니다.
        String payload = message.getPayload();
        System.out.println("Received: " + payload);
        session.sendMessage(new TextMessage("Hello, " + payload + "!"));
    }
}
