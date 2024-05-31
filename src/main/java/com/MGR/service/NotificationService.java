package com.MGR.service;

import com.MGR.controller.NotificationController;
import com.MGR.entity.*;
import com.MGR.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


@Service
@RequiredArgsConstructor
public class NotificationService {

    private final MemberService memberService;
    private final NotificationRepository notificationRepository;
    public final Map<Long, SseEmitter> sseEmitters = new ConcurrentHashMap<>();
    // 1. 모든 Emitters를 저장하는 ConcurrentHashMap

    // 메시지 알림
    public SseEmitter subscribe(Long memberId) {

        // 1. 현재 클라이언트를 위한 sseEmitter 객체 생성
        SseEmitter sseEmitter = new SseEmitter(Long.MAX_VALUE);

        // 2. 연결 확인 메세지
//        try {
//            sseEmitter.send(SseEmitter.event()
//                    .name("connect")
//                    .data("연결성공"));
//        } catch (IOException e) {
//            System.out.println("연결 에러= " + e.getMessage());
//        }

        // 3. 저장
        sseEmitters.put(memberId, sseEmitter);

        // 4. 연결 종료 처리
        sseEmitter.onCompletion(() -> sseEmitters.remove(memberId));    // sseEmitter 연결이 완료될 경우
        sseEmitter.onTimeout(() -> sseEmitters.remove(memberId));        // sseEmitter 연결에 타임아웃이 발생할 경우
        sseEmitter.onError((e) -> sseEmitters.remove(memberId));        // sseEmitter 연결에 오류가 발생할 경우

        return sseEmitter;
    }


    // 이벤트 등록 알림
    @Transactional
    public void notifyBoard(EventBoard board) {
        // role = ROLE_USER 만 조회
        List<Member> memberList = memberService.findByAllUser();

        for (Member member : memberList) {

            subscribe(member.getId()); //객체 생성해서 멤버 아이디 넣어줌

            // SseEmitter 객체 가져오기
            SseEmitter sseEmitterReceiver = sseEmitters.get(member.getId());
            System.out.println("sseEmitterReceiver = " + sseEmitterReceiver);
            if (sseEmitterReceiver != null) {
                try {
                    sseEmitterReceiver.send(SseEmitter.event()
                            .name("message")
                            .data(board.getTitle()));
                } catch (Exception e) {
                    sseEmitters.remove(member.getId());
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
    public void notifyCoupon(Coupon coupon, MemberCoupon memberCoupon, Member member) {
        subscribe(member.getId());
        //알림메세지 생성
        String data = "쿠폰이 발행되었습니다! " +
                coupon.getName() + " 쿠폰 코드 : "
                + memberCoupon.getCouponCode();

            SseEmitter sseEmitter = sseEmitters.get(member.getId());
            if (sseEmitter != null) {
                try {
                    sseEmitter.send(SseEmitter.event() //sseEmitter 객체에 메세지 담아서 보내기
                            .name("message")
                            .data(data));
                } catch (Exception e) {
                    sseEmitters.remove(member.getId());
                }
                Notification notification = new Notification(member.getId(), data, "쿠폰", coupon.getId());
                notificationRepository.save(notification); // 보낸 메세지 저장
            }

    }

    public List<Notification> findByMemberId(Long userId) {
        return notificationRepository.findByMemberId(userId);
    }

    public void deleteNotification(Long id) {
        notificationRepository.deleteById(id);
    }

    public int countNotificationsForMember(Long memberId){
        //알림이 등록된 수 반환
        List<Notification> byMemberId = notificationRepository.findByMemberId(memberId);
        return byMemberId.size();
    }
}

