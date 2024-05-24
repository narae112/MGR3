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

    @Query("SELECT DISTINCT q FROM QnaQuestion q " +
            "LEFT JOIN q.author u1 " +
            "LEFT JOIN q.answerList a " +
            "LEFT JOIN a.author u2 " +
            "WHERE q.subject LIKE %:kw% " +
            "OR q.content LIKE %:kw% " +
            "OR u1.name LIKE %:kw% " +
            "OR a.content LIKE %:kw% " +
            "OR u2.name LIKE %:kw%")
    Page<QnaQuestion> findAllByKeyword(@Param("kw") String kw, Pageable pageable);
}
