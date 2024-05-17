package com.MGR.entity;

import com.MGR.constant.ImageCategory;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Setter @Getter
public class Image {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private ImageCategory imageCategory;

    @Column(length = 50)
    private String title;

    @Column(length = 50)
    private String imgName;

    @Column(length = 50)
    private String oriName;

    @Column(length = 100)
    private String imgUrl;

    @Column
    private Boolean repImgYn;

}

