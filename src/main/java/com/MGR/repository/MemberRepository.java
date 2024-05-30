package com.MGR.repository;

import com.MGR.entity.Member;
import com.MGR.entity.MemberCoupon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByName(String name);
    Optional<Member> findByEmail(String email);
    Optional<Member> findByNickname(String nickname);

    Optional<Member> findByOauth2Id(String oauth2Id);

}
