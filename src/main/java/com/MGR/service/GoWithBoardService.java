package com.MGR.service;

import com.MGR.constant.AgeCategory;
import com.MGR.constant.LocationCategory;
import com.MGR.entity.GoWithBoard;
import com.MGR.entity.Image;
import com.MGR.entity.Member;
import com.MGR.entity.ReviewBoard;
import com.MGR.repository.GoWithBoardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Service
public class GoWithBoardService {
    private final GoWithBoardRepository goWithBoardRepository;
    private final ImageService imageService;


    public Long createGoWithBoard(Member user, String title, String content, LocalDate wantDate, LocationCategory locationCategory,
                                  AgeCategory ageCategory, List<String> attractionTypes, List<String> afterTypes,
                                  List<String> personalities, List<MultipartFile> goWithImgFileList) throws Exception {

        GoWithBoard goWithBoard = new GoWithBoard();

        goWithBoard.setMember(user);
        goWithBoard.setCreateDate(LocalDateTime.now());
        goWithBoard.setWantDate(wantDate);
        goWithBoard.setLocationCategory(locationCategory);
        goWithBoard.setAgeCategory(ageCategory);
        goWithBoard.setAttractionTypes(String.join(",", attractionTypes));
        goWithBoard.setAfterTypes(String.join(",", afterTypes));
        goWithBoard.setPersonalities(String.join(",", personalities));

        goWithBoard.setTitle(title);
        goWithBoard.setContent(content);

        this.goWithBoardRepository.save(goWithBoard);

        for (int i = 0; i < goWithImgFileList.size(); i++) {
            Image goWithImage = new Image();
            goWithImage.setGoWithBoard(goWithBoard);

            if (i == 0) {
                goWithImage.setRepImgYn(true);
            } else {
                goWithImage.setRepImgYn(false);
            }
            imageService.saveGoWithImage(goWithImage, goWithImgFileList.get(i));
        }
        return goWithBoard.getId();
    }
}
