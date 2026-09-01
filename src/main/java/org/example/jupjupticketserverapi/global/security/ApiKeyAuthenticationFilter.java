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

        // API Key 발급 API 인증 제외
        if (request.getRequestURI().equals("/api/server-credential")
                && request.getMethod().equals("POST")) {
            filterChain.doFilter(request, response);
            return;
        }

        String apiKey = request.getHeader("X-API-KEY");

        if (apiKey == null || apiKey.isBlank()) {
            sendErrorResponse(
                    response,
                    "API_KEY_MISSING",
                    "API Key가 없습니다."
            );
            return;
        }

        boolean valid = serverCredentialRepository.existsByApiKey(apiKey);

        if (!valid) {
            sendErrorResponse(
                    response,
                    "INVALID_API_KEY",
                    "유효하지 않은 API Key입니다."
            );
            return;
        }

        filterChain.doFilter(request, response);
    }

    private void sendErrorResponse(
            HttpServletResponse response,
            String code,
            String message
    ) throws IOException {

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        ApiResponse<Void> errorResponse =
                ApiResponse.error(code, message);

        response.getWriter().write(
                objectMapper.writeValueAsString(errorResponse)
        );
    }
}