package org.example.jupjupticketserverapi.global.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.example.jupjupticketserverapi.global.dto.ApiResponse;
import org.example.jupjupticketserverapi.servercredential.repository.ServerCredentialRepository;
import org.springframework.web.filter.OncePerRequestFilter;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;

@RequiredArgsConstructor
public class ApiKeyAuthenticationFilter extends OncePerRequestFilter {

    private final ServerCredentialRepository serverCredentialRepository;
    private final ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        // API Key 발급 API는 인증 제외
        if (isServerCredentialEndpoint(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        String apiKey = request.getHeader("X-API-KEY");

        // 헤더에 API Key가 없으면 일반 요청으로 처리
        if (apiKey == null || apiKey.isBlank()) {
            filterChain.doFilter(request, response);
            return;
        }

        // API Key가 있다면 서버 간 요청으로 판단
        boolean valid = serverCredentialRepository.existsByApiKey(apiKey);

        if (!valid) {
            sendErrorResponse(
                    response,
                    HttpServletResponse.SC_UNAUTHORIZED,
                    "INVALID_API_KEY",
                    "유효하지 않은 API Key입니다."
            );
            return;
        }

        // 서버 간 요청인 경우
        // 줍줍서버에서 호출 가능한 API인지 확인
        if (!isAllowedEndpoint(request)) {
            sendErrorResponse(
                    response,
                    HttpServletResponse.SC_FORBIDDEN,
                    "FORBIDDEN_API",
                    "접근할 수 없는 API입니다."
            );
            return;
        }

        filterChain.doFilter(request, response);
    }

    private boolean isServerCredentialEndpoint(
            HttpServletRequest request
    ) {
        return request.getMethod().equals("POST")
                && request.getRequestURI()
                .equals("/api/server-credentials");
    }

    // 줍줍서버에서 호출 가능한 API
    private boolean isAllowedEndpoint(
            HttpServletRequest request
    ) {
        String method = request.getMethod();
        String uri = request.getRequestURI();

        // 인증
        if (method.equals("POST")
                && uri.equals("/api/auth/verify")) {
            return true;
        }

        // 사용자
        if (method.equals("POST")
                && uri.equals("/api/users")) {
            return true;
        }

        if (method.equals("GET")
                && uri.equals("/api/users")) {
            return true;
        }

        if (method.equals("GET")
                && uri.matches("/api/users/\\d+")) {
            return true;
        }

        if (method.equals("DELETE")
                && uri.equals("/api/users")) {
            return true;
        }

        if (method.equals("GET")
                && uri.equals("/api/programs")) {
            return true;
        }

        if (method.equals("GET")
                && uri.equals("/api/performances")) {
            return true;
        }

        if (method.equals("GET")
                && uri.equals("/api/internal/tickets")) {
            return true;
        }

        if (method.equals("POST")
                && uri.equals("/api/reservations")) {
            return true;
        }

        if (method.equals("POST")
                && uri.matches("/api/reservations/\\d+/confirm")) {
            return true;
        }

        if (method.equals("POST")
                && uri.matches("/api/reservations/\\d+/cancel")) {
            return true;
        }

        return false;
    }

    private void sendErrorResponse(
            HttpServletResponse response,
            int status,
            String code,
            String message
    ) throws IOException {

        response.setStatus(status);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        ApiResponse<Void> errorResponse =
                ApiResponse.error(code, message);

        response.getWriter().write(
                objectMapper.writeValueAsString(errorResponse)
        );
    }
}