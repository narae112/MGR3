package com.MGR.entity;

import com.MGR.constant.EventType;
import com.MGR.dto.EventBoardFormDto;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
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

    @Column
    private Boolean isEventCurrent; //현재 날짜를 기준으로 이벤트가 진행중인지 표시하는 변수

    public void isEventActive(String startDate, String endDate) {
        LocalDate now = LocalDate.now(); //현재 날짜를 구해서

        this.startDate = startDate;
        this.endDate = endDate;
        this.isEventCurrent = now.isEqual(LocalDate.parse(startDate)) || now.isAfter(LocalDate.parse(startDate))
                &&  now.isEqual(LocalDate.parse(endDate)) || now.isBefore(LocalDate.parse(endDate));
        //String 으로 되어있는 시작 날짜와 끝 날짜를 LocalDate 타입으로 변경 후 날짜를 비교함
        //-> 시작~끝 기간 안에 현재 날짜가 없으면 false
    }

    public EventBoard() {}

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

        board.isEventActive(boardFormDto.getStartDate(),boardFormDto.getEndDate());

        return board;

    }

    public int viewCount() {
        return this.count += 1;
    }

    public EventBoard(EventType type, String title, String content, String startDate, String endDate, Member member) {
        this.type = type;
        this.title = title;
        this.content = content;
        this.createDate = LocalDateTime.now();
        this.startDate = startDate;
        this.endDate = endDate;
        this.member = member;
        this.isEventCurrent = true;
        this.count = 0;
    }
}
