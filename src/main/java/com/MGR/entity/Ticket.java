package com.MGR.entity;

import com.MGR.constant.LocationCategory;
import com.MGR.dto.TicketFormDto;
import com.MGR.exception.DuplicateTicketNameException;
import com.fasterxml.jackson.databind.ser.Serializers;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Setter
@Getter
public class Ticket extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "DATE")
    private LocalDate startDate;

    @Column(columnDefinition = "DATE")
    private LocalDate endDate;


    @Enumerated(EnumType.STRING)
    private LocationCategory locationCategory;

    @Column(length = 50)
    private String name;

    @Column(name="adultPrice", nullable = false)
    private Integer adultPrice;

    @Column(name="childPrice", nullable = false)
    private Integer childPrice;

    @Column(columnDefinition = "TEXT")
    private String memo;


    public void updateTicket(TicketFormDto ticketFormDto) {
        this.name = ticketFormDto.getName();
        this.memo = ticketFormDto.getMemo();
        this.startDate = ticketFormDto.getStartDate();
        this.endDate = ticketFormDto.getEndDate();
        this.adultPrice = ticketFormDto.getAdultPrice();
        this.childPrice = ticketFormDto.getChildPrice();
        this.locationCategory = ticketFormDto.getLocationCategory();
    }
}
