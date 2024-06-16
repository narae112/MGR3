package com.MGR.repository;

import com.MGR.entity.Chat;
import com.MGR.entity.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
    @Query("SELECT cr FROM ChatRoom cr WHERE cr.sender.id = :memberId OR cr.receiver.id = :memberId")
    List<ChatRoom> findBySenderIdOrReceiverId(@Param("memberId") Long memberId);

    Optional<Object> findByIsGlobal(boolean b);

//    @Query("SELECT cr FROM ChatRoom cr WHERE cr.isGlobal = true")
//    Optional<ChatRoom> findGlobalChatRoom();

    Optional<ChatRoom> findByIsGlobalTrue();
}
