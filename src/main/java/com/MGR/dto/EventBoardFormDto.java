package com.MGR.dto;

import com.MGR.constant.EventType;
import jakarta.persistence.Column;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDateTime;

@Getter @Setter
public class EventBoardFormDto {

    private EventType type;

    @NotBlank
    @Length(min=1, max=30, message = "제목을 30자 이하로 입력해주세요")
    private String title;

    @Size(max = 299, message = "내용을 300자 미만으로 입력해주세요")
    private String content;

    private String startDate;

    private String endDate;

}
