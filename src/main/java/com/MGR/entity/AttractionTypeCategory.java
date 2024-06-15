package com.MGR.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Entity
public class AttractionTypeCategory {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    private Category category;

    public void updateAttractionTypeCategory(String name){
        this.name = name;
    }
    @Override
    public String toString() {
        return name;
    }
}
