package com.MGR.service;

import com.MGR.constant.LocationCategory;
import com.MGR.constant.ReservationStatus;
import com.MGR.dto.ImageDto;
import com.MGR.dto.MainTicketDto;
import com.MGR.dto.TicketFormDto;
import com.MGR.dto.TicketSearchDto;
import com.MGR.entity.*;
import com.MGR.repository.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
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

    public Ticket updateTicket(Long id, TicketFormDto ticketFormDto, List<MultipartFile> ticketImgFileList) throws Exception {
        Ticket ticket = ticketRepository.findById(id).orElseThrow();
        // 업데이트할 티켓을 가져옵니다.

        // endDate를 변경한 경우
        LocalDate oldEndDate = ticket.getEndDate();
        LocalDate newEndDate = ticketFormDto.getEndDate();

        // endDate가 변경된 경우에만 재고 업데이트
        if (!oldEndDate.equals(newEndDate)) {
            // endDate가 앞당겨진 경우, 기존 endDate 이후의 재고 삭제
            if (newEndDate.isBefore(oldEndDate)) {
                // endDate 이후의 재고 삭제
                inventoryRepository.deleteByTicketAndDateAfter(ticket, newEndDate);
            }
            // endDate가 뒤로 늘어난 경우, 새로운 endDate 이후의 재고 추가
            else {
                // 시작일(startDate)부터 새로운 endDate까지의 기간을 계산하여 재고 생성
                LocalDate currentDate = oldEndDate.plusDays(1); // 기존 endDate 다음 날부터 시작
                while (!currentDate.isAfter(newEndDate)) {
                    // 재고 생성
                    Inventory inventory = Inventory.createInventory(ticket, 100, currentDate);
                    inventoryRepository.save(inventory);

                    // 다음 날짜로 이동
                    currentDate = currentDate.plusDays(1);
                }
            }
        }

        ticket.setName(ticketFormDto.getName());
        ticket.setAdultPrice(ticketFormDto.getAdultPrice());
        ticket.setChildPrice(ticketFormDto.getChildPrice());
        ticket.setLocationCategory(ticketFormDto.getLocationCategory());
        ticket.setMemo(ticketFormDto.getMemo());
        ticket.setStartDate(ticketFormDto.getStartDate());
        ticket.setEndDate(ticketFormDto.getEndDate());

        ticketRepository.save(ticket);

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


 @Bean
    public CommandLineRunner initFullDayTicket(TicketRepository ticketRepository, ImageRepository imageRepository) {
        return args -> {
            boolean isFullDayTicketPresent = ticketRepository.findByName("종일권").isPresent();

            if (!isFullDayTicketPresent) {
                Ticket fullDayTicket = new Ticket();
                fullDayTicket.setName("종일권");
                fullDayTicket.setStartDate(LocalDate.parse("2024-06-10"));
                fullDayTicket.setEndDate(LocalDate.parse("2024-06-30"));
                fullDayTicket.setMemo("많이 이용하세요");
                fullDayTicket.setAdultPrice(50000);
                fullDayTicket.setChildPrice(40000);
                fullDayTicket.setLocationCategory(LocationCategory.SEOUL);

                ticketRepository.save(fullDayTicket);

                String fullDayTicketImgUrl = "/img/logo/placeholder.png";

                Image fullDayTicketImage = new Image();
                fullDayTicketImage.setTicket(fullDayTicket);
                fullDayTicketImage.setImgUrl(fullDayTicketImgUrl);
                fullDayTicketImage.setRepImgYn(true);
                imageRepository.save(fullDayTicketImage);
            }
        };
    }

    @Bean
    public CommandLineRunner initAfternoonTicket(TicketRepository ticketRepository, ImageRepository imageRepository) {
        return args -> {
            boolean isAfternoonTicketPresent = ticketRepository.findByName("오후권").isPresent();

            if (!isAfternoonTicketPresent) {
                Ticket afternoonTicket = new Ticket();
                afternoonTicket.setName("오후권");
                afternoonTicket.setStartDate(LocalDate.parse("2024-06-22"));
                afternoonTicket.setEndDate(LocalDate.parse("2024-06-28"));
                afternoonTicket.setMemo("많이 이용하세요");
                afternoonTicket.setAdultPrice(25000);
                afternoonTicket.setChildPrice(20000);
                afternoonTicket.setLocationCategory(LocationCategory.SEOUL);

                ticketRepository.save(afternoonTicket);

                String afternoonTicketImgUrl = "/img/logo/placeholder.png";

                Image afternoonTicketImage = new Image();
                afternoonTicketImage.setTicket(afternoonTicket);
                afternoonTicketImage.setImgUrl(afternoonTicketImgUrl);
                afternoonTicketImage.setRepImgYn(true);
                imageRepository.save(afternoonTicketImage);
            }
        };
    }

    @Bean
    public CommandLineRunner initMorningTicket(TicketRepository ticketRepository, ImageRepository imageRepository) {
        return args -> {
            boolean isMorningTicketPresent = ticketRepository.findByName("오전권").isPresent();

            if (!isMorningTicketPresent) {
                Ticket morningTicket = new Ticket();
                morningTicket.setName("오전권");
                morningTicket.setStartDate(LocalDate.parse("2024-06-22"));
                morningTicket.setEndDate(LocalDate.parse("2024-07-30"));
                morningTicket.setMemo("많이 이용하세요");
                morningTicket.setAdultPrice(25000);
                morningTicket.setChildPrice(20000);
                morningTicket.setLocationCategory(LocationCategory.SEOUL);

                ticketRepository.save(morningTicket);

                String morningTicketImgUrl = "/img/logo/placeholder.png";

                Image morningTicketImage = new Image();
                morningTicketImage.setTicket(morningTicket);
                morningTicketImage.setImgUrl(morningTicketImgUrl);
                morningTicketImage.setRepImgYn(true);
                imageRepository.save(morningTicketImage);
            }
        };
    }

    @Bean
    public CommandLineRunner initSummerTicket(TicketRepository ticketRepository, ImageRepository imageRepository) {
        return args -> {
            boolean isSummerTicketPresent = ticketRepository.findByName("썸머 티켓").isPresent();

            if (!isSummerTicketPresent) {
                Ticket summerTicket = new Ticket();
                summerTicket.setName("썸머 티켓");
                summerTicket.setStartDate(LocalDate.parse("2024-06-22"));
                summerTicket.setEndDate(LocalDate.parse("2024-08-31"));
                summerTicket.setMemo("많이 이용하세요");
                summerTicket.setAdultPrice(30000);
                summerTicket.setChildPrice(20000);
                summerTicket.setLocationCategory(LocationCategory.BUSAN);

                ticketRepository.save(summerTicket);

                String summerTicketImgUrl = "/img/logo/placeholder.png";

                Image summerTicketImage = new Image();
                summerTicketImage.setTicket(summerTicket);
                summerTicketImage.setImgUrl(summerTicketImgUrl);
                summerTicketImage.setRepImgYn(true);
                imageRepository.save(summerTicketImage);
            }
        };
    }
}
