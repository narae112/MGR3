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
@AllArgsConstructor
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long memberId;

    private String message;

    private String type;

    private Long memberCouponId;

    private Long boardId;

    @CreatedDate
    private LocalDateTime createdTime;

    public Notification(Long memberId, String message, String type, Long boardId) {
        this.memberId = memberId;
        this.message = message;
        this.type = type;
        this.boardId = boardId;
        this.createdTime = LocalDateTime.now();
    }

    public Notification(Long memberId, String message, String type, Long memberCouponId, Long boardId) {
        this.memberId = memberId;
        this.message = message;
        this.type = type;
        this.memberCouponId = memberCouponId;
        this.boardId = boardId;
        this.createdTime = LocalDateTime.now();
    }

}
