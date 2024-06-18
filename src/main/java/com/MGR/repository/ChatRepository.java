package com.MGR.repository;

import com.MGR.entity.Chat;
import com.MGR.entity.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ChatRepository extends JpaRepository<Chat,Long> {
    List<Chat> findAllByRoomId(Long roomId);

    List<Chat> findByRoom(ChatRoom room);

    void deleteByRoomId(Long roomId);

}
