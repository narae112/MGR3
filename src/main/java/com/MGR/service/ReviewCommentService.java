package com.MGR.service;

import com.MGR.entity.Member;
import com.MGR.entity.ReviewBoard;
import com.MGR.entity.ReviewComment;
import com.MGR.exception.DataNotFoundException;
import com.MGR.repository.ReviewCommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class ReviewCommentService {
    private final ReviewCommentRepository reviewCommentRepository;
    private final NotificationService notificationService;

    public ReviewComment create(ReviewBoard reviewBoard, String content, Member author){
        ReviewComment reviewComment = new ReviewComment();
        reviewComment.setContent(content);
        reviewComment.setCreateDate(LocalDateTime.now());
        reviewComment.setAuthor(author);
        reviewComment.setReviewBoard(reviewBoard);
        this.reviewCommentRepository.save(reviewComment);
        notificationService.reviewComment(reviewBoard);
        return reviewComment;
    }

    public ReviewComment getComment(Long id){
        Optional<ReviewComment> reviewComment = this.reviewCommentRepository.findById(id);
        if(reviewComment.isPresent()){
            return reviewComment.get();
        }else{
            throw  new DataNotFoundException("comment not found");
        }
    }

    public void modify(ReviewComment reviewComment, String content){
        reviewComment.setContent(content);
        reviewComment.setModifiedDate(LocalDateTime.now());
        this.reviewCommentRepository.save(reviewComment);
    }
    public void delete(ReviewComment reviewComment){
        this.reviewCommentRepository.delete(reviewComment);
    }
}
