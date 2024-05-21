package com.MGR.repository;

import com.MGR.dto.MainTicketDto;
import com.MGR.dto.TicketSearchDto;
import com.MGR.entity.Ticket;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TicketRepositoryCustom {

    Page<Ticket> getAdminTicketPage(TicketSearchDto ticketSearchDto, Pageable pageable);
    Page<MainTicketDto> getMainTicketPage(TicketSearchDto ticketSearchDto, Pageable pageable);
}
