package com.Telnet.volet.communication;


import com.Telnet.volet.config.FeignConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;
import com.Telnet.volet.model.Token;
import java.util.Collection;
@Component
@FeignClient(name = "auth-service", configuration = FeignConfig.class)
public interface TokenClient {
    Logger logger = LoggerFactory.getLogger(TokenClient.class);

    @GetMapping("/tokens/{token}")
    ResponseEntity<Token> getTokenByValue(@PathVariable("token") String token);

    @PutMapping("/tokens/update")
    ResponseEntity<Token> updateTokenStatus(@RequestBody Token token);

    @GetMapping("/tokens/user/{token}")
    ResponseEntity<User> getUserByToken(@PathVariable("token") String token);

    @GetMapping("/tokens/validate/{token}")
    boolean checkTokenValidity(@PathVariable("token") String token, @RequestParam("user") String userDetails);

    @GetMapping("/tokens/extractUsername/{token}")
    String extractUsername(@PathVariable("token") String token);

    @GetMapping("/tokens/loadUserByUsername/{email}")
    UserDetails loadUserByUsername(@PathVariable("email") String email);

    @GetMapping("/tokens/userDetails")
    ResponseEntity<UserDetails> getUserDetailsFromToken(@RequestParam("token") String token);

    @PostMapping("/logout")
    void logout();

    @GetMapping("/tokens/getAuthorities/{email}")
    ResponseEntity<Collection<? extends GrantedAuthority>> getAuthorities(@PathVariable("email") String email);
}