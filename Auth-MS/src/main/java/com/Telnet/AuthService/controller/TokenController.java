package com.Telnet.AuthService.controller;

import com.Telnet.AuthService.config.CustomLogoutHandler;
import com.Telnet.AuthService.model.Token;
import com.Telnet.AuthService.model.User;
import com.Telnet.AuthService.repository.TokenRepository;
import com.Telnet.AuthService.repository.UserRepository;
import com.Telnet.AuthService.service.JwtService;
import com.Telnet.AuthService.service.UserDetailsServiceImp;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.security.SignatureException;
import java.util.Collection;
import java.util.Optional;

@RestController
@RequestMapping("/tokens")
public class TokenController {

    private final JwtService jwtService;
    private final UserDetailsServiceImp userDetailsService;
    private final TokenRepository tokenRepository;
    private final CustomLogoutHandler customLogoutHandler;

    @Autowired
    UserRepository userRepository;
    private static final Logger logger = LoggerFactory.getLogger(TokenController.class);

    @Autowired
    public TokenController(JwtService jwtService, UserDetailsServiceImp userDetailsService, TokenRepository tokenRepository, CustomLogoutHandler customLogoutHandler) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
        this.tokenRepository = tokenRepository;
        this.customLogoutHandler = customLogoutHandler;
    }

    @GetMapping("/{token}")
    public ResponseEntity<Token> getTokenByValue(@PathVariable String token) {
        logger.info("Received request to fetch token by value: {}", token);
        try {
            Optional<Token> tokenObject = tokenRepository.findByToken(token);
            if (tokenObject.isPresent()) {
                logger.info("Token found: {}", tokenObject.get());
                return ResponseEntity.ok(tokenObject.get());
            } else {
                logger.warn("Token not found: {}", token);
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            logger.error("Error fetching token by value: {}", token, e);
            return ResponseEntity.status(500).build();
        }
    }

    @PutMapping("/update")
    public ResponseEntity<Token> updateTokenStatus(@RequestBody Token token) {
        logger.info("Received request to update token: {}", token);
        try {
            Token updatedToken = tokenRepository.save(token);
            logger.info("Token updated successfully: {}", updatedToken);
            return ResponseEntity.ok(updatedToken);
        } catch (Exception e) {
            logger.error("Error updating token: {}", token, e);
            return ResponseEntity.status(500).build();
        }
    }

    @GetMapping("/user/{token}")
    public ResponseEntity<User> getUserByToken(@PathVariable("token") String tokenValue) {
        logger.info("Received request to fetch user by token: {}", tokenValue);
        try {
            Optional<Token> tokenObject = tokenRepository.findByToken(tokenValue);
            return tokenObject.map(tokenEntity -> {
                logger.info("User found: {}", tokenEntity.getUser());
                return ResponseEntity.ok(tokenEntity.getUser());
            }).orElseGet(() -> {
                logger.warn("User not found for token: {}", tokenValue);
                return ResponseEntity.notFound().build();
            });
        } catch (Exception e) {
            logger.error("Error fetching user by token: {}", tokenValue, e);
            return ResponseEntity.status(500).build();
        }
    }

    @GetMapping("/validate/{token}")
    public ResponseEntity<Boolean> checkTokenValidity(@PathVariable String token, @RequestParam("user") String userEmail) {
        logger.info("Received request to validate token: {}", token);
        try {
            // Récupérer les informations de l'utilisateur à partir de l'e-mail
            UserDetails userDetails = userDetailsService.loadUserByUsername(userEmail);
            // Vérifier la validité du jeton en utilisant les informations de l'utilisateur
            boolean isValid = jwtService.isValid(token, userDetails);
            // Retourner le résultat de la validation
            return ResponseEntity.ok(isValid);
        } catch (UsernameNotFoundException e) {
            logger.error("User not found with email: {}", userEmail, e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(false);
        } catch (Exception e) {
            logger.error("Error validating token: {}", token, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(false);
        }
    }


    @GetMapping("/extractUsername/{token}")
    public String extractUsername(@PathVariable String token) {
        logger.info("Received request to extract username from token: {}", token);
        try {
            return jwtService.extractUsername(token);
        } catch (Exception e) {
            logger.error("Error extracting username from token: {}", token, e);
            return null;
        }
    }

    @GetMapping("/loadUserByUsername/{email}")
    public UserDetails loadUserByUsername(@PathVariable String email) {
        logger.info("Received request to load user by username: {}", email);
        try {
            UserDetails userDetails = userDetailsService.loadUserByUsername(email);
            logger.info("User found: {}", userDetails.getUsername());
            return userDetails;
        } catch (Exception e) {
            logger.error("Error loading user by username: {}", email, e);
            return null;
        }
    }

    @GetMapping("/getAuthorities/{email}")
    public ResponseEntity<Collection<? extends GrantedAuthority>> getAuthorities(@PathVariable String email) {
        logger.info("Received request to get authorities for user: {}", email);
        try {
            User user = userDetailsService.userRepository.findByEmail(email)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
            Collection<? extends GrantedAuthority> authorities = userDetailsService.getAuthorities(user);
            return ResponseEntity.ok(authorities);
        } catch (UsernameNotFoundException e) {
            logger.error("User not found with email: {}", email, e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            logger.error("Error getting authorities for user: {}", email, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    @GetMapping("/userDetails")
    public ResponseEntity<UserDetails> getUserDetailsFromToken(@RequestParam("token") String token) {
        logger.info("Received request to fetch user details for token: {}", token);
        try {
            String username = jwtService.extractUsername(token);
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            return ResponseEntity.ok(userDetails);
        } catch (Exception e) {
            logger.error("Error fetching user details for token: {}", token, e);
            return ResponseEntity.status(500).build();
        }
    }

    @PostMapping("/logout")
    public void logout(HttpServletRequest request, HttpServletResponse response) {
        logger.info("Received request to logout");
        customLogoutHandler.logout(request, response, null);
    }
}
