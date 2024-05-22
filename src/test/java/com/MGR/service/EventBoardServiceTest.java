package com.MGR.service;

import com.MGR.constant.EventType;
import com.MGR.entity.EventBoard;
import com.MGR.repository.EventBoardRepository;
import com.MGR.repository.MemberRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class EventBoardServiceTest {

    @Autowired EventBoardRepository eventBoardRepository;
    @Autowired MemberRepository memberRepository;

    EventBoard createBoard(){

        EventBoard board = new EventBoard();

        board.setType(EventType.EVENT);
        board.setTitle("test title");
        board.setContent("test content");
        board.setStartDate("2024-05-20");
        board.setEndDate("2024-05-25");
        board.setCreateDate(LocalDateTime.now());
        board.setCreateDate(LocalDateTime.now());

        board.setMember(memberRepository.findById(1L).orElseThrow());

        return board;
    }

    @Test
    void saveBoard() {
        EventBoard board = createBoard();
        EventBoard saveBoard = eventBoardRepository.save(board);
        Assertions.assertThat(saveBoard.getId()).isEqualTo(1L);
    }

    @Test
    void findAll() {
        EventBoard board = createBoard();
        EventBoard saveBoard = eventBoardRepository.save(board);
        List<EventBoard> boardList = eventBoardRepository.findAll();

        Assertions.assertThat(boardList.size()).isEqualTo(1);

        for (EventBoard eventBoard : boardList) {
            Long memberId = eventBoard.getMember().getId();
            System.out.println("memberId = " + memberId);
        }

    }



}