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

    @ElementCollection
    private List<String> memberEmails;

    @Builder
    public ChatRoom(String name, List<String> memberEmails) {
        this.name = name;
        this.memberEmails = memberEmails;
    }

    public static ChatRoom createRoom(String name, List<String> memberEmails) {
        return ChatRoom.builder()
                .name(name)
                .memberEmails(memberEmails)
                .build();
    }
}
