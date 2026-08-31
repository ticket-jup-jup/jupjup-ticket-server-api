package org.example.jupjupticketserverapi.ticket.entity;

public enum TicketStatus {
    AVAILABLE,  // 판매 가능
    RESERVED,   // 임시 예약
    SOLD,       // 예약 확정/판매 완료
    CANCELLED   // 취소
}
