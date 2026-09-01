package org.example.jupjupticketserverapi.servercredential.service;

import lombok.RequiredArgsConstructor;
import org.example.jupjupticketserverapi.servercredential.dto.ServerCredentialCreateRequest;
import org.example.jupjupticketserverapi.servercredential.dto.ServerCredentialCreateResponse;
import org.example.jupjupticketserverapi.servercredential.entity.ServerCredential;
import org.example.jupjupticketserverapi.servercredential.repository.ServerCredentialRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ServerCredentialService {

    private final ServerCredentialRepository serverCredentialRepository;

    @Transactional
    public List<ServerCredentialCreateResponse> create(ServerCredentialCreateRequest request) {

        // 기존 apiKey 조회
        Optional<ServerCredential> existCredential = serverCredentialRepository.findByServiceName(request.getServiceName());
        if (existCredential.isPresent()) {
            return List.of(
                    new ServerCredentialCreateResponse(
                            existCredential.get().getApiKey()
                    )
            );
        }

        // 신규 발급
        String apiKey = generateApiKey();
        ServerCredential serverCredential = new ServerCredential(request.getServiceName(), apiKey);
        serverCredentialRepository.save(serverCredential);

        return List.of(new ServerCredentialCreateResponse(apiKey));
    }

    public String generateApiKey() {
        // 임의의 32바이트 배열 생성
        byte[] bytes = new byte[32];

        // 랜덤 데이터 추가
        new SecureRandom().nextBytes(bytes);

        // base64 인코딩
        return Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(bytes);
    }
}
