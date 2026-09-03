package org.example.jupjupticketserverapi.reservation.entity;

import org.example.jupjupticketserverapi.reservation.exception.InvalidReservationStatusException;
import org.example.jupjupticketserverapi.ticket.entity.Ticket;
import org.example.jupjupticketserverapi.user.entity.User;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;

class ReservationTest {

    @Test
    void PENDING_예약_확정() {
        // given
        Reservation reservation = new Reservation(
                mock(User.class),
                mock(Ticket.class),
                LocalDateTime.now().plusMinutes(10)
        );

        // when
        reservation.confirm();

        // then
        assertThat(reservation.getStatus()).isEqualTo(ReservationStatus.CONFIRMED);
    }

    @Test
    void 이미_확정된_예약_확정_불가() {

        // given
        Reservation reservation = new Reservation(
                mock(User.class),
                mock(Ticket.class),
                LocalDateTime.now().plusMinutes(10)
        );

        reservation.confirm();

        // when & then
        assertThatThrownBy(reservation::confirm).isInstanceOf(InvalidReservationStatusException.class);
    }

    @Test
    void 만료된_예약_확정_불가() {

        // given
        Reservation reservation = new Reservation(
                mock(User.class),
                mock(Ticket.class),
                LocalDateTime.now().minusMinutes(1)
        );

        // when & then
        assertThatThrownBy(reservation::confirm).isInstanceOf(InvalidReservationStatusException.class);
    }
}