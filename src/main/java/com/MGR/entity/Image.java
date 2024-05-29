package com.MGR.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Setter @Getter
public class Image extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 50)
    private String imgName; // 이미지 이름

    @Column(length = 50)
    private String imgOriName; // 원본 파일명

    @Column(length = 100)
    private String imgUrl; // 이미지 경로

    @Column
    private Boolean repImgYn; // 대표 이미지 여부

    @ManyToOne(fetch = FetchType.LAZY)
    private Ticket ticket;

    @ManyToOne(fetch=FetchType.LAZY)
    private QnaQuestion qnaQuestion;

    @ManyToOne(fetch = FetchType.LAZY)
    private EventBoard eventBoard;

    @ManyToOne(fetch = FetchType.LAZY)
    private Coupon coupon;

    public void updateImg(String imgOriName, String imgName, String imgUrl){
        this.imgOriName = imgOriName;
        this.imgName = imgName;
        this.imgUrl = imgUrl;
    }

}

