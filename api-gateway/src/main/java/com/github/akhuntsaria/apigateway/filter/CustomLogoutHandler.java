 package com.github.akhuntsaria.apigateway.filter;


import com.github.akhuntsaria.apigateway.communication.TokenClient;

import com.github.akhuntsaria.apigateway.model.Token;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Configuration
public class CustomLogoutHandler implements LogoutHandler {

    private final TokenClient tokenClient;

    @Autowired
    public CustomLogoutHandler(TokenClient tokenClient) {
        this.tokenClient = tokenClient;
    }

    @Override
    public void logout(HttpServletRequest request,
                       HttpServletResponse response,
                       Authentication authentication) {
        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return;
        }

        String token = authHeader.substring(7);
        ResponseEntity<Token> responseEntity = tokenClient.getTokenByValue(token);
        Token storedToken = responseEntity.getBody();

        if (storedToken != null) {
            storedToken.setLoggedOut(true);
            // Assume there is a way to update the token status in the auth-service, such as a PUT endpoint
            tokenClient.updateTokenStatus(storedToken); // You need to implement this method in TokenClient
        }
    }
}
