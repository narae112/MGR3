package com.MGR.repository;

import com.MGR.entity.QnAQuestion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface QnAQuestionRepository extends JpaRepository<QnAQuestion, Integer> {
    QnAQuestion findBySubject(String subject);
    QnAQuestion findBySubjectAndContent(String subject, String content);
    List<QnAQuestion> findBySubjectLise(String subject);
    Page<QnAQuestion> findAll(Pageable pageable);
    Page<QnAQuestion> findAll(Specification<QnAQuestion> spec, Pageable pageable);

    @Query("select "
            + "distinct q "
            + "from QnABoard q "
            + "left outer join Member u1 on q.author=u1 "
            + "left outer join QnAComment a on a.qnABoard=q "
            + "left outer join Member u2 on a.author=u2 "
            + "where "
            + "   q.subject like %:kw% "
            + "   or q.content like %:kw% "
            + "   or u1.name like %:kw% "
            + "   or a.content like %:kw% "
            + "   or u2.name like %:kw% ")
    Page<QnAQuestion> findAllByKeyword(@Param("kw") String kw, Pageable pageable);
}
