package org.example.jupjupticketserverapi.global.config;

import lombok.RequiredArgsConstructor;
import org.example.jupjupticketserverapi.global.security.ApiKeyAuthenticationFilter;
import org.example.jupjupticketserverapi.servercredential.repository.ServerCredentialRepository;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tools.jackson.databind.ObjectMapper;

@Configuration
@RequiredArgsConstructor
public class FilterConfig {

    private final ServerCredentialRepository serverCredentialRepository;
    private final ObjectMapper objectMapper;

    @Bean
    public FilterRegistrationBean<ApiKeyAuthenticationFilter> apiKeyAuthenticationFilter() {

        ApiKeyAuthenticationFilter filter =
                new ApiKeyAuthenticationFilter(
                        serverCredentialRepository,
                        objectMapper
                );

        FilterRegistrationBean<ApiKeyAuthenticationFilter> registration =
                new FilterRegistrationBean<>();

        registration.setFilter(filter);
        registration.addUrlPatterns("/api/*");
        registration.setOrder(1);

        return registration;
    }
}