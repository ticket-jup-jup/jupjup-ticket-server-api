package org.example.jupjupticketserverapi.auth.service;

import org.example.jupjupticketserverapi.auth.dto.AuthVerifyRequest;
import org.example.jupjupticketserverapi.auth.dto.AuthVerifyResponse;
import org.example.jupjupticketserverapi.auth.exception.InvalidPasswordException;
import org.example.jupjupticketserverapi.global.config.PasswordEncoder;
import org.example.jupjupticketserverapi.user.entity.User;
import org.example.jupjupticketserverapi.user.exception.UserNotFoundException;
import org.example.jupjupticketserverapi.user.respository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    @Test
    void 사용자_연동() {
        // given
        String email = "dummy001@example.com";
        String password = "password123";

        User user = new User(email, "encodedPassword", "홍길동");
        ReflectionTestUtils.setField(user, "id", 1L);

        AuthVerifyRequest request = new AuthVerifyRequest();
        ReflectionTestUtils.setField(request, "email", email);
        ReflectionTestUtils.setField(request, "password", password);

        given(userRepository.findByEmail(email)).willReturn(Optional.of(user));
        given(passwordEncoder.matches(password, "encodedPassword")).willReturn(true);

        // when
        List<AuthVerifyResponse> responses = authService.verify(request);

        // then
        verify(userRepository).findByEmail(email);
        verify(passwordEncoder).matches(password, "encodedPassword");

        assertEquals(1L, responses.get(0).getUserId());
        assertEquals(email, responses.get(0).getEmail());
        assertEquals("홍길동", responses.get(0).getName());
    }

    @Test
    void 존재하지_않는_사용자() {
        // given
        String email = "dummy001@example.com";

        AuthVerifyRequest request = new AuthVerifyRequest();
        ReflectionTestUtils.setField(request, "email", email);
        ReflectionTestUtils.setField(request, "password", "password123");

        given(userRepository.findByEmail(email))
                .willReturn(Optional.empty());

        // when & then
        assertThrows(UserNotFoundException.class, () -> authService.verify(request));

        verify(userRepository).findByEmail(email);
    }

    @Test
    void 비밀번호가_일치하지_않음() {
        // given
        String email = "dummy001@example.com";
        String password = "password123";

        User user = new User(email, "encodedPassword", "홍길동");

        AuthVerifyRequest request = new AuthVerifyRequest();
        ReflectionTestUtils.setField(request, "email", email);
        ReflectionTestUtils.setField(request, "password", password);

        given(userRepository.findByEmail(email))
                .willReturn(Optional.of(user));

        given(passwordEncoder.matches(password, "encodedPassword"))
                .willReturn(false);

        // when & then
        assertThrows(InvalidPasswordException.class, () -> authService.verify(request));
    }
}