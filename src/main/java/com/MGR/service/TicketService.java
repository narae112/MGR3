package com.MGR.service;

import com.MGR.dto.ImageDto;
import com.MGR.dto.MainTicketDto;
import com.MGR.dto.TicketFormDto;
import com.MGR.dto.TicketSearchDto;
import com.MGR.entity.Image;
import com.MGR.entity.Ticket;
import com.MGR.repository.ImageRepository;
import com.MGR.repository.TicketRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.hibernate.proxy.EntityNotFoundDelegate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class TicketService {
    private final TicketRepository ticketRepository;
    private final ImageRepository imageRepository;
    private final ImageService imageService;


    public Long saveTicket(TicketFormDto ticketFormDto, List<MultipartFile> ticketImgFileList) throws Exception{
        // 티켓등록
        Ticket ticket = ticketFormDto.createTicket();
        ticketRepository.save(ticket);

        //이미지 등록
        for(int i = 0; i< ticketImgFileList.size(); i++){
            Image ticketImage = new Image();
            ticketImage.setTicket(ticket);

            if(i == 0){
                ticketImage.setRepImgYn(true);
            }else{
                ticketImage.setRepImgYn(false);
            }

            imageService.saveTicketImage(ticketImage, ticketImgFileList.get(i));
        }
        return  ticket.getId();
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
        //티켓수정
        Ticket ticket = ticketRepository.findById(ticketFormDto.getId())
                .orElseThrow(EntityNotFoundException::new);
        ticket.updateTicket(ticketFormDto);
        List<Long> ticketImgIds = ticketFormDto.getTicketImgIds();
        //이미지번호
        //이미지 수정
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
        return ticket.getId(); // 업데이트된 티켓의 ID 반환
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
