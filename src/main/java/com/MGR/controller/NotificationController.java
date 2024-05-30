package com.MGR.controller;

import com.MGR.entity.Notification;
import com.MGR.security.PrincipalDetails;
import com.MGR.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Map;
import java.util.List;
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

    @GetMapping("/api/notifications")
    public List<Notification> getNotification(@AuthenticationPrincipal PrincipalDetails member){
        Long userId = member.getId();
        return notificationService.findByMemberId(userId);
    }

    @DeleteMapping("/api/notifications/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteNotification(@PathVariable Long id) {
        notificationService.deleteNotification(id);
    }
}
