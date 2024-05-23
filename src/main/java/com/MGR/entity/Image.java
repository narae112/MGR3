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
    private String imgName;

    @Column(length = 50)
    private String imgOriName;

    @Column(length = 100)
    private String imgUrl;

    @Column
    private Boolean repImgYn;

    @ManyToOne(fetch = FetchType.LAZY)
    private Ticket ticket;

    @ManyToOne(fetch=FetchType.LAZY)
    private QnAAnswer qnAAnswer;

    @ManyToOne(fetch=FetchType.LAZY)
    private ReviewComment reviewComment;

    public void updateImg(String imgOriName, String imgName, String imgUrl){
        this.imgOriName = imgOriName;
        this.imgName = imgName;
        this.imgUrl = imgUrl;
    }

}

