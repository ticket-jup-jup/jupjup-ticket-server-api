package org.example.jupjupticketserverapi.program.service;

import lombok.RequiredArgsConstructor;
import org.example.jupjupticketserverapi.program.dto.ProgramGetResponse;
import org.example.jupjupticketserverapi.program.repository.ProgramRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProgramService {

    private final ProgramRepository programRepository;

    @Transactional(readOnly = true)
    public List<ProgramGetResponse> getAll() {
        return programRepository.findAll().stream().map(
                program -> new ProgramGetResponse(
                        program.getId(),
                        program.getName(),
                        program.getType(),
                        program.getDescription(),
                        program.getCreatedAt(),
                        program.getUpdatedAt(),
                        program.getDeletedAt()
                )
        ).toList();
    }
}
