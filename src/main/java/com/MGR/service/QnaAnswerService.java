package com.MGR.service;

import com.MGR.entity.Member;
import com.MGR.entity.QnaAnswer;
import com.MGR.entity.QnaQuestion;
import com.MGR.exception.DataNotFoundException;
import com.MGR.repository.QnaAnswerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class QnaAnswerService {
    private final QnaAnswerRepository qnaAnswerRepository;

    public QnaAnswer create(QnaQuestion qnaQuestion, String content, Member author){
        QnaAnswer qnaAnswer = new QnaAnswer();
        qnaAnswer.setContent(content);
        qnaAnswer.setCreateDate(LocalDateTime.now());
        qnaAnswer.setAuthor(author);
        qnaAnswer.setQnaQuestion(qnaQuestion);
        this.qnaAnswerRepository.save(qnaAnswer);
        return qnaAnswer;
    }

    public QnaAnswer getAnswer(Long id){
        Optional<QnaAnswer> qnaAnswer = this.qnaAnswerRepository.findById(id);
        if(qnaAnswer.isPresent()){
            return qnaAnswer.get();
        }else{
            throw  new DataNotFoundException("answer not found");
        }
    }

    public void modify(QnaAnswer qnaAnswer, String content){
        qnaAnswer.setContent(content);
        qnaAnswer.setModifiedDate(LocalDateTime.now());
        this.qnaAnswerRepository.save(qnaAnswer);
    }
    public void delete(QnaAnswer qnaAnswer){
        this.qnaAnswerRepository.delete(qnaAnswer);
    }
}
