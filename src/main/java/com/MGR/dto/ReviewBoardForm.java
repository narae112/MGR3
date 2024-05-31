package com.MGR.dto;

import com.MGR.entity.ReviewBoard;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.modelmapper.ModelMapper;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class ReviewBoardForm {
    private Long id;

    private List<ImageDto> reviewImgDtoList = new ArrayList<>();

    private List<Long> reviewImgIds = new ArrayList<>();

    @NotEmpty(message="제목은 필수항목입니다.")
    @Size(max = 200)
    private String subject;

    @NotEmpty(message = "내용은 필수항목입니다.")
    private String content;

    private LocalDateTime modifiedDate;


    private static ModelMapper modelMapper = new ModelMapper();

    public ReviewBoard createReviewBoard() {
        return modelMapper.map(this, ReviewBoard.class);
    }
    public static ReviewBoardForm of(ReviewBoard reviewBoard) {
        return modelMapper.map(reviewBoard, ReviewBoardForm.class);
    }

}
