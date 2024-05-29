package com.MGR.service;

import com.MGR.dto.EventBoardFormDto;
import com.MGR.dto.ImageDto;
import com.MGR.dto.QnaQuestionForm;
import com.MGR.dto.TicketFormDto;
import com.MGR.entity.*;
import com.MGR.exception.DataNotFoundException;
import com.MGR.repository.ImageRepository;
import com.MGR.repository.QnaQuestionRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.criteria.*;
import lombok.RequiredArgsConstructor;
import org.springframework.cglib.core.Local;
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
public class QnaQuestionService {

    private final QnaQuestionRepository qnaquestionRepository;
    private final ImageRepository imageRepository;
    private final ImageService imageService;


    @SuppressWarnings("unused")
    private Specification<QnaQuestion> search(String kw){
        return new Specification<>(){
            private static final long serialVersionUID = 1L;

            @Override
            public Predicate toPredicate(Root<QnaQuestion> q, CriteriaQuery<?> query, CriteriaBuilder cb) {
                query.distinct(true);
                Join<QnaQuestion, Member> u1 = q.join("author", JoinType.LEFT);
                Join<QnaQuestion, QnaAnswer> a = q.join("answerList", JoinType.LEFT);
                Join<QnaAnswer, Member> u2 = a.join("author", JoinType.LEFT);
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
    public Page<QnaQuestion>getList(int page, String kw){
        List<Sort.Order> sorts = new ArrayList<>();
        sorts.add(Sort.Order.desc("createDate"));
        Pageable pageable = PageRequest.of(page, 10, Sort.by(sorts));
        return this.qnaquestionRepository.findAllByKeyword(kw, pageable);
    }

    public QnaQuestion getQnaQuestion(Long id){
        Optional<QnaQuestion> question = this.qnaquestionRepository.findById(id);
        if(question.isPresent()){
            return question.get();

        }else{
            throw new DataNotFoundException("question not found");
        }
    }
    public Long createQuestion(String subject, String content, Member user, List<MultipartFile> reviewImgFileList) throws Exception {
        QnaQuestion q = new QnaQuestion();
        q.setSubject(subject);
        q.setContent(content);
        q.setCreateDate(LocalDateTime.now());
        q.setAuthor(user);
        this.qnaquestionRepository.save(q);

        for (int i = 0; i < reviewImgFileList.size(); i++) {
            Image reviewImage = new Image();
            reviewImage.setQnaQuestion(q);

            if (i == 0) {
                reviewImage.setRepImgYn(true);
            } else {
                reviewImage.setRepImgYn(false);
            }
            imageService.saveReviewImage(reviewImage, reviewImgFileList.get(i));
        }
        return q.getId();
    }


    public void delete(QnaQuestion qnaQuestion) throws Exception {
        try {
            imageService.deleteImagesByQnaQuestionId(qnaQuestion.getId());
            this.qnaquestionRepository.delete(qnaQuestion);
        } catch (Exception e) {
            throw new Exception("질문 삭제 중 오류가 발생하였습니다.", e);
        }
    }

    @Transactional
    public void modify(QnaQuestion question, String subject, String content, List<MultipartFile> reviewImgFileList) throws Exception{
        // 기존 질문의 내용을 수정
        question.setSubject(subject);
        question.setContent(content);
        question.setModifiedDate(LocalDateTime.now());
        // 기존 이미지를 삭제하고 새로운 이미지를 추가

        if (reviewImgFileList != null && !reviewImgFileList.isEmpty()) {
            // 기존 이미지를 삭제
            imageService.deleteImagesByQnaQuestionId(question.getId());

            // 새로운 이미지를 저장
            for (MultipartFile reviewImg : reviewImgFileList) {
                if (reviewImg != null && !reviewImg.isEmpty()) {
                    Image image = new Image();
                    imageService.saveReviewImage(image, reviewImg);
                    image.setQnaQuestion(question);
                    imageRepository.save(image);
                }
            }
        }

        // 수정된 내용을 저장
        qnaquestionRepository.save(question);
    }
    @Transactional(readOnly = true)
    public QnaQuestionForm getQuestionDtl(Long qnaQuestionId){
        List<Image> reviewImgList = imageRepository.findByQnaQuestionIdOrderByIdAsc(qnaQuestionId);
        List<ImageDto> reviewImgDtoList = new ArrayList<>();
        for(Image reviewImage : reviewImgList){
            ImageDto reviewImgDto = ImageDto.of(reviewImage);
            reviewImgDtoList.add(reviewImgDto);
        }
        QnaQuestion qnaQuestion = qnaquestionRepository.findById(qnaQuestionId).
                orElseThrow(EntityNotFoundException::new);

        QnaQuestionForm qnaQuestionForm = QnaQuestionForm.of(qnaQuestion);
        qnaQuestionForm.setReviewImgDtoList(reviewImgDtoList);
        return qnaQuestionForm;
    }

    public void vote(QnaQuestion question, Member siteUser) {
        question.getVoter().add(siteUser);
        this.qnaquestionRepository.save(question);
    }
}
