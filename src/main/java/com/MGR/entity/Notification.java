package com.MGR.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

@Entity
@Getter @Setter @ToString
@NoArgsConstructor
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long memberId;

    private String message;

    private String type;

    private Long couponId;

    private Long boardId;

    private Long orderId;

    private Long reviewId;

    private Long reviewCommentId;

    @CreatedDate
    private LocalDateTime createdDate;

    public Notification(Long memberId, String message, String type, Long id) {
        switch (type) {
            case "이벤트":
                this.boardId = id;
                break;
            case "쿠폰":
                this.couponId = id;
                break;
            case "결제":
                this.orderId = id;
                break;
            case "리뷰":
                this.reviewId = id;
                break;
            case "리뷰댓글":
                this.reviewCommentId = id;
                break;
            default:
                throw new IllegalArgumentException("공지 타입 입력 오류= " + type);
        }
        this.memberId = memberId;
        this.message = message;
        this.type = type;
        this.createdDate = LocalDateTime.now();
    }

}
