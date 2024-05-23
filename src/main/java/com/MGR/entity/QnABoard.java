package com.MGR.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Entity(name = "QNA_BOARD")
@Setter @Getter
public class QnABoard {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 50)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Column(length = 200)
    private String subject;

    @Column
    private int count = 0;
    //조회수 초기값 0 설정

    @Column(columnDefinition = "DATETIME")
    private LocalDateTime createDate;

    @Column(columnDefinition = "DATETIME")
    private LocalDateTime modifiedDate;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member author;

    @ManyToMany
    Set<Member> voter;

    @OneToMany(mappedBy = "qnABoard", cascade = CascadeType.REMOVE)
    private List<QnAComment> qnACommentList = new ArrayList<>();
}
