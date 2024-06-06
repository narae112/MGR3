package com.MGR.service;

import com.MGR.entity.Inventory;
import com.MGR.entity.Ticket;
import com.MGR.repository.InventoryRepository;
import com.MGR.repository.TicketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InventoryService {
    private final InventoryRepository inventoryRepository;
    private final TicketRepository ticketRepository;

    @Scheduled(cron = "0 */3 * * * *") // 매 3분마다 실행
//    @Scheduled(cron = "0 0 0 1 * ?") // 매월 1일 자정에 실행
    public void resetInventory() {
        // 현재 날짜를 가져옴
        LocalDate currentDate = LocalDate.now();
        // 한 달 후 날짜 계산
        LocalDate oneMonthLater = currentDate.plusMonths(1);

        // 데이터베이스에서 모든 티켓을 가져옴
        List<Ticket> allTickets = ticketRepository.findAll();
        for (Ticket ticket : allTickets) {
            // 현재 날짜부터 한 달 동안의 재고를 설정
            for (LocalDate date = currentDate; date.isBefore(oneMonthLater); date = date.plusDays(1)) {
                // 티켓에 대한 재고를 가져오거나 생성
                Inventory inventory = inventoryRepository.findByTicketIdAndDate(ticket.getId(), date);
                if (inventory == null) { // 인벤토리가 존재하지 않는 경우
                    inventory = Inventory.createInventory(ticket, 100, date); // 새로운 인벤토리 생성
                }
                // 데이터베이스에 저장
                inventoryRepository.save(inventory);
            }
        }
    }
}