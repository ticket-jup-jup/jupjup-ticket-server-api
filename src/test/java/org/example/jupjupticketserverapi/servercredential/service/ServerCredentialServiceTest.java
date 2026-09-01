package org.example.jupjupticketserverapi.servercredential.service;

import org.example.jupjupticketserverapi.servercredential.dto.ServerCredentialCreateRequest;
import org.example.jupjupticketserverapi.servercredential.dto.ServerCredentialCreateResponse;
import org.example.jupjupticketserverapi.servercredential.entity.ServerCredential;
import org.example.jupjupticketserverapi.servercredential.repository.ServerCredentialRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ServerCredentialServiceTest {

    @Mock
    private ServerCredentialRepository serverCredentialRepository;

    @InjectMocks
    private ServerCredentialService serverCredentialService;

    @Test
    void API키_신규_생성() {
        // given
        String serviceName = "testCode";
        ServerCredentialCreateRequest request = new ServerCredentialCreateRequest();
        ReflectionTestUtils.setField(request, "serviceName", serviceName);

        when(serverCredentialRepository.findByServiceName(serviceName))
                .thenReturn(Optional.empty());

        // when
        serverCredentialService.create(request);

        // then
        verify(serverCredentialRepository).findByServiceName(serviceName);

        // save 메서드에 전달된 인수를 캡처
        ArgumentCaptor<ServerCredential> serverCredentialCaptor = ArgumentCaptor.forClass(ServerCredential.class);
        verify(serverCredentialRepository).save(serverCredentialCaptor.capture());

        // 캡처된 인수 검증
        ServerCredential savedCredential = serverCredentialCaptor.getValue();
        assertEquals(serviceName, savedCredential.getServiceName());
        assertNotNull(savedCredential.getApiKey());
    }

    @Test
    void 기존_API키_조회() {
        // given
        String serviceName = "testCode";

        ServerCredentialCreateRequest request = new ServerCredentialCreateRequest();
        ReflectionTestUtils.setField(request, "serviceName", serviceName);

        String apiKey = serverCredentialService.generateApiKey();

        ServerCredential serverCredential = new ServerCredential(serviceName, apiKey);

        when(serverCredentialRepository.findByServiceName(serviceName))
                .thenReturn(Optional.of(serverCredential));

        // when
        List<ServerCredentialCreateResponse> response = serverCredentialService.create(request);

        // then
        verify(serverCredentialRepository).findByServiceName("testCode");
        assertEquals(apiKey, response.get(0).getApiKey());
    }
}