package com.MGR.entity;

import com.MGR.constant.ImageCategory;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Image extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private ImageCategory imageCategory;

    @Column(length = 50)
    private String imgTitle;

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
    private EventBoard eventBoard;

    @ManyToOne(fetch=FetchType.LAZY)
    private ReviewComment reviewComment;

    public void updateImg(String imgOriName, String imgName, String imgUrl){
        this.imgOriName = imgOriName;
        this.imgName = imgName;
        this.imgUrl = imgUrl;
    }


}

