package org.example.jupjupticketserverapi.auth.service;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.jupjupticketserverapi.auth.dto.AuthVerifyRequest;
import org.example.jupjupticketserverapi.auth.dto.AuthVerifyResponse;
import org.example.jupjupticketserverapi.auth.exception.InvalidPasswordException;
import org.example.jupjupticketserverapi.global.config.PasswordEncoder;
import org.example.jupjupticketserverapi.user.entity.User;
import org.example.jupjupticketserverapi.user.exception.UserNotFoundException;
import org.example.jupjupticketserverapi.user.respository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public List<AuthVerifyResponse> verify(@Valid AuthVerifyRequest request) {
        // 사용자 조회
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() ->
                        new UserNotFoundException("존재하지 않는 유저입니다.")
                );

        // 비밀번호 확인
        if (!passwordEncoder.matches(
                request.getPassword(),
                user.getPassword()
        )) {
            throw new InvalidPasswordException("비밀번호가 일치하지 않습니다.");
        }

        return List.of(
                new AuthVerifyResponse(
                        user.getId(),
                        user.getEmail(),
                        user.getName()
                )
        );
    }
}
