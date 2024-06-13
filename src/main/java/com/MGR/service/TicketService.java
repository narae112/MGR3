package com.MGR.service;

import com.MGR.constant.ReservationStatus;
import com.MGR.dto.ImageDto;
import com.MGR.dto.MainTicketDto;
import com.MGR.dto.TicketFormDto;
import com.MGR.dto.TicketSearchDto;
import com.MGR.entity.*;
import com.MGR.repository.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class TicketService {
    private final TicketRepository ticketRepository;
    private final ImageRepository imageRepository;
    private final ImageService imageService;
    private  final InventoryRepository inventoryRepository;
    private final ReservationTicketRepository reservationTicketRepository;
    private final OrderTicketRepository orderTicketRepository;
@Transactional
    @Scheduled(cron = "0 0 0 * * ?") // 매일 자정에 실행
    public void deleteExpiredCoupons() {
        LocalDate currentDate = LocalDate.now();
        List<Ticket> expiredTicket = ticketRepository.findByEndDateBefore(currentDate);
        // 현재 날짜보다 유효기간이 이전인 티켓들을 조회

        for (Ticket ticket : expiredTicket) {
           ticketRepository.delete(ticket);
        }
        // 조회된 티켓들을 삭제
    }
    
    public Long saveTicket(TicketFormDto ticketFormDto, List<MultipartFile> ticketImgFileList) throws Exception {
        // 티켓 저장
        Ticket ticket = ticketFormDto.createTicket();
        ticketRepository.save(ticket);

        // 이미지 저장
        for (int i = 0; i < ticketImgFileList.size(); i++) {
            Image ticketImage = new Image();
            ticketImage.setTicket(ticket);

            if (i == 0) {
                ticketImage.setRepImgYn(true);
            } else {
                ticketImage.setRepImgYn(false);
            }
            imageService.saveTicketImage(ticketImage, ticketImgFileList.get(i));
        }

        return ticket.getId();
    }


//    public boolean isTicketNameDuplicated(String name) {
//        return ticketRepository.existsByName(name);
//    }
//    public boolean isExistedTicketNameDuplicated(String name, Long excludeId) {
//        return ticketRepository.existsByNameAndIdNot(name, excludeId);
//    }
    //티켓 데이터를 읽어오는 함수
    @Transactional(readOnly = true)
    public TicketFormDto getTicketDtl(Long ticketId){
        List<Image> ticketImgList = imageRepository.findByTicketIdOrderByIdAsc(ticketId);
        List<ImageDto> ticketImgDtoList = new ArrayList<>();
        for(Image ticketImage : ticketImgList){
            ImageDto ticketImgDto = ImageDto.of(ticketImage);
            ticketImgDtoList.add(ticketImgDto);
        }
        Ticket ticket = ticketRepository.findById(ticketId).
                orElseThrow(EntityNotFoundException::new);

        TicketFormDto ticketFormDto = TicketFormDto.of(ticket);
        ticketFormDto.setTicketImgDtoList(ticketImgDtoList);
        return ticketFormDto;
    }

    @Transactional

    public Ticket updateTicket(Long id, TicketFormDto ticketFormDto, List<MultipartFile> ticketImgFileList) throws Exception {
       Ticket ticket = ticketRepository.findById(id).orElseThrow();
       ticket.setName(ticketFormDto.getName());
        ticket.setAdultPrice(ticketFormDto.getAdultPrice());
        ticket.setChildPrice(ticketFormDto.getChildPrice());
        ticket.setLocationCategory(ticketFormDto.getLocationCategory());
        ticket.setMemo(ticketFormDto.getMemo());
        ticket.setStartDate(ticketFormDto.getStartDate());
        ticket.setEndDate(ticketFormDto.getEndDate());
        ticketRepository.save(ticket);
        // 업데이트할 티켓을 가져옵니다.
        Image findImage = imageService.findByTicket(ticket);

        MultipartFile imgFile = ticketImgFileList.get(0);

        imageService.saveTicketImage(findImage, imgFile);

        return ticket;

    }

    @Transactional
    public void deleteTicket(Long ticketId) {
        try {
            // ticketId로 모든 관련 예약 티켓을 가져옴
            List<ReservationTicket> reservationTickets = reservationTicketRepository.findAllByTicketId(ticketId);

            // reservationTickets가 비어 있거나, 모든 티켓의 상태가 RESERVE가 아닌 경우
            boolean canDelete = reservationTickets.isEmpty() || reservationTickets.stream()
                    .noneMatch(ticket -> ticket.getReservationStatus() == ReservationStatus.RESERVE);

            if (canDelete) {
                // 티켓에 연결된 이미지 삭제
                imageService.deleteImagesByTicketId(ticketId);

                // 티켓과 관련된 모든 인벤토리 레코드 삭제
                List<Inventory> inventories = inventoryRepository.findAllByTicketId(ticketId);
                if (!inventories.isEmpty()) {
                    inventoryRepository.deleteAll(inventories);
                }

                // ORDER_TICKET에서 티켓과 관련된 모든 레코드 삭제
                List<OrderTicket> orderTickets = orderTicketRepository.findAllByTicketId(ticketId);
                if (!orderTickets.isEmpty()) {
                    orderTicketRepository.deleteAll(orderTickets);
                }

                // ReservationTicket 삭제
                if (!reservationTickets.isEmpty()) {
                    reservationTicketRepository.deleteAll(reservationTickets);
                }

                // 티켓 삭제
                ticketRepository.deleteById(ticketId);
            } else {
                throw new IllegalStateException("상태가 'RESERVE'인 티켓이 있는 경우 티켓을 삭제할 수 없습니다.");
            }
        } catch (Exception e) {
            // 예외 메시지 출력
            System.out.println("Exception occurred: " + e.getMessage());
            e.printStackTrace();
            throw e;  // 예외를 다시 던져서 트랜잭션 롤백을 유발
        }
    }

    @Transactional(readOnly = true)
    public Page<Ticket> getAdminTicketPage(TicketSearchDto ticketSearchDto, Pageable pageable){
        return ticketRepository.getAdminTicketPage(ticketSearchDto, pageable);
    }

    @Transactional(readOnly = true)
    public Page<MainTicketDto> getMainTicketPage(TicketSearchDto ticketSearchDto, Pageable pageable){
        return ticketRepository.getMainTicketPage(ticketSearchDto, pageable);
    }


}
