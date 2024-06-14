package com.MGR.dto;

import com.MGR.entity.Category;
import lombok.Getter;
import lombok.Setter;
import org.modelmapper.ModelMapper;

import java.util.List;

@Getter
@Setter
public class CategoryFormDto {
    private Long id;

    private String attractionTypeCategory;

    private String afterTypeCategory;

    private String personalityCategory;
    private static ModelMapper modelMapper = new ModelMapper();

    public Category createCategory(){
        return modelMapper.map(this, Category.class);
    }

    public static CategoryFormDto of(Category category){
        return modelMapper.map(category, CategoryFormDto.class);
    }
}
