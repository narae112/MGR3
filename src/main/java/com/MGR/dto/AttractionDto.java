package com.MGR.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class AttractionDto {

    @NotBlank(message = "이름을 입력하세요")
    private String name;

    @NotBlank(message = "상세설명란을 입력하세요")
    private String information;

    @Min(value = 1, message = "운휴 일자는 1 이상이어야 합니다")
    @Max(value = 31, message = "운휴 일자는 31 이하여야 합니다")
    private int closureDay;



}
