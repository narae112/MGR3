package com.MGR.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter @Getter
@Entity(name = "QNA_COMMENT")
public class QnAAnswer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private LocalDateTime createDate;

    @Column
    private LocalDateTime modifiedDate;

    @Column(columnDefinition = "TEXT")
    private String content;

    @ManyToOne
    @JoinColumn(name = "qna_board_id")
    private QnAQuestion author;

    @PrePersist
    protected void onCreate() {
        createDate = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        modifiedDate = LocalDateTime.now();
    }


}
