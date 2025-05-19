package com.example.media_upload.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class ApiKeyInterceptor implements HandlerInterceptor {

    private static final Logger log = LoggerFactory.getLogger(ApiKeyInterceptor.class);

    @Value("${api.key}")
    private String configuredApiKey;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String requestApiKey = request.getHeader("X-API-KEY");

        log.info("Incoming request to: {} from IP: {}", request.getRequestURI(), request.getRemoteAddr());

        if (configuredApiKey != null && configuredApiKey.equals(requestApiKey)) {
            log.info("API key validated successfully.");
            return true;
        }

        log.warn("Unauthorized access attempt to: {} with API key: {}", request.getRequestURI(), requestApiKey);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.getWriter().write("Unauthorized: Invalid API Key");
        return false;
    }
}
