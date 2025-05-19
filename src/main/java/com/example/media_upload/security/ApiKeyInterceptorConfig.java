package com.example.media_upload.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.*;

@Configuration
public class ApiKeyInterceptorConfig implements WebMvcConfigurer {

    private static final Logger log = LoggerFactory.getLogger(ApiKeyInterceptorConfig.class);

    @Autowired
    private ApiKeyInterceptor apiKeyInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        log.info("Registering API Key Interceptor for /api/** routes");
        registry.addInterceptor(apiKeyInterceptor).addPathPatterns("/api/**");
    }
}
