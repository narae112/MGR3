package com.MGR.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

@Getter @Setter
@RequiredArgsConstructor
@Entity
public class Chat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "chat_id")
    private Long id;

//    @ManyToOne(fetch = FetchType.EAGER)
//    @JoinColumn(name = "room_id")
//    @JsonBackReference
//    private ChatRoom room;

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "room_id")
//    @JsonBackReference
//    private ChatRoom room;
    private Long roomId;

    @ManyToOne
    @JoinColumn(name = "sender_id")
    private Member sender;

    private String senderEmail;

    private String profileImgUrl;

    @Column(columnDefinition = "TEXT")
    private String message;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime sendDate;

    @Builder
    public Chat(Long roomId, Member sender, String senderEmail, String message, String profileImgUrl) {
        this.roomId = roomId;
        this.sender = sender;
        this.senderEmail = senderEmail;
        this.message = message;
        this.sendDate = LocalDateTime.now();
        this.profileImgUrl = profileImgUrl;
    }

    /**
     * 채팅 생성
     * @param roomId 채팅 방
     * @param sender 보낸이
     * @param message 내용
     * @return Chat Entity
     */
    public static Chat createChat(Long roomId, Member sender, String senderEmail, String message, String profileImgUrl) {
        return Chat.builder()
                .roomId(roomId)
                .sender(sender)
                .senderEmail(senderEmail)
                .message(message)
                .profileImgUrl(profileImgUrl)
                .build();
    }

}
