package com.MGR.repository;

import com.MGR.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Arrays;
import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findByBoardId(Long boardId);
    List<Notification> findByMemberId(Long memberId);
    List<Notification> findByMemberIdOrderByCreatedDateDesc(Long memberId);

    // JPQL 쿼리를 사용하여 특정 회원의 알림 수를 계산
    @Query("SELECT COUNT(n) FROM Notification n WHERE n.memberId = :memberId")
    int countByMemberId(Long memberId);
}
