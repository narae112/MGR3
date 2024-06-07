package com.MGR.service;

import com.MGR.dto.ImageDto;
import com.MGR.dto.MainTicketDto;
import com.MGR.dto.TicketFormDto;
import com.MGR.dto.TicketSearchDto;
import com.MGR.entity.Image;
import com.MGR.entity.Inventory;
import com.MGR.entity.Ticket;
import com.MGR.exception.DuplicateTicketNameException;
import com.MGR.repository.ImageRepository;
import com.MGR.repository.InventoryRepository;
import com.MGR.repository.TicketRepository;
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
        boolean isDuplicate =isTicketNameDuplicated (ticketFormDto.getName());
        if (isDuplicate) {
            throw new DuplicateTicketNameException("중복된 티켓 정보가 존재합니다.");
        }

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


    public boolean isTicketNameDuplicated(String name) {
        return ticketRepository.existsByName(name);
    }
    public boolean isExistedTicketNameDuplicated(String name, Long excludeId) {
        return ticketRepository.existsByNameAndIdNot(name, excludeId);
    }
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


    public Long updateTicket(TicketFormDto ticketFormDto, List<MultipartFile> ticketImgFileList) throws Exception {
        boolean isDuplicate =isExistedTicketNameDuplicated(ticketFormDto.getName(), ticketFormDto.getId());
        if (isDuplicate) {
            throw new DuplicateTicketNameException("중복된 티켓 정보가 존재합니다.");
        }

        // 업데이트할 티켓을 가져옵니다.
        Ticket ticket = ticketRepository.findById(ticketFormDto.getId())
                .orElseThrow(() -> new EntityNotFoundException("티켓을 찾을 수 없습니다. ID: " + ticketFormDto.getId()));
        ticket.updateTicket(ticketFormDto);
        // 이미지 업데이트
        List<Long> ticketImgIds = ticketFormDto.getTicketImgIds();

        if (!ticketImgFileList.isEmpty()) {
            imageService.updateTicketImage(ticketImgIds.get(0), ticketImgFileList.get(0));
        }

        return ticket.getId();
    }


    //삭제
    public void deleteTicket(Long ticketId) {
        // 티켓에 연결된 이미지를 먼저 삭제
        imageService.deleteImagesByTicketId(ticketId);

        // 티켓을 삭제하기 전에 해당 티켓과 관련된 모든 인벤토리 레코드를 삭제
        List<Inventory> inventories = inventoryRepository.findAllByTicketId(ticketId);
        if (!inventories.isEmpty()) {
            inventoryRepository.deleteAll(inventories);
        }

        // 티켓 삭제
        ticketRepository.deleteById(ticketId);
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
