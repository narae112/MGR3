package com.MGR.service;

import com.MGR.entity.EventBoard;
import com.MGR.entity.Image;
import com.MGR.entity.Member;
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

    public void updateTicketImage(Long ticketImgId, MultipartFile ticketImgFile) throws Exception {
        updateImage(ticketImgId, ticketImgFile, ticketImgLocation);
    }

    public void updateReviewImage(Long reviewImgId, MultipartFile reviewImageFile) throws Exception {
        updateImage(reviewImgId, reviewImageFile, reviewImgLocation);
    }

    public void updateBoardImage(Long boardImgId, MultipartFile boardImgFile) throws Exception {
        updateImage(boardImgId, boardImgFile, boardImgLocation);
    }

    public void updateImage(Long id, MultipartFile imgFile, String imgLocation) throws Exception {
        if(!imgFile.isEmpty()){ // 업로드한 파일이 있는경우(새로운 이미지를 업로드한경우)
            Image savedImg = imageRepository.findById(id).
                    orElseThrow(EntityNotFoundException::new);
            //기존 이미지 파일삭제
            if(!StringUtils.isEmpty(savedImg.getImgName())){
                fileService.deleteFile(imgLocation+"/"+savedImg.getImgName());
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

    public List<Image> findByEvent(EventBoard eventBoard) {
        return imageRepository.findByEventBoard(eventBoard);
    }

    public void deleteImage(EventBoard eventBoard) {
        Optional<Image> image = imageRepository.findById(eventBoard.getId());
        imageRepository.delete(image.get());
    }
}
