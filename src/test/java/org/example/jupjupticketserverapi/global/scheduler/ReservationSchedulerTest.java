package org.example.jupjupticketserverapi.global.scheduler;

import org.example.jupjupticketserverapi.reservation.service.ReservationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ReservationSchedulerTest {

    @Mock
    private ReservationService reservationService;

    @InjectMocks
    private ReservationScheduler reservationScheduler;

    @Test
    void 예약_만료_스케줄러가_서비스_호출() {

        // when
        reservationScheduler.expireReservations();

        // then
        verify(reservationService).expireReservations();
    }
}