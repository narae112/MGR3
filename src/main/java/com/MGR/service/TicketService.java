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

        //재고 생성

        // 시작일(startDate)부터 종료일(endDate)까지의 기간을 계산하여 재고 생성
        LocalDate currentDate = ticket.getStartDate();
        LocalDate endDate = ticket.getEndDate();

        while (!currentDate.isAfter(endDate)) {
            // 재고 생성
            Inventory inventory = Inventory.createInventory(ticket, 100, currentDate);
            inventoryRepository.save(inventory);

            // 다음 날짜로 이동
            currentDate = currentDate.plusDays(1);
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

    public Long updateTicket(TicketFormDto ticketFormDto, List<MultipartFile> ticketImgFileList) throws Exception {
//        boolean isDuplicate = isDuplicateTicket(ticketFormDto);
//        if (isDuplicate) {
//            throw new DuplicateTicketNameException("중복된 티켓 정보가 존재합니다.");
//        }

        // 업데이트할 티켓을 가져옵니다.
        Ticket ticketToUpdate = ticketRepository.findById(ticketFormDto.getId())
                .orElseThrow(() -> new EntityNotFoundException("티켓을 찾을 수 없습니다. ID: " + ticketFormDto.getId()));

        // 티켓 정보 업데이트
        ticketToUpdate.updateTicket(ticketFormDto);
        ticketRepository.save(ticketToUpdate);

        // 이미지 업데이트
        List<Long> ticketImgIds = ticketFormDto.getTicketImgIds();
        for (int i = 0; i < ticketImgIds.size(); i++) {
            Long imgId = ticketImgIds.get(i);
            MultipartFile imgFile = ticketImgFileList.get(i);
            imageService.updateTicketImage(imgId, imgFile);
        }

        // 재고 업데이트

        // 기존 티켓의 endDate
        LocalDate oldEndDate = ticketToUpdate.getEndDate();
        // 업데이트된 티켓의 endDate
        LocalDate newEndDate = ticketFormDto.getEndDate();

        // endDate를 변경한 경우
        if (!oldEndDate.equals(newEndDate)) {
            // endDate가 앞당겨진 경우, 기존 endDate 이후의 재고 삭제
            if (newEndDate.isBefore(oldEndDate)) {
                // endDate 이후의 재고 삭제
                inventoryRepository.deleteByTicketAndDateAfter(ticketToUpdate, newEndDate);
            }
            // endDate가 뒤로 늘어난 경우, 새로운 endDate 이후의 재고 추가
            else {
                // 시작일(startDate)부터 새로운 endDate까지의 기간을 계산하여 재고 생성
                LocalDate currentDate = oldEndDate.plusDays(1); // 기존 endDate 다음 날부터 시작
                while (!currentDate.isAfter(newEndDate)) {
                    // 재고 생성
                    Inventory inventory = Inventory.createInventory(ticketToUpdate, 100, currentDate);
                    inventoryRepository.save(inventory);

                    // 다음 날짜로 이동
                    currentDate = currentDate.plusDays(1);
                }
            }
        }

        return ticketToUpdate.getId();
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
