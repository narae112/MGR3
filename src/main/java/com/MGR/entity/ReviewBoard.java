package com.MGR.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Formula;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;


@Entity
@Setter
@Getter
public class ReviewBoard {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

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

    @OneToMany(mappedBy = "reviewBoard", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Image> images;

    @ManyToOne
    private Member author;

    @ManyToMany
    Set<Member> voter;

    @OneToMany(mappedBy = "reviewBoard", cascade = CascadeType.ALL)
    private List<ReviewComment> commentList;

    public int viewCount() {
        return this.count += 1;
    }

    @Formula("(SELECT COUNT(*) FROM review_board_voter WHERE review_board_voter.review_board_id = id)")
    private int voterCount;


}
