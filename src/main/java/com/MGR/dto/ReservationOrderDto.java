package com.MGR.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter @Setter
public class ReservationOrderDto {
    private Long reservationTicketId;
    private List<ReservationOrderDto> reservationOrderDtoList;

}
