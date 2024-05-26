package com.MGR.entity;

import com.MGR.constant.EventType;
import com.MGR.dto.EventBoardFormDto;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.text.SimpleDateFormat;
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

    @Column(columnDefinition = "TIMESTAMP")
    private LocalDateTime createDate;

    @Column(columnDefinition = "TIMESTAMP")
    private LocalDateTime modifiedDate = null;

    @Column
    private String startDate;

    @Column
    private String endDate;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "member_id")
    private Member member;

    public String dateFormat(){
        LocalDateTime dateTime = LocalDateTime.now();
        SimpleDateFormat createDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return createDate.format(dateTime);
    }

    public static EventBoard createBoard(EventBoardFormDto boardFormDto, Member member){
        EventBoard board = new EventBoard();

        board.setType(boardFormDto.getType());
        board.setTitle(boardFormDto.getTitle());
        board.setContent(boardFormDto.getContent());
        board.setStartDate(boardFormDto.getStartDate());
        board.setEndDate(boardFormDto.getEndDate());
        board.setCreateDate(LocalDateTime.now());
        board.setMember(member);

        return board;

    }

    public int viewCount() {
        return this.count += 1;
    }


}
