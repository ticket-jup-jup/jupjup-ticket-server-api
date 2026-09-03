package org.example.jupjupticketserverapi.performance.service;

import lombok.RequiredArgsConstructor;
import org.example.jupjupticketserverapi.performance.dto.PerformanceGetResponse;
import org.example.jupjupticketserverapi.performance.repository.PerformanceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PerformanceService {

    private final PerformanceRepository performanceRepository;

    @Transactional(readOnly = true)
    public List<PerformanceGetResponse> getAll(Long programId) {
        return performanceRepository.findPerformanceList(programId).stream().map(
                performance -> new PerformanceGetResponse(
                        performance.getId(),
                        performance.getProgram().getId(),
                        performance.getStartAt(),
                        performance.getEndAt(),
                        performance.getVenue(),
                        performance.getStatus(),
                        performance.getCreatedAt(),
                        performance.getUpdatedAt()
                )
        ).toList();
    }
}
