package org.example.jupjupticketserverapi.servercredential.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class ServerCredentialCreateRequest {

    @NotBlank(message = "서비스명을 입력해주세요.")
    @Size(max = 30, message = "서비스명은 30자 이내로 입력해주세요.")
    private String serviceName;
}
