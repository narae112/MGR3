package com.MGR.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Setter @Getter
public class ReviewBoard {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 50)
    private String title; // 게시글 제목

    @Column(columnDefinition = "TEXT")
    private String content; // 내용

    @Column
    private int viewCount = 0;
    //조회수 초기값 0 설정

    @Column(columnDefinition = "DATETIME")
    private LocalDateTime createDate; // 생성일

    @Column(columnDefinition = "DATETIME")
    private LocalDateTime modifiedDate; // 수정일

    @Column
    private int likeCount; // 추천수

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    @OneToMany(mappedBy = "reviewBoard", cascade = CascadeType.REMOVE)
    private List<ReviewComment> reviewCommentList = new ArrayList<>();

    @PrePersist
    private void onCreate() {
        createDate = LocalDateTime.now();
    }

    @PreUpdate
    private void onUpdate() {
        modifiedDate = LocalDateTime.now();
    }

}
