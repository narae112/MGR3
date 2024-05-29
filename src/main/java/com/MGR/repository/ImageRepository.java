package com.MGR.repository;

import com.MGR.entity.EventBoard;
import com.MGR.entity.Image;
import com.MGR.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ImageRepository extends JpaRepository<Image, Long> {
    List<Image> findByTicketIdOrderByIdAsc(Long ticketId);
    //상품이미지 아이디의 오름차순으로 가져오는 쿼리 메소드
    
    void deleteByTicketId(Long ticketId);
    
    Image findByIdAndRepImgYn(Long ticketId, Boolean repImgYn);
    //주문상품의 대표이미지를 보여주기 위한 쿼리

    List<Image> findByEventBoard(EventBoard eventBoard);
}
