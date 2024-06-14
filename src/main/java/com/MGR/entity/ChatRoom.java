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

    private String memberEmail;

    @Builder
    public ChatRoom(String name, String memberEmail) {
        this.name = name;
        this.memberEmail = memberEmail;
    }

    public static ChatRoom createRoom(String name, String memberEmail) {
        return ChatRoom.builder()
                .name(name)
                .memberEmail(memberEmail)
                .build();
    }
}
