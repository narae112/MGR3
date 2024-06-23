package com.MGR.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Getter @Setter
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

    @Column
    private Boolean isGlobal = false;

    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<Chat> chats;

    @Builder
    public ChatRoom(String name, Member sender, Member receiver, boolean isGlobal) {
        this.name = name;
        this.sender = sender;
        this.receiver = receiver;
        this.isGlobal = isGlobal;
    }

    public static ChatRoom createRoom(String name, Member sender, Member receiver) {
        return ChatRoom.builder()
                .name(name)
                .sender(sender)
                .receiver(receiver)
                .build();
    }

    public static ChatRoom createGlobalRoom() {
        return ChatRoom.builder()
                .name("전체 채팅방")
                .isGlobal(true)
                .build();
    }
}
