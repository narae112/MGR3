package com.MGR.service;

import com.MGR.controller.NotificationController;
import com.MGR.entity.EventBoard;
import com.MGR.entity.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class NotificationService {

    private final MemberService memberService;

    // 메시지 알림
    public SseEmitter subscribe(Long userId) {

        // 1. 현재 클라이언트를 위한 sseEmitter 객체 생성
        SseEmitter sseEmitter = new SseEmitter(Long.MAX_VALUE);

        // 2. 연결
        try {
            sseEmitter.send(SseEmitter.event()
                    .name("connect")
                    .data("연결성공"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 3. 저장
        NotificationController.sseEmitters.put(userId, sseEmitter);

        // 4. 연결 종료 처리
        sseEmitter.onCompletion(() -> NotificationController.sseEmitters.remove(userId));    // sseEmitter 연결이 완료될 경우
        sseEmitter.onTimeout(() -> NotificationController.sseEmitters.remove(userId));        // sseEmitter 연결에 타임아웃이 발생할 경우
        sseEmitter.onError((e) -> NotificationController.sseEmitters.remove(userId));        // sseEmitter 연결에 오류가 발생할 경우

        return sseEmitter;
    }


    // 이벤트 등록 알림 - 전부
    public void notifyBoard(String boardTitle) {
        // 5. 모든 멤버 id 조회
        List<Member> memberList = memberService.findByAllMembers();

        for (Member member : memberList) {
//            idList.add(member.getId());
            // 6. 모든 멤버에게 보낼 메시지 객체 가져오기
            SseEmitter sseEmitterReceiver =
                    NotificationController.sseEmitters.get(member.getId());
            if (sseEmitterReceiver != null) {

                try {
                    sseEmitterReceiver.send(SseEmitter.event()
                            .name("addMessage")
                            .data("새로운 이벤트가 등록되었습니다> " + boardTitle));
                } catch (Exception e) {
                    NotificationController.sseEmitters.remove(member.getId());
                }
            }
        }
    }
}

//    // 채팅 수신 알림 - receiver 에게
//    public void notifyMessage(Long id) {
//        // 5. 수신자 정보 조회
//        Member member = memberService.findById(id).orElseThrow();
//
//        // 6. 수신자 정보로부터 id 값 추출
//        Long userId = member.getId();
//
//        // 7. Map 에서 userId 로 사용자 검색
//        if (NotificationController.sseEmitters.containsKey(userId)) {
//            SseEmitter sseEmitterReceiver = NotificationController.sseEmitters.get(userId);
//            // 8. 알림 메시지 전송 및 해체
//            try {
//                sseEmitterReceiver.send(SseEmitter.event().name("addMessage").data("메시지가 왔습니다."));
//            } catch (Exception e) {
//                NotificationController.sseEmitters.remove(userId);
//            }
//        }
//    }

    // 댓글 알림 - 게시글 작성자 에게
//    public void notifyComment(Long postId) {
//        EventBoard post = eventBoardService.findById(postId).orElseThrow(
//                () -> new IllegalArgumentException("게시글을 찾을 수 없습니다.")
//        );
//
//        Long userId = post.getMember().getId();
//
//        if (NotificationController.sseEmitters.containsKey(userId)) {
//            SseEmitter sseEmitter = NotificationController.sseEmitters.get(userId);
//            try {
//                sseEmitter.send(SseEmitter.event().name("addComment").data("댓글이 달렸습니다."));
//            } catch (Exception e) {
//                NotificationController.sseEmitters.remove(userId);
//            }
//        }
//    }
//}
