package com.MGR.service;

import com.MGR.entity.*;
import com.MGR.repository.ImageRepository;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.thymeleaf.util.StringUtils;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ImageService {
    private final ImageRepository imageRepository;
    private final FileService fileService;

    @Value("${ticketImgLocation}")
    private String ticketImgLocation;

    @Value("${reviewImgLocation}")
    private String reviewImgLocation;

    @Value("${boardImgLocation}")
    private String boardImgLocation;

    @Value("${goWithImgLocation}")
    private String goWithImgLocation;

    @Value("${couponImgLocation}")
    private String couponImgLocation;

    @Value("${attractionImgLocation}")
    private String attractionImgLocation;

    @Value("${profileImgLocation}")
    private String profileImgLocation;

    public void saveTicketImage(Image ticketImage, MultipartFile ticketImgFile) throws Exception {
        saveImage(ticketImage, ticketImgFile, ticketImgLocation);
    }

    public void saveReviewImage(Image reviewImage, MultipartFile reviewImgFile) throws Exception {
        saveImage(reviewImage, reviewImgFile, reviewImgLocation);
    }

    public void saveBoardImage(Image boardImage, MultipartFile boardImgFile) throws Exception {
        saveImage(boardImage, boardImgFile, boardImgLocation);
    }

    public void saveGoWithImage(Image goWithImage, MultipartFile goWithImgFile) throws Exception {
        saveImage(goWithImage, goWithImgFile, goWithImgLocation);
    }

    public void saveCouponImage(Image couponImage, MultipartFile couponImgFile) throws Exception {
        saveImage(couponImage, couponImgFile, couponImgLocation);
    }

    public void saveAttractionImage(Image attractionImage, MultipartFile attractionImgFile) throws Exception {
        saveImage(attractionImage, attractionImgFile, attractionImgLocation);
    }

    public void saveProfileImage(Image profileImage, MultipartFile profileImgFile) throws Exception {
        saveImage(profileImage, profileImgFile, profileImgLocation);
    }

    private void saveImage(Image image, MultipartFile imgFile, String imgLocation) throws Exception {
        System.out.println("saveImage 시작");
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
                System.out.println("imgUrl: " + imgUrl);
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

    public void updateTicketImage(Long ticketImgId, MultipartFile ticketImgFile) throws Exception {
        updateImage(ticketImgId, ticketImgFile, ticketImgLocation);
    }

    public void updateReviewImage(Long reviewImgId, MultipartFile reviewImageFile) throws Exception {
        updateImage(reviewImgId, reviewImageFile, reviewImgLocation);
    }

    public void updateBoardImage(Long boardImgId, MultipartFile boardImgFile) throws Exception {
        updateImage(boardImgId, boardImgFile, boardImgLocation);
    }

    public void updateGoWithImage(Long goWithImgId, MultipartFile goWithImgFile) throws Exception {
        updateImage(goWithImgId, goWithImgFile, goWithImgLocation);
    }

    public void updateCouponImage(Long couponImgId, MultipartFile couponFile) throws Exception {
        updateImage(couponImgId, couponFile, couponImgLocation);
    }

    public void updateImage(Long id, MultipartFile imgFile, String imgLocation) throws Exception {
        if (!imgFile.isEmpty()) { // 업로드한 파일이 있는경우(새로운 이미지를 업로드한경우)
            Image savedImg = imageRepository.findById(id).
                    orElseThrow(EntityNotFoundException::new);
            //기존 이미지 파일삭제
            if (!StringUtils.isEmpty(savedImg.getImgName())) {
                fileService.deleteFile(imgLocation + "/" + savedImg.getImgName());
            }
            String imgOriName = imgFile.getOriginalFilename();
            String imgName = fileService.uploadFile(imgLocation, imgOriName, imgFile.getBytes());
            String imgDirName = imgLocation.substring(imgLocation.lastIndexOf("/") + 1);
            String imgUrl = "/images/" + imgDirName + "/" + imgName;
            savedImg.updateImg(imgOriName, imgName, imgUrl);
        }
    }

    public void deleteImagesByTicketId(Long ticketId) {
        imageRepository.deleteByTicketId(ticketId);
    }

    public void deleteImagesByCouponId(Long couponId) {
        imageRepository.deleteById(couponId);
    }

    public void deleteImagesByReviewBoardId(Long reviewBoardId) throws Exception {
        List<Image> images = imageRepository.findByReviewBoardId(reviewBoardId);
        for (Image image : images) {
            try {
                fileService.deleteFile(image.getImgName()); // 파일 시스템에서 이미지 파일 삭제
                imageRepository.delete(image); // 데이터베이스에서 이미지 레코드 삭제
            } catch (Exception e) {
                // 개별 이미지 삭제 실패 시 로그 기록 또는 별도 처리
                System.err.println("이미지 삭제 실패: " + e.getMessage());
                throw new Exception("이미지 삭제 중 오류가 발생하였습니다.", e);
            }
        }
    }

    public void deleteImage(EventBoard eventBoard) {
        Image image = imageRepository.findByEventBoard(eventBoard);
        imageRepository.delete(image);
    }

    public void deleteImage(Attraction attraction) {
        Image image = imageRepository.findByAttraction(attraction);
        imageRepository.delete(image);
    }

    public Image findByEvent(EventBoard eventBoard) {
        return imageRepository.findByEventBoard(eventBoard);
    }

    public Image findByAttraction(Attraction attraction) {
        return imageRepository.findByAttraction(attraction);
    }
    public Image findByTicket(Ticket ticket){
        return imageRepository.findByTicket(ticket);
    }

    public Image findByMember(Member member) {
        System.out.println("이미지 엔티티 확인 = " + imageRepository.findByMember(member));
        return imageRepository.findByMember(member);
    }
}
