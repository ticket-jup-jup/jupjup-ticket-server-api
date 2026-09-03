package org.example.jupjupticketserverapi.ticket.entity;

public enum TicketStatus {
    /*
    ReservationStatus에 따른 티켓 상태 구분
    1. AVAILABLE
      - 예약 없음
      - 임시예약(PENDING) + 결제 만료시간 지남
      - EXPIRED
      - REFUNDED
    2. RESERVED
      - 임시예약(PENDING) + 결제 만료시간 안지남
    3. SOLD
      - CONFIRMED
     */
    AVAILABLE,
    RESERVED,
    SOLD
}