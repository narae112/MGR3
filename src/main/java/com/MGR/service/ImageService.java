package com.MGR.service;

import com.MGR.entity.Image;
import com.MGR.repository.ImageRepository;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.thymeleaf.util.StringUtils;

@Service
@RequiredArgsConstructor
@Transactional
public class ImageService {
    private final ImageRepository imageRepository;
    private final FileService fileService;

    @Value("${ticketImgLocation}")
    private String ticketImgLocation;

    @Value("${reviewImgLocation}")
    private String reviewImgLocation;

    @Value("${boardImgLocation}")
    private String boardImgLocation;

    public void saveTicketImage(Image ticketImage, MultipartFile ticketImgFile) throws Exception {
        saveImage(ticketImage, ticketImgFile, ticketImgLocation);
    }

    public void saveReviewImage(Image reviewImage, MultipartFile reviewImgFile) throws Exception {
        saveImage(reviewImage, reviewImgFile, reviewImgLocation);
    }

    public void saveBoardImage(Image boardImage, MultipartFile boardImgFile) throws Exception {
        saveImage(boardImage, boardImgFile, boardImgLocation);
    }

    private void saveImage(Image image, MultipartFile imgFile, String imgLocation) throws Exception {
        String imgOriName = imgFile.getOriginalFilename();
        String imgName = "";
        String imgUrl = "";
        try {
            // 파일 업로드
            if (!StringUtils.isEmpty(imgOriName)) {
                imgName = fileService.uploadFile(imgLocation, imgOriName, imgFile.getBytes());
                // 이미지 URL 생성
                String imgDirName = imgLocation.substring(imgLocation.lastIndexOf("/") + 1);
                imgUrl = "/images/" + imgDirName + "/" + imgName;
            }
            // 이미지 정보 저장
            image.updateImg(imgOriName, imgName, imgUrl);
            // 원본이미지이름, 이미지이름, 이미지 URL을 업데이트
            imageRepository.save(image);
        } catch (Exception e) {
            // 예외 발생 시 롤백 처리 또는 다른 처리 방법 구현
            throw new Exception("이미지 저장에 실패하였습니다.", e);
        }
    }
    public void updateTicketImage(Long imageId, MultipartFile ticketImgFile) throws Exception {
        updateImage(imageId, ticketImgFile, ticketImgLocation);
    }

    public void updateReviewImage(Long imageId, MultipartFile reviewImageFile) throws Exception {
        updateImage(imageId, reviewImageFile, reviewImgLocation);
    }

    public void updateBoardImage(Long imageId, MultipartFile boardImgFile) throws Exception {
        updateImage(imageId, boardImgFile, boardImgLocation);
    }

    private void updateImage(Long imageId, MultipartFile imgFile, String imgLocation) throws Exception {
        if (!imgFile.isEmpty()) {
            Image savedImg = imageRepository.findById(imageId)
                    .orElseThrow(EntityNotFoundException::new);
            // 기존 이미지 파일 삭제
            if (!StringUtils.isEmpty(savedImg.getImgName())) {
                fileService.deleteFile(imgLocation + "/" + savedImg.getImgName());
            }
            String imgOriName = imgFile.getOriginalFilename();
            String imgName = fileService.uploadFile(imgLocation, imgOriName, imgFile.getBytes());
            String imgUrl = "/images/" + imgLocation.substring(imgLocation.lastIndexOf("/") + 1) + "/" + imgName;
            savedImg.updateImg(imgOriName, imgName, imgUrl);
            // 이미지 정보 저장
            imageRepository.save(savedImg);
        }
    }
}