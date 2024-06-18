package com.MGR.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Formula;

import java.time.LocalDateTime;
import java.util.ArrayList;
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

    @OneToMany(mappedBy = "reviewBoard", cascade = CascadeType.ALL)
    private List<Image> images;

    public List<String> getImageUrls() {
        List<String> imageUrls = new ArrayList<>();
        if (images != null) {
            for (Image image : images) {
                imageUrls.add(image.getImgUrl());
            }
        }
        return imageUrls;
    }

    // 다른 필드들과 함께 이미지 URL을 저장할 필드 추가
    @ElementCollection
    private List<String> imageUrls;

    public String getFirstImageUrl() {
        if (images != null && !images.isEmpty()) {
            return images.get(0).getImgUrl();
        }
        return null;
    }
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
