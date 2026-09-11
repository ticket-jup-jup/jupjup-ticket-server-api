package org.example.jupjupticketserverapi.seat.service;

import org.example.jupjupticketserverapi.seat.dto.SeatGetResponse;
import org.example.jupjupticketserverapi.seat.entity.Seat;
import org.example.jupjupticketserverapi.seat.repository.SeatRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SeatServiceTest {

    @Mock
    private SeatRepository seatRepository;

    @InjectMocks
    private SeatService seatService;

    @Test
    void 전체_좌석_조회() {
        // given
        Seat seat = new Seat(
                "A",
                "1",
                1
        );

        Seat seat2 = new Seat(
                "A",
                "1",
                2
        );

        Object[] result1 = {seat, 1L};
        Object[] result2 = {seat2, 1L};

        when(seatRepository.findAllByPerformanceId(null)).thenReturn(List.of(result1, result2));

        // when
        List<SeatGetResponse> seats = seatService.getAll(null);

        // then
        verify(seatRepository).findAllByPerformanceId(null);

        assertThat(seats).hasSize(2);
        assertThat(seats.get(0).getId()).isEqualTo(seat.getId());
        assertThat(seats.get(0).getPerformanceId()).isEqualTo(1L);
        assertThat(seats.get(0).getSection()).isEqualTo("A");
        assertThat(seats.get(0).getSeatRow()).isEqualTo("1");
        assertThat(seats.get(0).getSeatNumber()).isEqualTo(1);
        assertThat(seats.get(1).getId()).isEqualTo(seat2.getId());
        assertThat(seats.get(1).getPerformanceId()).isEqualTo(1L);
        assertThat(seats.get(1).getSection()).isEqualTo("A");
        assertThat(seats.get(1).getSeatRow()).isEqualTo("1");
        assertThat(seats.get(1).getSeatNumber()).isEqualTo(2);
    }

    @Test
    void 프로그램별_좌석_조회() {
        // given
        Long performanceId = 1L;

        Seat seat = new Seat(
                "B",
                "2",
                10
        );

        Object[] result = new Object[]{seat, performanceId};

        when(seatRepository.findAllByPerformanceId(performanceId)).thenReturn(Collections.singletonList(result));

        // when
        List<SeatGetResponse> seats = seatService.getAll(performanceId);

        // then
        verify(seatRepository).findAllByPerformanceId(performanceId);

        assertThat(seats).hasSize(1);
        assertThat(seats.get(0).getId()).isEqualTo(seat.getId());
        assertThat(seats.get(0).getPerformanceId()).isEqualTo(performanceId);
        assertThat(seats.get(0).getSection()).isEqualTo("B");
        assertThat(seats.get(0).getSeatRow()).isEqualTo("2");
        assertThat(seats.get(0).getSeatNumber()).isEqualTo(10);
    }
}