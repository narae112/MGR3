package com.MGR.service;

import com.MGR.controller.NotificationController;
import com.MGR.entity.Member;
import com.MGR.entity.Notification;
import com.MGR.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


@Service
@RequiredArgsConstructor
public class NotificationService {

    private final MemberService memberService;
    private final NotificationRepository notificationRepository;
    private final Map<Long, SseEmitter> sseEmitters = new ConcurrentHashMap<>();

    // 메시지 알림
    public SseEmitter subscribe(Long memberId) {

        // 1. 현재 클라이언트를 위한 sseEmitter 객체 생성
        SseEmitter sseEmitter = new SseEmitter(Long.MAX_VALUE);

        // 2. 연결
        try {
            sseEmitter.send(SseEmitter.event()
                    .name("connect")
                    .data("연결성공"));
        } catch (IOException e) {
            System.out.println("연결 에러= " + e.getMessage());
        }

        // 3. 저장
        sseEmitters.put(memberId, sseEmitter);

        // 4. 연결 종료 처리
        sseEmitter.onCompletion(() -> NotificationController.sseEmitters.remove(memberId));    // sseEmitter 연결이 완료될 경우
        sseEmitter.onTimeout(() -> NotificationController.sseEmitters.remove(memberId));        // sseEmitter 연결에 타임아웃이 발생할 경우
        sseEmitter.onError((e) -> NotificationController.sseEmitters.remove(memberId));        // sseEmitter 연결에 오류가 발생할 경우

        return sseEmitter;
    }


    // 이벤트 등록 알림 - 전부
    @Transactional
    public void notifyBoard(String boardTitle) {
        // 5. 모든 멤버 id 조회
        List<Member> memberList = memberService.findByAllMembers();

        for (Member member : memberList) {
            // 6. 모든 멤버에게 보낼 메시지 객체 가져오기
            SseEmitter sseEmitterReceiver = sseEmitters.get(member.getId());
            System.out.println("sseEmitters 값 확인= " + sseEmitters);
            if (sseEmitterReceiver != null) {
                System.out.println("어디서 안되는겨");
                try {
                    sseEmitterReceiver.send(SseEmitter.event()
                            .name("<새로운 이벤트가 등록되었습니다>\n")
                            .data(boardTitle));
                    System.out.println("새로운 이벤트 등록 멘트");
                } catch (Exception e) {
                    NotificationController.sseEmitters.remove(member.getId());
                    System.out.println("어디서 안되는겨2" + e.getMessage());
                }
            }
            Notification notification = new Notification(member.getId(), boardTitle,"이벤트 알림");
            notificationRepository.save(notification);
            System.out.println("알림에러2" + notification);
        }
    }

    public List<Notification> findByMemberId(Long userId) {
        return notificationRepository.findByMemberId(userId);
    }
}

