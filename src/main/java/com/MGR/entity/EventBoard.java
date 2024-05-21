package com.MGR.entity;

import com.MGR.constant.EventType;
import com.MGR.dto.EventBoardFormDto;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Setter @Getter
public class EventBoard {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private EventType type;

    @Column(length = 50)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Column
    private int count = 0; //조회수 초기값 0 설정

    @Column(columnDefinition = "DATE")
    private LocalDateTime createDate;

    @Column(columnDefinition = "DATE")
    private LocalDateTime modifiedDate = null;

    private String startDate;

    private String endDate;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

//    @PrePersist
//    private void onCreate() {
//        createDate = LocalDateTime.now();
//    }
//
//    @PreUpdate
//    private void onUpdate() {
//        modifiedDate = LocalDateTime.now();
//    }

    public static EventBoard createBoard(EventBoardFormDto boardFormDto, Member member){
        EventBoard board = new EventBoard();
        board.setType(boardFormDto.getType());
        board.setTitle(boardFormDto.getTitle());
        board.setContent(boardFormDto.getContent());
        board.setStartDate(boardFormDto.getStartDate());
        board.setEndDate(boardFormDto.getEndDate());
        board.setCreateDate(LocalDateTime.now());
        board.setCreateDate(LocalDateTime.now());
        board.setMember(member);

        return board;

    }

}
