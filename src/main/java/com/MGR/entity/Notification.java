package com.MGR.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

@Entity
@Getter @Setter
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

    private Long paymentId;

    @CreatedDate
    private LocalDateTime createdTime;

    public Notification(Long memberId, String message, String type, Long id) {
        switch (type) {
            case "이벤트":
                this.boardId = id;
                break;
            case "쿠폰":
                this.couponId = id;
                break;
            case "결제":
                this.paymentId = id;
                break;
            default:
                throw new IllegalArgumentException("Invalid notification type: " + type);
        }
        this.memberId = memberId;
        this.message = message;
        this.type = type;
        this.createdTime = LocalDateTime.now();
    }

}
