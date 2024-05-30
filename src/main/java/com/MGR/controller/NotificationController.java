package com.MGR.controller;

import com.MGR.security.PrincipalDetails;
import com.MGR.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequiredArgsConstructor
public class NotificationController {
    private final NotificationService notificationService;
    public static Map<Long, SseEmitter> sseEmitters = new ConcurrentHashMap<>();
    // 1. 모든 Emitters를 저장하는 ConcurrentHashMap

    // 메시지 알림
    @GetMapping(value = "/api", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter subscribe(@AuthenticationPrincipal PrincipalDetails member) {

        Long userId = member.getId();

        return notificationService.subscribe(userId);
    }
}

// 메시지 알림
//@GetMapping("/api/notification/subscribe")
//public SseEmitter subscribe(@AuthenticationPrincipal PrincipalDetails member) {
//
//    Long userId = member.getId(); //현재 로그인한 멤버의 아이디를 찾아서
//
//    //알림을 보냄
//    return notificationService.subscribe(userId);
//}

//        @GetMapping("/chat")
//        public ResponseEntity<?> gemini(@RequestParam String message) {
//            try {
//                String response = geminiService.getContents(message);
//                return ResponseEntity.ok().body(response);
//            } catch (HttpClientErrorException e) {
//                return ResponseEntity.badRequest().body(e.getMessage());
//            }
//        }
//    }
//}
