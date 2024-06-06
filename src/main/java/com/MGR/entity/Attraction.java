package com.MGR.entity;

import com.MGR.dto.AttractionDto;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Setter @Getter
public class Attraction {

    // 240606 테스트입니다

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 20)
    private String name;

//    private Boolean condition;

    private int closureDay;

    @Column(columnDefinition = "TEXT")
    private String information;


    public static Attraction create(AttractionDto attractionDto) {
        Attraction attraction = new Attraction();
//        attraction.setCondition(true);
        attraction.setName(attractionDto.getName());
        attraction.setInformation(attractionDto.getInformation());
        attraction.setClosureDay(attractionDto.getClosureDay());

        return attraction;
    }
}
