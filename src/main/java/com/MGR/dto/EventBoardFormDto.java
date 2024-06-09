package com.MGR.dto;

import com.MGR.constant.EventType;
import com.MGR.entity.EventBoard;
import com.MGR.entity.Ticket;
import jakarta.persistence.Column;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;
import org.modelmapper.ModelMapper;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter @Setter
public class EventBoardFormDto {

    private EventType type;

    @NotBlank
    @Length(min=1, max=30, message = "제목을 30자 이하로 입력해주세요")
    private String title;

    @Size(max = 299, message = "내용을 300자 미만으로 입력해주세요")
    private String content;

    @NotEmpty(message = "시작 날짜를 입력해주세요")
    private String startDate;

    @NotEmpty(message = "종료 날짜를 입력해주세요")
    private String endDate;

    private ImageDto eventImgDtoList;


}
