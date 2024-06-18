package com.MGR.repository;

import com.MGR.entity.Member;
import com.MGR.entity.MemberCoupon;
import com.MGR.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;


import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByName(String name);
    Optional<Member> findByEmail(String email);
    Optional<Member> findByNickname(String nickname);

    Optional<Member> findByOauth2Id(String oauth2Id);
    List<Member> findAll();


//    @Query("SELECT m FROM Member m WHERE m.birth = :today")
//    List<Member> findByBirth(String today);

    @Query("SELECT m FROM Member m WHERE SUBSTRING(m.birth, 6, 5) = :today")
    List<Member> findByBirth(@Param("today") String today);

    Page<Member> findByRole(String role, Pageable pageable);

}
