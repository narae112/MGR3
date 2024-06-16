package com.MGR.service;

import com.MGR.constant.AgeCategory;
import com.MGR.constant.LocationCategory;
import com.MGR.dto.GoWithBoardFormDto;
import com.MGR.dto.ImageDto;
import com.MGR.dto.ReviewBoardForm;
import com.MGR.entity.GoWithBoard;
import com.MGR.entity.Image;
import com.MGR.entity.Member;
import com.MGR.entity.ReviewBoard;
import com.MGR.exception.DataNotFoundException;
import com.MGR.repository.GoWithBoardRepository;
import com.MGR.repository.ImageRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class GoWithBoardService {
    private final GoWithBoardRepository goWithBoardRepository;
    private final ImageService imageService;
    private final ImageRepository imageRepository;


    public Long createGoWithBoard(Member user, String title, String content, String wantDate, LocationCategory locationCategory,
                                  AgeCategory ageCategory, List<String> attractionTypes, List<String> afterTypes,
                                  List<String> personalities, List<MultipartFile> goWithImgFileList) throws Exception {

        GoWithBoard goWithBoard = new GoWithBoard();

        goWithBoard.setMember(user);
        goWithBoard.setCreateDate(LocalDateTime.now());
        goWithBoard.setWantDate(wantDate);
        goWithBoard.setLocationCategory(locationCategory);
        goWithBoard.setAgeCategory(ageCategory);
        goWithBoard.setAttractionTypes(attractionTypes != null ? String.join(",", attractionTypes) : "");
        goWithBoard.setAfterTypes(afterTypes != null ? String.join(",", afterTypes) : "");
        goWithBoard.setPersonalities(personalities != null ? String.join(",", personalities) : "");

        goWithBoard.setTitle(title);
        goWithBoard.setContent(content);

        goWithBoardRepository.save(goWithBoard);

        for (int i = 0; i < goWithImgFileList.size(); i++) {
            Image goWithImage = new Image();
            goWithImage.setGoWithBoard(goWithBoard);
            if (i == 0) {
                goWithImage.setRepImgYn(true);
            } else {
                goWithImage.setRepImgYn(false);
            }

            if(!goWithImgFileList.get(i).isEmpty()) {
                imageService.saveGoWithImage(goWithImage, goWithImgFileList.get(i));
            }
        }
        return goWithBoard.getId();
    }

    public GoWithBoard getGoWithBoard(Long id){
        Optional<GoWithBoard> goWithBoard = this.goWithBoardRepository.findById(id);
        if(goWithBoard.isPresent()){
            return goWithBoard.get();

        }else{
            throw new DataNotFoundException("게시물을 찾을 수 없습니다");
        }
    }

    public void saveGoWithBoard(GoWithBoard goWithBoard) {
        goWithBoardRepository.save(goWithBoard);
    }

    @Transactional(readOnly = true)
    public GoWithBoardFormDto getGoWithBoardDtl(Long goWithBoardId){
        List<Image> goWithImgList = imageRepository.findByGoWithBoardIdOrderByIdAsc(goWithBoardId);
        List<ImageDto> goWithImgDtoList = new ArrayList<>();
        for(Image goWithImage : goWithImgList){
            ImageDto goWithImgDto = ImageDto.of(goWithImage);
            goWithImgDtoList.add(goWithImgDto);
        }
        GoWithBoard goWithBoard = goWithBoardRepository.findById(goWithBoardId).
                orElseThrow(EntityNotFoundException::new);

        GoWithBoardFormDto goWithBoardFormDto = GoWithBoardFormDto.of(goWithBoard);
        goWithBoardFormDto.setGoWithImgDtoList(goWithImgDtoList);
        return goWithBoardFormDto;
    }
}
