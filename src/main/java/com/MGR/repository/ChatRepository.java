package com.MGR.repository;

import com.MGR.entity.Chat;
import com.MGR.entity.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

public interface ChatRepository extends JpaRepository<Chat, Long> {
    List<Chat> findAllByRoomId(Long roomId);

    List<Chat> findByRoom(ChatRoom room);

    void deleteByRoomId(Long roomId);

    @Modifying
    @Transactional
    @Query("DELETE FROM Chat c WHERE c.room.id = :roomId AND c.sendDate < :sendDate")
    void deleteByRoomIdAndSendDateBefore(Long roomId, LocalDateTime sendDate);
}
