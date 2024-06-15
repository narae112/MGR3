package com.MGR.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Getter @ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class ChatRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "chatRoom_id")
    private Long id;


    private String name;

    @ManyToOne
    @JoinColumn(name = "sender_id")
    private Member sender;

    @ManyToOne
    @JoinColumn(name = "receiver_id")
    private Member receiver;

    @Builder
    public ChatRoom(String name, Member sender, Member receiver) {
        this.name = name;
        this.sender = sender;
        this.receiver = receiver;
    }

    public static ChatRoom createRoom(String name, Member sender, Member receiver) {
        return ChatRoom.builder()
                .name(name)
                .sender(sender)
                .receiver(receiver)
                .build();
    }
}
