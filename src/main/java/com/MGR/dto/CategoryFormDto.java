package com.MGR.dto;

import com.MGR.entity.Category;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.modelmapper.ModelMapper;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class CategoryFormDto {
    private Long id;
    @NotEmpty(message = "놀이기구 취향 키워드를 입력해주세요.")
    private List<String> attractionTypeCategory;

    @NotEmpty(message = "동행 후 행선지 키워드를 입력해주세요.")
    private List<String> afterTypeCategory;

    @NotEmpty(message = "성격 키워드를 입력해주세요.")
    private List<String> personalityCategory;

    private static ModelMapper modelMapper = new ModelMapper();


    public static CategoryFormDto of(Category category){
        return modelMapper.map(category, CategoryFormDto.class);
    }
    public boolean isAttractionTypeCategoriesValid() {
        return getNonEmptySize(this.attractionTypeCategory) >= 2;
    }

    public boolean isAfterTypeCategoriesValid() {
        return getNonEmptySize(this.afterTypeCategory) >= 2;
    }

    public boolean isPersonalityCategoriesValid() {
        return getNonEmptySize(this.personalityCategory) >= 2;
    }

    private int getNonEmptySize(List<String> list) {
        return (int) list.stream()
                .filter(str -> str != null && !str.trim().isEmpty())
                .count();
    }
}
