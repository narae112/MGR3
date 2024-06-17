package com.MGR.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class GoWithCommentFormDto {
    @NotEmpty(message = "내용을 입력하세요")
    private String content;
}
