package com.MGR.entity;

import com.MGR.constant.EventType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Setter @Getter
public class EventBoard {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private EventType type;

    @Column(length = 50)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Column
    private int count = 0;
    //조회수 초기값 0 설정

    @Column(columnDefinition = "DATETIME")
    private LocalDateTime createDate;

    @Column(columnDefinition = "DATETIME")
    private LocalDateTime modifiedDate;

    @Column(columnDefinition = "DATETIME")
    private LocalDateTime startDate;

    @Column(columnDefinition = "DATETIME")
    private LocalDateTime endDate;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    @PrePersist
    private void onCreate() {
        createDate = LocalDateTime.now();
    }

    @PreUpdate
    private void onUpdate() {
        modifiedDate = LocalDateTime.now();
    }

}
