package com.MGR.service;

import com.MGR.dto.ImageDto;
import com.MGR.dto.ReviewBoardForm;
import com.MGR.entity.*;
import com.MGR.exception.DataNotFoundException;
import com.MGR.repository.ImageRepository;
import com.MGR.repository.ReviewBoardRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.criteria.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class ReviewBoardService {

    private final ReviewBoardRepository reviewBoardRepository;
    private final ImageRepository imageRepository;
    private final ImageService imageService;


    @SuppressWarnings("unused")
    private Specification<ReviewBoard> search(String kw){
        return new Specification<>(){
            private static final long serialVersionUID = 1L;

            @Override
            public Predicate toPredicate(Root<ReviewBoard> q, CriteriaQuery<?> query, CriteriaBuilder cb) {
                query.distinct(true);
                Join<ReviewBoard, Member> u1 = q.join("author", JoinType.LEFT);
                Join<ReviewBoard, ReviewComment> a = q.join("commentList", JoinType.LEFT);
                Join<ReviewComment, Member> u2 = a.join("author", JoinType.LEFT);
                return cb.or(
                        cb.like(q.get("subject"), "%" + kw + "%"), // 제목
                        cb.like(q.get("content"), "%" + kw + "%"), // 내용
                        cb.like(u1.get("nickname"), "%" + kw + "%"), // 질문 작성자의 닉네임
                        cb.like(a.get("content"), "%" + kw + "%"), // 답변 내용
                        cb.like(u2.get("nickname"), "%" + kw + "%")
                );
            }
        };
    }
    public Page<ReviewBoard>getList(int page, String kw){
        List<Sort.Order> sorts = new ArrayList<>();
        sorts.add(Sort.Order.desc("createDate"));
        Pageable pageable = PageRequest.of(page, 10, Sort.by(sorts));
        return this.reviewBoardRepository.findAllByKeyword(kw, pageable);
    }

    public ReviewBoard getReviewBoard(Long id){
        Optional<ReviewBoard> reviewBoard = this.reviewBoardRepository.findById(id);
        if(reviewBoard.isPresent()){
            return reviewBoard.get();

        }else{
            throw new DataNotFoundException("review not found");
        }
    }
    public Long createReviewBoard(String subject, String content, Member user, List<MultipartFile> reviewImgFileList) throws Exception {
        ReviewBoard q = new ReviewBoard();
        q.setSubject(subject);
        q.setContent(content);
        q.setCreateDate(LocalDateTime.now());
        q.setAuthor(user);
        this.reviewBoardRepository.save(q);

        for (int i = 0; i < reviewImgFileList.size(); i++) {
            Image reviewImage = new Image();
            reviewImage.setReviewBoard(q);

            if (i == 0) {
                reviewImage.setRepImgYn(true);
            } else {
                reviewImage.setRepImgYn(false);
            }
            imageService.saveReviewImage(reviewImage, reviewImgFileList.get(i));
        }
        return q.getId();
    }


    public void delete(ReviewBoard reviewBoard) throws Exception {
        try {
            imageService.deleteImagesByReviewBoardId(reviewBoard.getId());
            this.reviewBoardRepository.delete(reviewBoard);
        } catch (Exception e) {
            throw new Exception("질문 삭제 중 오류가 발생하였습니다.", e);
        }
    }

    @Transactional
    public void modify(ReviewBoard reviewBoard, String subject, String content, List<MultipartFile> reviewImgFileList) throws Exception{
        // 기존 질문의 내용을 수정
        reviewBoard.setSubject(subject);
        reviewBoard.setContent(content);
        reviewBoard.setModifiedDate(LocalDateTime.now());
        // 기존 이미지를 삭제하고 새로운 이미지를 추가

        if (reviewImgFileList != null && !reviewImgFileList.isEmpty()) {
            // 기존 이미지를 삭제
            imageService.deleteImagesByReviewBoardId(reviewBoard.getId());

            // 새로운 이미지를 저장
            for (MultipartFile reviewImg : reviewImgFileList) {
                if (reviewImg != null && !reviewImg.isEmpty()) {
                    Image image = new Image();
                    imageService.saveReviewImage(image, reviewImg);
                    image.setReviewBoard(reviewBoard);
                    imageRepository.save(image);
                }
            }
        }

        // 수정된 내용을 저장
        reviewBoardRepository.save(reviewBoard);
    }
    @Transactional(readOnly = true)
    public ReviewBoardForm getReviewBoardDtl(Long reviewBoardId){
        List<Image> reviewImgList = imageRepository.findByReviewBoardIdOrderByIdAsc(reviewBoardId);
        List<ImageDto> reviewImgDtoList = new ArrayList<>();
        for(Image reviewImage : reviewImgList){
            ImageDto reviewImgDto = ImageDto.of(reviewImage);
            reviewImgDtoList.add(reviewImgDto);
        }
        ReviewBoard reviewBoard = reviewBoardRepository.findById(reviewBoardId).
                orElseThrow(EntityNotFoundException::new);

        ReviewBoardForm reviewBoardForm = ReviewBoardForm.of(reviewBoard);
        reviewBoardForm.setReviewImgDtoList(reviewImgDtoList);
        return reviewBoardForm;
    }

    public void vote(ReviewBoard reviewBoard, Member siteUser) {
        reviewBoard.getVoter().add(siteUser);
        this.reviewBoardRepository.save(reviewBoard);
    }

    public void saveReviewBoard(ReviewBoard reviewBoard) {
        reviewBoardRepository.save(reviewBoard);
    }
    public void cancelVote(ReviewBoard reviewBoard, Member siteUser) {
        reviewBoard.getVoter().remove(siteUser);
        this.reviewBoardRepository.save(reviewBoard);
    }

}
