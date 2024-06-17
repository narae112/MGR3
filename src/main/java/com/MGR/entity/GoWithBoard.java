package com.MGR.entity;

import com.MGR.constant.AgeCategory;
import com.MGR.constant.LocationCategory;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter @Setter
@Entity
public class GoWithBoard {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    @Column(columnDefinition = "DATETIME")
    private LocalDateTime createDate; // 게시글 작성 날짜

    @Column(columnDefinition = "DATETIME")
    private LocalDateTime modifiedDate;

    @Column(length = 50)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Column
    private String wantDate; // 이용자가 원하는 날짜

    @Enumerated(EnumType.STRING)
    @Column
    private LocationCategory locationCategory; // 지점

    @Enumerated(EnumType.STRING)
    @Column
    private AgeCategory ageCategory; // 나이대

    @Column
    private String attractionTypes; // 놀이기구 취향

    @Column
    private String afterTypes; // 동행 이후 취향

    @Column
    private String personalities; // 성격

    @OneToMany(mappedBy = "goWithBoard", cascade = CascadeType.ALL)
    private List<GoWithComment> commentList; // 댓글

    @OneToMany(mappedBy = "goWithBoard", cascade = CascadeType.ALL)
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
}
