package org.example.jupjupticketserverapi.seat.service;

import lombok.RequiredArgsConstructor;
import org.example.jupjupticketserverapi.seat.dto.SeatGetResponse;
import org.example.jupjupticketserverapi.seat.entity.Seat;
import org.example.jupjupticketserverapi.seat.repository.SeatRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SeatService {

    private final SeatRepository seatRepository;

    @Transactional(readOnly = true)
    public List<SeatGetResponse> getAll(Long performanceId) {
        return seatRepository.findAllByPerformanceId(performanceId).stream()
                .map(result -> {
                    Seat seat = (Seat) result[0];
                    Long performanceIdFromTicket = (Long) result[1];

                    return new SeatGetResponse(
                            seat.getId(),
                            performanceIdFromTicket,
                            seat.getSection(),
                            seat.getSeatRow(),
                            seat.getSeatNumber()
                    );
                })
                .toList();
    }
}
