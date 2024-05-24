package com.MGR.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Entity(name = "QnaQuestion") // 엔티티 이름을 명확히 합니다.
@Setter
@Getter
public class QnaQuestion {

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

    @Column(columnDefinition = "DATETIME")
    private LocalDateTime createDate;

    @Column(columnDefinition = "DATETIME")
    private LocalDateTime modifiedDate;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member author;

    @ManyToMany
    private Set<Member> voter;

    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL) // 필드 이름 수정
    private List<QnaAnswer> answerList; // 필드 이름 수정
}
