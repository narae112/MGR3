package com.MGR.service;

import com.MGR.entity.Chat;
import com.MGR.entity.ChatRoom;
import com.MGR.entity.Member;
import com.MGR.repository.ChatRepository;
import com.MGR.repository.ChatRoomRepository;
import com.MGR.security.PrincipalDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ChatService {
    private final ChatRoomRepository roomRepository;
    private final ChatRepository chatRepository;
    private final MemberService memberService;

    @Bean
    public CommandLineRunner global() {
        return args -> {
            if (roomRepository.findByIsGlobalTrue().isEmpty()) {
                // 전체 채팅 방이 없으면 생성
                ChatRoom globalRoom = ChatRoom.createGlobalRoom();
                roomRepository.save(globalRoom);
            }
        };
    }

    public List<ChatRoom> findAllRoom() {
        return roomRepository.findAll();
    }

    public ChatRoom findRoomById(Long id) {
        return roomRepository.findById(id).orElseThrow();
    }

    public void createRoom(String name, PrincipalDetails member, String receiverNickname) {
        Member sender = memberService.findById(member.getId()).orElseThrow();
        Member receiver = memberService.findByNickname(receiverNickname).orElseThrow();
        roomRepository.save(ChatRoom.createRoom(name, sender, receiver));
    }

    // 채팅방 채팅내용 불러오기
    public List<Chat> findAllChatByRoomId(Long roomId) {
        return chatRepository.findAllByRoomId(roomId);
    }

    //채팅 메시지 생성
    public Chat createChat(Long roomId, String sender, String senderEmail, String message, String profileImgUrl) {
        ChatRoom room = roomRepository.findById(roomId).orElseThrow(() -> new IllegalArgumentException("Invalid room ID")); // 방 찾기 -> 없는 방일 경우 예외처리
        return chatRepository.save(Chat.createChat(room, sender, senderEmail, message, profileImgUrl));
    }

    public List<ChatRoom> findAllRoomsByMember(Long memberId) {
        return roomRepository.findBySenderIdOrReceiverId(memberId);
    }

    public List<Chat> findAllGlobalChats () {
        ChatRoom chatRoom = roomRepository.findByIsGlobalTrue().orElseThrow();
        return chatRepository.findAllByRoomId(chatRoom.getId());
    }

    public void sendMessage(Long roomId, Long id, String message) {
        ChatRoom room = roomRepository.findById(roomId).orElseThrow(() ->
                new IllegalStateException("Chat room not found"));
        Member sender = memberService.findById(id).orElseThrow(() ->
                new IllegalStateException("Member not found"));
        Chat chat = Chat.builder()
                .room(room)
                .sender(sender.getNickname())
                .senderEmail(sender.getEmail())
                .message(message)
                .build();
        chatRepository.save(chat);
    }

    @Transactional
    public void deleteChatRoomMemberId(Long roomId, Long memberId) {
        ChatRoom chatRoom = roomRepository.findById(roomId).orElseThrow();

        if (chatRoom.getSender() != null && chatRoom.getSender().getId().equals(memberId)) {
            chatRoom.setSender(null);

        } else if (chatRoom.getReceiver() != null && chatRoom.getReceiver().getId().equals(memberId)) {
            chatRoom.setReceiver(null);
        }

        // sender 와 receiver 가 둘 다 null 이면 관련된 Chat 레코드 삭제
        if (chatRoom.getReceiver() == null && chatRoom.getSender() == null) {
            chatRepository.deleteByRoomId(roomId);
            roomRepository.deleteById(roomId);
        } else {
            roomRepository.save(chatRoom);
        }
    }


}