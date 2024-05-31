package com.MGR.repository;

import com.MGR.entity.ReviewBoard;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReviewBoardRepository extends JpaRepository<ReviewBoard, Long> {
    ReviewBoard findBySubject(String subject);
    ReviewBoard findBySubjectAndContent(String subject, String content);
    List<ReviewBoard> findBySubjectLike(String subject);
    Page<ReviewBoard> findAll(Pageable pageable);
    Page<ReviewBoard> findAll(Specification<ReviewBoard> spec, Pageable pageable);

    @Query("select distinct q "
            + "from ReviewBoard q "
            + "left join q.author u1 "
            + "left join q.commentList a "
            + "left join a.author u2 "
            + "where "
            + "   q.subject like %:kw% "
            + "   or q.content like %:kw% "
            + "   or u1.nickname like %:kw% "
            + "   or a.content like %:kw% "
            + "   or u2.nickname like %:kw% ")
    Page<ReviewBoard> findAllByKeyword(@Param("kw") String kw, Pageable pageable);

}
