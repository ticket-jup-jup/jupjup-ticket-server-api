package org.example.jupjupticketserverapi.global.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.jupjupticketserverapi.servercredential.repository.ServerCredentialRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.PrintWriter;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ApiKeyAuthenticationFilterTest {

    @Mock
    private ServerCredentialRepository serverCredentialRepository;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @Mock
    private PrintWriter printWriter;

    @InjectMocks
    private ApiKeyAuthenticationFilter apiKeyAuthenticationFilter;

    @Test
    void API키_발급_API는_API키_없이_통과한다() throws ServletException, IOException {

        // given
        when(request.getMethod()).thenReturn("POST");
        when(request.getRequestURI()).thenReturn("/api/server-credentials");

        // when
        apiKeyAuthenticationFilter.doFilter(request, response, filterChain);

        // then
        verify(filterChain).doFilter(request, response);
        verifyNoInteractions(serverCredentialRepository);
    }

    @Test
    void API키가_없으면_일반_요청으로_통과한다() throws ServletException, IOException {

        // given
        when(request.getMethod()).thenReturn("GET");

        // when
        apiKeyAuthenticationFilter.doFilter(request, response, filterChain);

        // then
        verify(filterChain).doFilter(request, response);
        verifyNoInteractions(serverCredentialRepository);
    }

    @Test
    void 잘못된_API키는_401을_반환한다() throws ServletException, IOException {

        // given
        String apiKey = "invalid-api-key";

        when(request.getMethod()).thenReturn("GET");
        when(request.getHeader("X-API-KEY")).thenReturn(apiKey);
        when(serverCredentialRepository.existsByApiKey(apiKey)).thenReturn(false);
        when(response.getWriter()).thenReturn(printWriter);
        when(objectMapper.writeValueAsString(any())).thenReturn("{}");

        // when
        apiKeyAuthenticationFilter.doFilter(request, response, filterChain);

        // then
        verify(serverCredentialRepository).existsByApiKey(apiKey);
        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        verify(printWriter).write("{}");
        verifyNoInteractions(filterChain);
    }

    @Test
    void 정상_API키로_허용된_API를_호출하면_통과한다() throws ServletException, IOException {

        // given
        String apiKey = "valid-api-key";

        when(request.getMethod()).thenReturn("GET");
        when(request.getRequestURI()).thenReturn("/api/programs");
        when(request.getHeader("X-API-KEY")).thenReturn(apiKey);
        when(serverCredentialRepository.existsByApiKey(apiKey)).thenReturn(true);

        // when
        apiKeyAuthenticationFilter.doFilter(request, response, filterChain);

        // then
        verify(serverCredentialRepository).existsByApiKey(apiKey);
        verify(filterChain).doFilter(request, response);
        verify(response, never()).setStatus(HttpServletResponse.SC_FORBIDDEN);
    }

    @Test
    void 허용되지_않은_API는_403을_반환한다() throws ServletException, IOException {

        // given
        String apiKey = "valid-api-key";

        when(request.getMethod()).thenReturn("GET");
        when(request.getRequestURI()).thenReturn("/api/reservations");
        when(request.getHeader("X-API-KEY")).thenReturn(apiKey);
        when(serverCredentialRepository.existsByApiKey(apiKey)).thenReturn(true);
        when(response.getWriter()).thenReturn(printWriter);
        when(objectMapper.writeValueAsString(any())).thenReturn("{}");

        // when
        apiKeyAuthenticationFilter.doFilter(request, response, filterChain);

        // then
        verify(serverCredentialRepository).existsByApiKey(apiKey);
        verify(response).setStatus(HttpServletResponse.SC_FORBIDDEN);
        verify(printWriter).write("{}");
        verifyNoInteractions(filterChain);
    }

    @Test
    void 정상_API키로_사용자_상세조회는_통과한다() throws ServletException, IOException {

        // given
        String apiKey = "valid-api-key";

        when(request.getMethod()).thenReturn("GET");
        when(request.getRequestURI()).thenReturn("/api/users/1");
        when(request.getHeader("X-API-KEY")).thenReturn(apiKey);
        when(serverCredentialRepository.existsByApiKey(apiKey)).thenReturn(true);

        // when
        apiKeyAuthenticationFilter.doFilter(request, response, filterChain);

        // then
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void 정상_API키로_예약_확정은_통과한다() throws ServletException, IOException {

        // given
        String apiKey = "valid-api-key";

        when(request.getMethod()).thenReturn("POST");
        when(request.getRequestURI()).thenReturn("/api/reservations/1/confirm");
        when(request.getHeader("X-API-KEY")).thenReturn(apiKey);
        when(serverCredentialRepository.existsByApiKey(apiKey)).thenReturn(true);

        // when
        apiKeyAuthenticationFilter.doFilter(request, response, filterChain);

        // then
        verify(filterChain).doFilter(request, response);
    }
}