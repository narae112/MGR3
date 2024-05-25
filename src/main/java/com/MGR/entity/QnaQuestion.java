package com.MGR.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;


@Entity
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
    private Member author;

    @OneToMany(mappedBy = "qnaQuestion", cascade = CascadeType.ALL)
    private List<QnaAnswer> answerList;

}
