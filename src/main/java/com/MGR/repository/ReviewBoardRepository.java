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

    @Query("SELECT rb FROM ReviewBoard rb ORDER BY rb.count DESC")
    Page<ReviewBoard> findAllOrderByCountDesc(Pageable pageable);

    @Query("SELECT rb FROM ReviewBoard rb LEFT JOIN rb.voter v GROUP BY rb ORDER BY COUNT(v) DESC")
    Page<ReviewBoard> findAllOrderByVoterCountDesc(Pageable pageable);

    @Query("SELECT rb FROM ReviewBoard rb ORDER BY rb.createDate DESC")
    Page<ReviewBoard> findAllOrderByCreateDateDesc(Pageable pageable);

    @Query("select distinct q from ReviewBoard q " +
            "left join q.author u1 " +
            "left join q.commentList a " +
            "left join a.author u2 " +
            "where q.subject like %:kw% " +
            "or q.content like %:kw% " +
            "or u1.nickname like %:kw% " +
            "or a.content like %:kw% " +
            "or u2.nickname like %:kw% " +
            "order by q.createDate desc")
    Page<ReviewBoard> findByKeywordOrderByCreateDateDesc(@Param("kw") String kw, Pageable pageable);

    @Query("select distinct q from ReviewBoard q " +
            "left join q.author u1 " +
            "left join q.commentList a " +
            "left join a.author u2 " +
            "where q.subject like %:kw% " +
            "or q.content like %:kw% " +
            "or u1.nickname like %:kw% " +
            "or a.content like %:kw% " +
            "or u2.nickname like %:kw% " +
            "order by q.count desc")
    Page<ReviewBoard> findByKeywordOrderByCountDesc(@Param("kw") String kw, Pageable pageable);

    @Query("select distinct q from ReviewBoard q " +
            "left join q.author u1 " +
            "left join q.commentList a " +
            "left join a.author u2 " +
            "left join q.voter v " +
            "where q.subject like %:kw% " +
            "or q.content like %:kw% " +
            "or u1.nickname like %:kw% " +
            "or a.content like %:kw% " +
            "or u2.nickname like %:kw% " +
            "group by q " +
            "order by count(v) desc")
    Page<ReviewBoard> findByKeywordOrderByVoterCountDesc(@Param("kw") String kw, Pageable pageable);
}