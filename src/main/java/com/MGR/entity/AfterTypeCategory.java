package com.MGR.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@Getter
@Setter
@Entity
public class AfterTypeCategory {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    private Category category;

    public void updateAfterTypeCategory(String name){
        this.name = name;
    }
    @Override
    public String toString() {
        return name;
    }
}
