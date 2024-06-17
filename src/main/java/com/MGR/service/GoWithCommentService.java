package com.MGR.service;

import com.MGR.entity.*;
import com.MGR.exception.DataNotFoundException;
import com.MGR.repository.GoWithCommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class GoWithCommentService {
    private final GoWithCommentRepository goWithCommentRepository;
    private final NotificationService notificationService;

    public GoWithComment create(GoWithBoard goWithBoard, String content, Member member){
        GoWithComment goWithComment = new GoWithComment();
        goWithComment.setContent(content);
        goWithComment.setCreateDate(LocalDateTime.now());
        goWithComment.setMember(member);
        goWithComment.setGoWithBoard(goWithBoard);
        this.goWithCommentRepository.save(goWithComment);
//        notificationService.goWithComment(goWithBoard);
        return goWithComment;
    }

    public GoWithComment getComment(Long id){
        Optional<GoWithComment> goWithComment = this.goWithCommentRepository.findById(id);
        if(goWithComment.isPresent()){
            return goWithComment.get();
        }else{
            throw  new DataNotFoundException("댓글이 없습니다");
        }
    }

    public void modify(GoWithComment goWithComment, String content){
        goWithComment.setContent(content);
        goWithComment.setModifiedDate(LocalDateTime.now());
        this.goWithCommentRepository.save(goWithComment);
    }
    public void delete(GoWithComment goWithComment){
        this.goWithCommentRepository.delete(goWithComment);
    }
}
