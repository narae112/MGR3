package com.MGR.controller;

import com.MGR.security.PrincipalDetails;
import com.MGR.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.net.URI;
import java.util.concurrent.CompletableFuture;

@RestController
@RequiredArgsConstructor
public class NotificationController {
    private final NotificationService notificationService;

    // 메시지 알림
    @GetMapping(value = "/api/notifications/subscribe", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter subscribe(@AuthenticationPrincipal PrincipalDetails member) {
        if(member == null){
            return null;
        }
        Long userId = member.getId();
        return notificationService.subscribe(userId);
    }

    @GetMapping("/api/notifications")
    public ResponseEntity<?> getNotification(@AuthenticationPrincipal PrincipalDetails member){
        if(member != null) {
            Long userId = member.getId();
            System.out.println(notificationService.findByMemberId(userId).toString());
            return ResponseEntity.ok(notificationService.findByMemberId(userId));
        }
        return ResponseEntity.status(HttpStatus.FOUND).location(URI.create("/")).build();
    }

    @DeleteMapping("/api/notifications/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteNotification(@PathVariable Long id) {
        notificationService.deleteNotification(id);
    }

    @GetMapping("/api/notifications/count")
    public CompletableFuture<ResponseEntity<Integer>> getNotificationCount(@AuthenticationPrincipal PrincipalDetails member) {
        if (member == null) {
            // 사용자가 로그인하지 않은 경우
            return CompletableFuture.completedFuture(new ResponseEntity<>(HttpStatus.UNAUTHORIZED));
        }

        Long memberId = member.getId();
        return notificationService.countNotificationsForMemberAsync(memberId)
                .thenApply(notificationCount -> new ResponseEntity<>(notificationCount, HttpStatus.OK));
    }

}
