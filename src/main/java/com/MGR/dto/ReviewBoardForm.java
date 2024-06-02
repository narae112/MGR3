package com.MGR.dto;

import com.MGR.entity.Image;
import com.MGR.entity.Member;
import com.MGR.entity.ReviewBoard;
import com.MGR.entity.ReviewComment;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.modelmapper.ModelMapper;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Setter
public class ReviewBoardForm {
    private Long id;

    private List<ImageDto> reviewImgDtoList = new ArrayList<>();

    private List<Long> reviewImgIds = new ArrayList<>();

    @NotEmpty(message = "제목은 필수항목입니다.")
    @Size(max = 200)
    private String subject;

    @NotEmpty(message = "내용은 필수항목입니다.")
    private String content;

    private LocalDateTime createDate;
    private LocalDateTime modifiedDate;
    private int count;
    private Member author;
    private Set<Member> voter;
    private List<ReviewComment> commentList;

    private static ModelMapper modelMapper = new ModelMapper();

    public ReviewBoard createReviewBoard() {
        return modelMapper.map(this, ReviewBoard.class);
    }

    public static ReviewBoardForm of(ReviewBoard reviewBoard) {
        ReviewBoardForm reviewBoardForm = modelMapper.map(reviewBoard, ReviewBoardForm.class);
        // ReviewBoard와 연관된 이미지 가져오기
        List<Image> images = reviewBoard.getImages();
        if (images != null && !images.isEmpty()) {
            List<ImageDto> imageDtos = images.stream()
                    .map(image -> modelMapper.map(image, ImageDto.class))
                    .collect(Collectors.toList());
            reviewBoardForm.setReviewImgDtoList(imageDtos);
            List<Long> imageIds = images.stream()
                    .map(Image::getId)
                    .collect(Collectors.toList());
            reviewBoardForm.setReviewImgIds(imageIds);
        }
        return reviewBoardForm;
    }
    public String getImageUrl() {
        if (this.reviewImgDtoList != null && !this.reviewImgDtoList.isEmpty()) {
            return this.reviewImgDtoList.get(0).getImgUrl();
        }
        return null;
    }
}
