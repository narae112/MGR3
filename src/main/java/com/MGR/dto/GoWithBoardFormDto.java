package com.MGR.dto;

import com.MGR.constant.AgeCategory;
import com.MGR.constant.LocationCategory;
import com.MGR.entity.GoWithBoard;
import com.MGR.entity.Image;
import com.MGR.entity.Member;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.modelmapper.ModelMapper;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter @Setter
public class GoWithBoardFormDto {
    private Long id;

    private LocalDateTime createDate;
    private LocalDateTime modifiedDate;

    private Member member;
    private String memberName;

    private String wantDate;
    private LocationCategory locationCategory;
    private AgeCategory ageCategory;

    private List<String> attractionTypes;
    private List<String> afterTypes;
    private List<String> personalities;

    @NotEmpty(message = "제목은 필수항목입니다.")
    @Size(max = 200)
    private String title;

    @NotEmpty(message = "내용은 필수항목입니다.")
    private String content;

    private List<ImageDto> goWithImgDtoList = new ArrayList<>();
    private List<Long> goWithImgIds = new ArrayList<>();

    private String memberProfileImgUrl;  // 프로필 이미지 URL 추가

    private static ModelMapper modelMapper = new ModelMapper();

    public GoWithBoard createGoWithBoard() {
        return modelMapper.map(this, GoWithBoard.class);
    }

    public static GoWithBoardFormDto of(GoWithBoard goWithBoard) {
        GoWithBoardFormDto goWithBoardFormDto = modelMapper.map(goWithBoard, GoWithBoardFormDto.class);

        // GoWithBoard와 연관된 이미지 가져오기
        List<Image> images = goWithBoard.getImages();
        if (images != null && !images.isEmpty()) {
            List<ImageDto> imageDtos = images.stream()
                    .map(image -> modelMapper.map(image, ImageDto.class))
                    .collect(Collectors.toList());

            goWithBoardFormDto.setGoWithImgDtoList(imageDtos);

            List<Long> imageIds = images.stream()
                    .map(Image::getId)
                    .collect(Collectors.toList());

            goWithBoardFormDto.setGoWithImgIds(imageIds);
        }

        // 작성자의 프로필 정보 추가
        Member member = goWithBoard.getMember();
        goWithBoardFormDto.setMemberName(member.getNickname());
        goWithBoardFormDto.setMemberProfileImgUrl(member.getProfileImgUrl());

        return goWithBoardFormDto;
    }

    public String getImageUrl() {
        if (this.goWithImgDtoList != null && !this.goWithImgDtoList.isEmpty()) {
            return this.goWithImgDtoList.get(0).getImgUrl();
        }
        return null;
    }
}
