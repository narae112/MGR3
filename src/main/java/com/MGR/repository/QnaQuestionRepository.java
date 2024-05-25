package com.MGR.repository;

import com.MGR.entity.QnaQuestion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface QnaQuestionRepository extends JpaRepository<QnaQuestion, Long> {
    QnaQuestion findBySubject(String subject);
    QnaQuestion findBySubjectAndContent(String subject, String content);
    List<QnaQuestion> findBySubjectLike(String subject);
    Page<QnaQuestion> findAll(Pageable pageable);
    Page<QnaQuestion> findAll(Specification<QnaQuestion> spec, Pageable pageable);

    @Query("select distinct q "
            + "from QnaQuestion q "
            + "left join q.author u1 "
            + "left join q.answerList a "
            + "left join a.author u2 "
            + "where "
            + "   q.subject like %:kw% "
            + "   or q.content like %:kw% "
            + "   or u1.nickname like %:kw% "
            + "   or a.content like %:kw% "
            + "   or u2.nickname like %:kw% ")
    Page<QnaQuestion> findAllByKeyword(@Param("kw") String kw, Pageable pageable);

}
