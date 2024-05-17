package com.MGR.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Setter @Getter
public class Attraction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 20)
    private String name;

    @Column(length = 10)
    private String condition;
//    boolean condition;
    
    @Column(columnDefinition = "TEXT")
    private String information;

}
