package com.MGR.entity;

import com.MGR.constant.TicketCategory;
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
    private LocalDateTime startDate;

    @Column(columnDefinition = "DATE")
    private LocalDateTime endDate;

    @Column(nullable = false)
    private String location;

    @Enumerated(EnumType.STRING)
    private TicketCategory ticketCategory;

    @Column(nullable = false, length = 50, unique = true)
    private String name;

    @Column(name="price", nullable = false)
    private int price;

    @Column(columnDefinition = "TEXT")
    private String memo;

    public void updateTicket(TicketFormDto ticketFormDto) {
        this.name = ticketFormDto.getName();
        this.price = ticketFormDto.getPrice();
        this.memo = ticketFormDto.getMemo();
        this.ticketCategory = ticketFormDto.getTicketCategory();
        this.startDate = ticketFormDto.getStartDate();
        this.endDate = ticketFormDto.getEndDate();
        this.location = ticketFormDto.getLocation();

    }



}

