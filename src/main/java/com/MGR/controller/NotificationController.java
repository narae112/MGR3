package com.MGR.controller;

import com.MGR.entity.Notification;
import com.MGR.security.PrincipalDetails;
import com.MGR.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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

    // 메시지 알림
    @GetMapping(value = "/api", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter subscribe(@AuthenticationPrincipal PrincipalDetails member) {
        Long userId = member.getId();
        return notificationService.subscribe(userId);
    }

    @GetMapping("/api/notifications")
    public List<Notification> getNotification(@AuthenticationPrincipal PrincipalDetails member){
        Long userId = member.getId();
        System.out.println(notificationService.findByMemberId(userId).toString());
        return notificationService.findByMemberId(userId);
    }

    @DeleteMapping("/api/notifications/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteNotification(@PathVariable Long id) {
        notificationService.deleteNotification(id);
    }

    @GetMapping("/api/notifications/count")
    public ResponseEntity<Integer> getNotificationCount(@AuthenticationPrincipal PrincipalDetails member) {
        if (member == null) {
            // 사용자가 로그인하지 않은 경우
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        Long memberId = member.getId();
        int notificationCount = notificationService.countNotificationsForMember(memberId);

        return new ResponseEntity<>(notificationCount, HttpStatus.OK);
    }
}
