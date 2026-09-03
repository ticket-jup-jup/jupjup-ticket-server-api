package org.example.jupjupticketserverapi.program.service;

import org.example.jupjupticketserverapi.program.dto.ProgramGetResponse;
import org.example.jupjupticketserverapi.program.entity.Program;
import org.example.jupjupticketserverapi.program.entity.ProgramType;
import org.example.jupjupticketserverapi.program.repository.ProgramRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProgramServiceTest {

    @Mock
    private ProgramRepository programRepository;

    @InjectMocks
    private ProgramService programService;

    @Test
    void 전체_프로그램_조회() {
        // given
        Program program = new Program(
                "테스트 프로그램",
                ProgramType.CONCERT,
                "테스트 설명"
        );

        Program program2 = new Program(
                "테스트 프로그램 2",
                ProgramType.TRAIN,
                "테스트 설명 2"
        );

        when(programRepository.findAll()).thenReturn(List.of(program, program2));

        // when
        List<ProgramGetResponse> programs = programService.getAll();

        // then
        verify(programRepository).findAll();

        assertThat(programs).hasSize(2);
        assertThat(programs.get(0).getName()).isEqualTo("테스트 프로그램");
        assertThat(programs.get(0).getType()).isEqualTo(ProgramType.CONCERT);
        assertThat(programs.get(0).getDescription()).isEqualTo("테스트 설명");
        assertThat(programs.get(1).getName()).isEqualTo("테스트 프로그램 2");
        assertThat(programs.get(1).getType()).isEqualTo(ProgramType.TRAIN);
        assertThat(programs.get(1).getDescription()).isEqualTo("테스트 설명 2");
    }

}