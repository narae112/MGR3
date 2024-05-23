package com.MGR.service;

import com.MGR.entity.Member;
import com.MGR.entity.QnABoard;
import com.MGR.entity.QnAComment;
import com.MGR.exception.DataNotFoundException;
import com.MGR.repository.QnAQuestionRepository;
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

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class QnAQuestionService {

    private final QnAQuestionRepository qnaquestionRepository;

    @SuppressWarnings("unused")
    private Specification<QnABoard> search(String kw){
        return new Specification<>(){
            private static final long serialVersionUID = 1L;

            @Override
            public Predicate toPredicate(Root<QnABoard> q, CriteriaQuery<?> query, CriteriaBuilder cb){
                query.distinct(true);//중복제거
                Join<QnABoard, Member> u1 = q.join("author", JoinType.LEFT);
                Join<QnABoard, Member>a = q.join("answerList", JoinType.LEFT);
                Join<QnAComment, Member>u2 = a.join("author", JoinType.LEFT);
                return cb.or(cb.like(q.get("subject"), "%" + kw + "%"), // 제목
                        cb.like(q.get("content"), "%" + kw + "%"), // 내용
                        cb.like(u1.get("name"), "%" + kw + "%"), // 질문 작성자
                        cb.like(a.get("content"), "%" + kw + "%"), // 답변 내용
                        cb.like(u2.get("name"), "%" + kw + "%")); // 답변 작성자
            }
        };
    }
    public Page<QnABoard>getList(int page, String kw){
        List<Sort.Order> sorts = new ArrayList<>();
        sorts.add(Sort.Order.desc("createDate"));
        Pageable pageable = PageRequest.of(page, 10, Sort.by(sorts));
        return this.qnaquestionRepository.findAllByKeyword(kw, pageable);
    }
    public QnABoard getQnaBoard(Integer id){
        Optional<QnABoard> qnABoard = this.qnaquestionRepository.findById(id);
        if(qnABoard.isPresent()){
            return qnABoard.get();
        }else{
            throw new DataNotFoundException("question not found");
        }
    }
    public void create(String subject, String content, Member user){
        QnABoard q = new QnABoard();
        q.setSubject(subject);
        q.setContent(content);
        q.setCreateDate(LocalDateTime.now());
        q.setAuthor(user);
        this.qnaquestionRepository.save(q);
    }
    public void modify(QnABoard question, String subject, String content){
        question.setSubject(subject);
        question.setContent(content);
        question.setModifiedDate(LocalDateTime.now());
        this.qnaquestionRepository.save(question);
    }
    public void delete(QnABoard qnABoard){
        this.qnaquestionRepository.delete(qnABoard);
    }
    public void vote(QnABoard question, Member siteUser){
        question.getVoter().add(siteUser);
        this.qnaquestionRepository.save(question);
    }
}
