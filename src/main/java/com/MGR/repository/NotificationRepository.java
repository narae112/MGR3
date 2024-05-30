package com.MGR.repository;

import com.MGR.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Arrays;
import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findByBoardId(Long boardId);
    List<Notification> findByMemberId(Long memberId);
}
