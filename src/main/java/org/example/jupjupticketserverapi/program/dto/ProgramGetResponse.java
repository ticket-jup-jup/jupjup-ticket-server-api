package org.example.jupjupticketserverapi.program.dto;

import lombok.Getter;
import org.example.jupjupticketserverapi.program.entity.ProgramType;

import java.time.LocalDateTime;

@Getter
public class ProgramGetResponse {

    private final Long id;
    private final String name;
    private final ProgramType type;
    private final String description;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
    private final LocalDateTime deletedAt;

    public ProgramGetResponse(Long id, String name, ProgramType type, String description, LocalDateTime createdAt, LocalDateTime updatedAt, LocalDateTime deletedAt) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.description = description;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.deletedAt = deletedAt;
    }
}
