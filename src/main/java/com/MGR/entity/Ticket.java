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

    @PrePersist
    public void checkDuplicateName() {
        // 티켓 이름이 이미 존재하는지 확인하고 예외를 발생시킴
        if (nameAlreadyExists(name)) {
            throw new DuplicateTicketNameException("티켓명 '" + name + "'은(는) 이미 존재합니다.");
        }
    }

    private boolean nameAlreadyExists(String name) {
        // 여기에서는 티켓 이름이 이미 데이터베이스에 존재하는지 확인하는 로직을 구현
        // 데이터베이스에서 해당 이름을 가진 티켓을 조회하여 존재 여부를 판단할 수 있음
        return false; // 구현에 따라서 true 또는 false 반환
    }

}

