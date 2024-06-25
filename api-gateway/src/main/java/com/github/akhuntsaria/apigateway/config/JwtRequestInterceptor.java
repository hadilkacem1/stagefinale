package com.github.akhuntsaria.apigateway.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class JwtRequestInterceptor implements RequestInterceptor {


        @Override
        public void apply(RequestTemplate template) {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.getCredentials() instanceof String) {
                String jwtToken = (String) authentication.getCredentials();
                template.header("Authorization", "Bearer " + jwtToken);
            }
        }
    }


