package org.example.jupjupticketserverapi.payment.repository;

import org.example.jupjupticketserverapi.payment.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
}
