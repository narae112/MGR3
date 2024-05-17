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

    private int likeCount;

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
