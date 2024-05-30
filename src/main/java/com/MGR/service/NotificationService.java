package com.MGR.service;

import com.MGR.controller.NotificationController;
import com.MGR.entity.*;
import com.MGR.repository.EventBoardRepository;
import com.MGR.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class NotificationService {

    private final MemberService memberService;
    private final NotificationRepository notificationRepository;

    // 메시지 알림
    public SseEmitter subscribe(Long memberId) {

        // 1. 현재 클라이언트를 위한 sseEmitter 객체 생성
        SseEmitter sseEmitter = new SseEmitter(Long.MAX_VALUE);

        // 2. 연결
//        try {
//            sseEmitter.send(SseEmitter.event()
//                    .name("connect")
//                    .data("연결성공"));
//        } catch (IOException e) {
//            System.out.println("연결 에러= " + e.getMessage());
//        }

        // 3. 저장
        NotificationController.sseEmitters.put(memberId, sseEmitter);

        // 4. 연결 종료 처리
        sseEmitter.onCompletion(() -> NotificationController.sseEmitters.remove(memberId));    // sseEmitter 연결이 완료될 경우
        sseEmitter.onTimeout(() -> NotificationController.sseEmitters.remove(memberId));        // sseEmitter 연결에 타임아웃이 발생할 경우
        sseEmitter.onError((e) -> NotificationController.sseEmitters.remove(memberId));        // sseEmitter 연결에 오류가 발생할 경우

        return sseEmitter;
    }


    // 이벤트 등록 알림
    @Transactional
    public void notifyBoard(EventBoard board) {
        // role = ROLE_USER 만 조회
        List<Member> memberList = memberService.findByAllUser();

        for (Member member : memberList) {

            // SseEmitter 객체 가져오기
            SseEmitter sseEmitterReceiver = NotificationController.sseEmitters.get(member.getId());
            System.out.println("sseEmitterReceiver = " + sseEmitterReceiver);
            if (sseEmitterReceiver != null) {
                try {
                    sseEmitterReceiver.send(SseEmitter.event()
                            .name("message")
                            .data(board.getTitle()));
                } catch (Exception e) {
                    NotificationController.sseEmitters.remove(member.getId());
                }

                // 알림 객체 생성 및 저장
                Notification notification =
                        new Notification(member.getId(), board.getTitle(), "이벤트", board.getId());
                notificationRepository.save(notification);
            }
        }
    }

    // 쿠폰 등록 알림
    @Transactional
    public void notifyCoupon(Coupon coupon) {
        // role = ROLE_USER 만 조회
        List<Member> memberList = memberService.findByAllUser();
        String data = "쿠폰이 발행되었습니다\n" +
                "쿠폰 : " + coupon.getName() + "\n" +
                "쿠폰 코드 : " + coupon.getCouponContent();
        for (Member member : memberList) {

            // SseEmitter 객체 가져오기
            SseEmitter sseEmitterReceiver = NotificationController.sseEmitters.get(member.getId());
            System.out.println("sseEmitterReceiver = " + sseEmitterReceiver);
            if (sseEmitterReceiver != null) {
                try {
                    sseEmitterReceiver.send(SseEmitter.event()
                            .name("message")
                            .data(data));
                } catch (Exception e) {
                    NotificationController.sseEmitters.remove(member.getId());
                }

                // 알림 객체 생성 및 저장
                Notification notification =
                        new Notification(member.getId(), data, "쿠폰", coupon.getId());
                notificationRepository.save(notification);
            }
        }
    }

    public List<Notification> findByMemberId(Long userId) {
        return notificationRepository.findByMemberId(userId);
    }

    public void deleteNotification(Long id) {
        notificationRepository.deleteById(id);
    }
}

