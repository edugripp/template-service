package com.example.template.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.www.BasicAuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class TemplateBasicAuthenticationEntryPoint extends BasicAuthenticationEntryPoint {
    @Override
    public void commence(
            HttpServletRequest request, HttpServletResponse response, AuthenticationException authEx)
            throws IOException {
        response.addHeader("Authorization", "Basic realm=" + getRealmName());
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        log.error("HTTP Status 401 UNAUTHORIZED - " + authEx.getMessage());
    }

    @Override
    public void afterPropertiesSet() {
        setRealmName("template");
        super.afterPropertiesSet();
    }
}
