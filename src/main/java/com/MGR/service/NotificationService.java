package com.MGR.service;

import com.MGR.entity.*;
import com.MGR.repository.NotificationRepository;
import com.MGR.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;


@Service
@RequiredArgsConstructor
public class NotificationService {
    private final OrderRepository orderRepository;
    private final MemberService memberService;
    private final NotificationRepository notificationRepository;
    public final Map<Long, SseEmitter> sseEmitters = new ConcurrentHashMap<>();
    // 1. 모든 Emitters를 저장하는 ConcurrentHashMap

    // 메시지 알림
    public SseEmitter subscribe(Long memberId) {

        // 1. 현재 클라이언트를 위한 sseEmitter 객체 생성
        SseEmitter sseEmitter = new SseEmitter(Long.MAX_VALUE);

        // 2. 연결 확인 메세지
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

            // SseEmitter 객체 가져오기
            SseEmitter sseEmitterReceiver = sseEmitters.get(member.getId());
            System.out.println("sseEmitterReceiver = " + sseEmitterReceiver);
            if (sseEmitterReceiver != null) {
                try {
                    sseEmitterReceiver.send(SseEmitter.event()
                            .name("message")
                            .data(board.getTitle()));

                    int notificationCount = countNotificationsForMember(member.getId());
                    System.out.println("notificationCount 알림수량 = " + notificationCount);
                    sseEmitterReceiver.send(SseEmitter.event()
                            .name("notificationCount")
                            .data(notificationCount));

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

                int notificationCount = countNotificationsForMember(member.getId());
                System.out.println("notificationCount 알림수량 = " + notificationCount);
                sseEmitter.send(SseEmitter.event()
                        .name("notificationCount")
                        .data(notificationCount));

            } catch (Exception e) {
                sseEmitters.remove(member.getId());
            }
            Notification notification = new Notification(member.getId(), data, "쿠폰", coupon.getId());
            notificationRepository.save(notification); // 보낸 메세지 저장
        }

    }

    // 리뷰 추천 알림
    @Transactional
    public void reviewVoter(ReviewBoard board) {
        Member member = board.getAuthor();

        //알림메세지 생성
        String data = "다른 사용자가 다음 리뷰에 추천을 표시했습니다. [" +
                board.getSubject() + "]";
        System.out.println("data = " + data);
        SseEmitter sseEmitter = sseEmitters.get(member.getId());
        System.out.println("sseEmitter = " + sseEmitter);
        if (sseEmitter != null) {
            try {
                sseEmitter.send(SseEmitter.event() //sseEmitter 객체에 메세지 담아서 보내기
                        .name("message")
                        .data(data));

                int notificationCount = countNotificationsForMember(member.getId());
                System.out.println("notificationCount 알림수량 = " + notificationCount);
                sseEmitter.send(SseEmitter.event()
                        .name("notificationCount")
                        .data(notificationCount));

            } catch (Exception e) {
                sseEmitters.remove(member.getId());
            }
            Notification notification = new Notification(member.getId(), data, "리뷰", board.getId());
            notificationRepository.save(notification); // 보낸 메세지 저장
        }
    }

    // 리뷰 댓글 알림
    @Transactional
    public void reviewComment(ReviewBoard board) {
        Member member = board.getAuthor();

        //알림메세지 생성
        String data = "다른 사용자가 다음 리뷰에 댓글을 등록했습니다. [" +
                board.getSubject() + "]";
        System.out.println("data = " + data);
        SseEmitter sseEmitter = sseEmitters.get(member.getId());
        System.out.println("sseEmitter = " + sseEmitter);
        if (sseEmitter != null) {
            try {
                sseEmitter.send(SseEmitter.event() //sseEmitter 객체에 메세지 담아서 보내기
                        .name("message")
                        .data(data));

                int notificationCount = countNotificationsForMember(member.getId());
                System.out.println("notificationCount 알림수량 = " + notificationCount);
                sseEmitter.send(SseEmitter.event()
                        .name("notificationCount")
                        .data(notificationCount));

            } catch (Exception e) {
                sseEmitters.remove(member.getId());
            }
            Notification notification = new Notification(member.getId(), data, "리뷰", board.getId());
            notificationRepository.save(notification); // 보낸 메세지 저장
        }
    }

    // 결제 완료 알림
    @Transactional
    public void notifyOrder(Long id) {
        Optional<Order> order = orderRepository.findById(id);
        Optional<Member> member = memberService.findById(order.get().getMember().getId());
        SseEmitter sseEmitter = sseEmitters.get(member.get().getId());

        String data = "주문 번호 : " + id;
        if (sseEmitter != null) {
            try {
                sseEmitter.send(SseEmitter.event()
                        .name("message")
                        .data(data));

                int notificationCount = countNotificationsForMember(member.get().getId());
                System.out.println("notificationCount 알림수량 = " + notificationCount);
                sseEmitter.send(SseEmitter.event()
                        .name("notificationCount")
                        .data(notificationCount));

            } catch (Exception e) {
                sseEmitters.remove(member.get().getId());
            }

            // 알림 객체 생성 및 저장
            Notification notification =
                    new Notification(member.get().getId(), data, "결제", id);
            notificationRepository.save(notification);
        }
    }


    public List<Notification> findByMemberId(Long userId) {
        System.out.println("2= " + notificationRepository.findByMemberIdOrderByCreatedDateDesc(userId));
        return notificationRepository.findByMemberIdOrderByCreatedDateDesc(userId);
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

