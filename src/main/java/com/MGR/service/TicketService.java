package com.MGR.service;

import com.MGR.dto.ImageDto;
import com.MGR.dto.MainTicketDto;
import com.MGR.dto.TicketFormDto;
import com.MGR.dto.TicketSearchDto;
import com.MGR.entity.Image;
import com.MGR.entity.Ticket;
import com.MGR.exception.DuplicateTicketNameException;
import com.MGR.repository.ImageRepository;
import com.MGR.repository.TicketRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

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


    public Long saveTicket(TicketFormDto ticketFormDto, List<MultipartFile> ticketImgFileList) throws Exception {
        boolean isDuplicate = isDuplicateTicket(ticketFormDto);
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


    private boolean isDuplicateTicket(TicketFormDto ticketFormDto) {
        Optional<Ticket> ticketOptional = ticketRepository.findByNameAndPriceAndMemoAndTicketCategoryAndStartDateAndEndDateAndLocationCategory(
                ticketFormDto.getName(), ticketFormDto.getPrice(), ticketFormDto.getMemo(),
                ticketFormDto.getTicketCategory(), ticketFormDto.getStartDate(), ticketFormDto.getEndDate(),
                ticketFormDto.getLocationCategory()
        );
        return ticketOptional.isPresent();
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
        boolean isDuplicate = isDuplicateTicket(ticketFormDto);
        if (isDuplicate) {
            throw new DuplicateTicketNameException("중복된 티켓 정보가 존재합니다.");
        }

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

        return ticketToUpdate.getId();
    }


    //삭제
public void deleteTicket(Long ticketId) {
    // 티켓에 연결된 이미지를 먼저 삭제
    imageService.deleteImagesByTicketId(ticketId);

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
