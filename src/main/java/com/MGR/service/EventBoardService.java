package com.MGR.service;

import com.MGR.dto.EventBoardFormDto;
import com.MGR.entity.EventBoard;
import com.MGR.entity.Member;
import com.MGR.repository.EventBoardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class EventBoardService {

    private final EventBoardRepository eventBoardRepository;


    public void saveBoard(EventBoardFormDto boardFormDto,Member member) {
        EventBoard board = EventBoard.createBoard(boardFormDto, member);
        eventBoardRepository.save(board);
    }

    public List<EventBoard> findAll() {
        return  eventBoardRepository.findAll();
    }

    public Page<EventBoard> getBoardList(int page) {

        List<Sort.Order> sorts = new ArrayList<>();
        sorts.add(Sort.Order.desc("createDate"));
        Pageable pageable = PageRequest.of(page, 5, Sort.by(sorts));

        return eventBoardRepository.findAll(pageable);
    }

    public Optional<EventBoard> findById(Long id) {
        Optional<EventBoard> eventBoard = eventBoardRepository.findById(id);
        return eventBoard;
    }

    public void delete(EventBoard eventBoard) {
        eventBoardRepository.delete(eventBoard);
    }
}
