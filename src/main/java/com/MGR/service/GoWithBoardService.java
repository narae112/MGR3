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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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

    // 게시글 작성
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

        Member member = goWithBoard.getMember();
        goWithBoardFormDto.setMemberName(member.getNickname());
        String profileImgUrl = member.getProfileImgUrl();
        goWithBoardFormDto.setMemberProfileImgUrl(profileImgUrl != null ? profileImgUrl : null);

        return goWithBoardFormDto;
    }

    // 게시글 삭제
    public void delete(GoWithBoard goWithBoard) throws Exception {
        try {
            imageService.deleteImagesByGoWithBoardId(goWithBoard.getId());
            this.goWithBoardRepository.delete(goWithBoard);
        } catch (Exception e) {
            throw new Exception("질문 삭제 중 오류가 발생하였습니다.", e);
        }
    }

    // 게시글 수정
    @Transactional
    public void modify(GoWithBoard goWithBoard, String title, String content, String wantDate, LocationCategory locationCategory,
                       AgeCategory ageCategory, List<String> attractionTypes, List<String> afterTypes,
                       List<String> personalities, List<MultipartFile> goWithImgFileList) throws Exception {
        // 기존 질문의 내용을 수정
        goWithBoard.setTitle(title);
        goWithBoard.setContent(content);
        goWithBoard.setModifiedDate(LocalDateTime.now());
        goWithBoard.setWantDate(wantDate);
        goWithBoard.setLocationCategory(locationCategory);
        goWithBoard.setAgeCategory(ageCategory);
        goWithBoard.setAttractionTypes(attractionTypes != null ? String.join(",", attractionTypes) : "");
        goWithBoard.setAfterTypes(afterTypes != null ? String.join(",", afterTypes) : "");
        goWithBoard.setPersonalities(personalities != null ? String.join(",", personalities) : "");

        // 기존 이미지를 삭제하고 새로운 이미지를 추가
        if (goWithImgFileList != null && !goWithImgFileList.isEmpty()) {
            // 기존 이미지를 삭제
            imageService.deleteImagesByGoWithBoardId(goWithBoard.getId());

            // 새로운 이미지를 저장
            for (MultipartFile goWithImg : goWithImgFileList) {
                if (goWithImg != null && !goWithImg.isEmpty()) {
                    Image image = new Image();
                    imageService.saveGoWithImage(image, goWithImg);
                    image.setGoWithBoard(goWithBoard);
                    imageRepository.save(image);
                }
            }
        }

        // 수정된 내용을 저장
        goWithBoardRepository.save(goWithBoard);
    }

    @Transactional(readOnly = true)
    public Page<GoWithBoardFormDto> getAllGoWithBoards(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<GoWithBoard> goWithBoardPage = goWithBoardRepository.findAll(pageable);
        return goWithBoardPage.map(GoWithBoardFormDto::of);
    }

    @Transactional(readOnly = true)
    public Page<GoWithBoardFormDto> searchGoWithBoards(List<String> ageCategories,
                                                       List<String> locationCategories,
                                                       List<String> attractionTypes,
                                                       List<String> afterTypes,
                                                       List<String> personalities) {
        // 필터링 조건을 받아서 해당 조건에 맞는 게시글을 조회하는 메서드를 호출합니다.
        // 이 메서드는 선택된 조건들을 기반으로 검색된 페이지를 반환해야 합니다.
        // 여기서는 단순 예시로 전달된 값들을 이용하여 게시글을 필터링하는 방법을 보여줍니다.
        // 실제로는 여기서 조건에 맞는 게시글을 조회하는 쿼리를 작성해야 합니다.
        // 이 예제에서는 페이지를 고정 크기로 반환합니다. 필요에 따라 페이지 크기 등을 조정할 수 있습니다.
        Pageable pageable = PageRequest.of(0, 10); // 페이지 크기 10으로 설정
        Page<GoWithBoard> filteredGoWithBoardsPage = goWithBoardRepository.findAll(pageable);

        // 필터링된 게시글 페이지를 GoWithBoardFormDto로 매핑합니다.
        return filteredGoWithBoardsPage.map(GoWithBoardFormDto::of);
    }
}
