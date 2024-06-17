package com.MGR.service;

import com.MGR.entity.*;
import com.MGR.repository.CouponRepository;
import com.MGR.repository.MemberCouponRepository;
import com.MGR.repository.NotificationRepository;
import com.MGR.repository.OrderRepository;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.text.NumberFormat;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;


@Service
@RequiredArgsConstructor
public class NotificationService {
    private final OrderRepository orderRepository;
    private final MemberCouponRepository memberCouponRepository;
    private final MemberService memberService;
    private final NotificationRepository notificationRepository;
    public final Map<Long, SseEmitter> sseEmitters = new ConcurrentHashMap<>();
    private final Map<Long, Map<Long, SseEmitter>> roomEmitters = new ConcurrentHashMap<>();
    // 1. 모든 Emitters 를 저장하는 ConcurrentHashMap

    // 메시지 알림
    public SseEmitter subscribe(Long memberId) {

        // 1. 현재 클라이언트를 위한 sseEmitter 객체 생성
        SseEmitter sseEmitter = new SseEmitter(Long.MAX_VALUE);

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

        List<Member> memberList = memberService.findAll();

        //알림메세지 생성
        String data = "[" + board.getType() + "] 가 등록되었습니다> " +
                board.getTitle();

        for (Member member : memberList) {

            // SseEmitter 객체 가져오기
            SseEmitter sseEmitterReceiver = sseEmitters.get(member.getId());
            System.out.println("sseEmitterReceiver = " + sseEmitterReceiver);
            if (sseEmitterReceiver != null) {
                try {
                    sseEmitterReceiver.send(SseEmitter.event()
                            .name("message")
                            .data(data));

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
                        new Notification(member.getId(), data, "이벤트", board.getId());
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
        String data = "다른 사용자가 다음 리뷰에 좋아요를 표시했습니다. [" +
                board.getSubject() + "] - 리뷰 페이지로 이동하려면 클릭하세요.";
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
                board.getSubject() + "] - 리뷰 페이지로 이동하려면 클릭하세요.";
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
    @Transactional
    public void goWithBoard(GoWithBoard goWithBoard, Member member) {

        //알림메세지 생성
        String data = "[동행찾기] 비슷한 취향의 사용자가 글을 올렸습니다.";
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
            Notification notification = new Notification(member.getId(), data, "동행찾기", goWithBoard.getId());
            notificationRepository.save(notification); // 보낸 메세지 저장
        }
    }
    // 결제 완료 알림
    @Transactional
    public void notifyOrder(Long id, Long couponId) {
        Optional<Order> order = orderRepository.findById(id);
        Optional<Member> member = memberService.findById(order.get().getMember().getId());
        SseEmitter sseEmitter = sseEmitters.get(member.get().getId());
        String orderNum = order.get().getOrderNum();

        List<OrderTicket> orderTickets = order.get().getOrderTickets();
        int totalPrice = 0;
        for (OrderTicket orderTicket : orderTickets) {
            totalPrice += orderTicket.getTotalPrice();
        }

        int discountRate = 0; // 기본 할인율을 0으로 설정

        System.out.println("couponId = " + couponId);

        // 쿠폰 아이디가 0이 아닌 경우에만 쿠폰을 조회하여 할인율을 가져옴
        if (couponId != 0) {
            MemberCoupon memberCoupon = memberCouponRepository.findById(couponId)
                    .orElseThrow(EntityNotFoundException::new);

            discountRate = memberCoupon.getCoupon().getDiscountRate();
        }

        int amount = totalPrice - (totalPrice * discountRate / 100);
        String formattedAmount = NumberFormat.getInstance().format(amount);

        String data = "주문 번호 : " + orderNum + "<br/>결제 금액 : " + formattedAmount + "원";
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

    // 구독 메서드 수정
    public SseEmitter subscribeToRoom(Long userId, Long roomId) {
        SseEmitter sseEmitter = new SseEmitter(Long.MAX_VALUE);
        roomEmitters.computeIfAbsent(roomId, k -> new ConcurrentHashMap<>()).put(userId, sseEmitter);

        sseEmitter.onCompletion(() -> roomEmitters.get(roomId).remove(userId));
        sseEmitter.onTimeout(() -> roomEmitters.get(roomId).remove(userId));
        sseEmitter.onError((e) -> roomEmitters.get(roomId).remove(userId));

        return sseEmitter;
    }

    @Transactional
    public void sendMessage(Long roomId, Long fromId, String message) {
        Map<Long, SseEmitter> userEmitters = roomEmitters.get(roomId);
        Member member = memberService.findById(fromId).orElseThrow();
        if (userEmitters != null) {
            userEmitters.forEach((toId, sseEmitter) -> {
                try {
                    boolean isRead = fromId.equals(toId);
                    String data = "{\"sender\":{\"nickname\":\"" + member.getNickname()
                            + "\"},\"message\":\"" + message + "\",\"profileImgUrl\":\""
                            + member.getProfileImgUrl() + "\",\"isRead\":" + isRead + "}";

                    sseEmitter.send(SseEmitter.event().name("chat").data(data));
                } catch (IOException e) {
                    userEmitters.remove(toId);
                }
            });
        }
    }

    public void sendReadEvent(Long roomId, Long userId) {
        Map<Long, SseEmitter> userEmitters = roomEmitters.get(roomId);
        if (userEmitters != null) {
            userEmitters.forEach((toId, sseEmitter) -> {
                try {
                    String data = "{\"roomId\":" + roomId + ",\"userId\":" + userId + "}";
                    sseEmitter.send(SseEmitter.event().name("read").data(data));
                } catch (IOException e) {
                    userEmitters.remove(toId);
                }
            });
        }
    }
}
