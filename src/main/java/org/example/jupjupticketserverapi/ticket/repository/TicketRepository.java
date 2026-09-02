package org.example.jupjupticketserverapi.ticket.repository;

import org.example.jupjupticketserverapi.ticket.entity.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TicketRepository extends JpaRepository<Ticket, Long> {
}
