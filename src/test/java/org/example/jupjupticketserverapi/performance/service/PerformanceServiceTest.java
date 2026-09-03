package org.example.jupjupticketserverapi.performance.service;

import org.example.jupjupticketserverapi.performance.dto.PerformanceGetResponse;
import org.example.jupjupticketserverapi.performance.entity.Performance;
import org.example.jupjupticketserverapi.performance.entity.PerformanceStatus;
import org.example.jupjupticketserverapi.performance.repository.PerformanceRepository;
import org.example.jupjupticketserverapi.program.entity.Program;
import org.example.jupjupticketserverapi.program.entity.ProgramType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PerformanceServiceTest {

    @Mock
    private Program program;

    @Mock
    private PerformanceRepository performanceRepository;

    @InjectMocks
    private PerformanceService performanceService;

    @Test
    void 전체_프로그램_조회() {
        Program program = new Program(
                "테스트 프로그램",
                ProgramType.CONCERT,
                "테스트 설명"
        );

        Performance performance = new Performance(
                program,
                LocalDateTime.of(2026, 9, 10, 19, 0),
                LocalDateTime.of(2026, 9, 10, 21, 0),
                "테스트 장소",
                PerformanceStatus.UPCOMING
        );

        Performance performance2 = new Performance(
                program,
                LocalDateTime.of(2026, 9, 11, 19, 0),
                LocalDateTime.of(2026, 9, 11, 21, 0),
                "테스트 장소 2",
                PerformanceStatus.UPCOMING
        );

        when(performanceRepository.findPerformanceList(null)).thenReturn(List.of(performance, performance2));

        // when
        List<PerformanceGetResponse> performances = performanceService.getAll(null);

        // then
        verify(performanceRepository).findPerformanceList(null);

        assertThat(performances).hasSize(2);
        assertThat(performances.get(0).getProgramId()).isNull();
        assertThat(performances.get(0).getStartAt()).isEqualTo(LocalDateTime.of(2026, 9, 10, 19, 0));
        assertThat(performances.get(0).getEndAt()).isEqualTo(LocalDateTime.of(2026, 9, 10, 21, 0));
        assertThat(performances.get(0).getVenue()).isEqualTo("테스트 장소");
        assertThat(performances.get(0).getStatus()).isEqualTo(PerformanceStatus.UPCOMING);
    }

    @Test
    void 프로그램별_회차_조회() {
        // given
        Long programId = 1L;

        Program program = new Program(
                "테스트 프로그램",
                null,
                "테스트 설명"
        );

        Performance performance = new Performance(
                program,
                LocalDateTime.of(2026, 9, 10, 19, 0),
                LocalDateTime.of(2026, 9, 10, 21, 0),
                "테스트 장소",
                PerformanceStatus.UPCOMING
        );

        when(performanceRepository.findPerformanceList(programId)).thenReturn(List.of(performance));

        // when
        List<PerformanceGetResponse> performances = performanceService.getAll(programId);

        // then
        verify(performanceRepository).findPerformanceList(programId);
        assertThat(performances).hasSize(1);
        assertThat(performances.get(0).getStartAt()).isEqualTo(LocalDateTime.of(2026, 9, 10, 19, 0));
        assertThat(performances.get(0).getVenue()).isEqualTo("테스트 장소");
        assertThat(performances.get(0).getStatus()).isEqualTo(PerformanceStatus.UPCOMING);
    }

}