package com.MGR.repository;

import com.MGR.entity.Chat;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatRepository extends JpaRepository<Chat,Long> {
    List<Chat> findAllByRoomId(Long roomId);
}
