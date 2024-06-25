package com.Telnet.AuthService.controller;


import com.Telnet.AuthService.model.AuthenticationResponse;
import com.Telnet.AuthService.model.ERole;
import com.Telnet.AuthService.model.Role;
import com.Telnet.AuthService.model.User;
import com.Telnet.AuthService.repository.RoleRepository;
import com.Telnet.AuthService.repository.UserRepository;
import com.Telnet.AuthService.service.AuthenticationService;
import com.Telnet.AuthService.service.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@CrossOrigin("*")
//@RequestMapping("/auth")
public class AuthenticationController {

    private final AuthenticationService authService;
 @Autowired
 AuthenticationService authenticationService;

 @Autowired
 UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;
    @Autowired
    JwtService jwtService;

    public AuthenticationController(AuthenticationService authService) {
        this.authService = authService;
    }

    @GetMapping("/list")
    public List<User> getUserList() {
        return userRepository.findAll();
    }

    @GetMapping("/api/users/list")
    public String sayHello() {
        return "Bonjour";
    }

    @GetMapping("/api/users/{email}")
    public ResponseEntity<User> getUserByEmail(@PathVariable String email) {
        Optional<User> userOptional = userRepository.findByEmail(email);

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            String token = jwtService.generateToken(user);

            HttpHeaders headers = new HttpHeaders();
            headers.add("Authorization", "Bearer " + token);

            return ResponseEntity.ok().headers(headers).body(user);
        } else {
            return ResponseEntity.notFound().build();
        }
    }


    @GetMapping("/roles")
    public ResponseEntity<List<String>> getRoles() {
        List<String> roles = Arrays.asList(ERole.ADMIN.toString(), ERole.RESPONSABLEQUALITE.toString(), ERole.DIRECTEUR.toString(), ERole.CHEFDEPROJET.toString());
        return ResponseEntity.ok(roles);
    }

    @PostMapping("/register")
    public AuthenticationResponse register(@RequestBody User request) {
        ResponseEntity<AuthenticationResponse> responseEntity = authService.register(request);
        return responseEntity.getBody();
    }
    @GetMapping("/{userId}/role")
    public ResponseEntity<?> getRoleByUserId(@PathVariable Integer userId) {
        Optional<Role> roleOptional = authService.getRoleByUserId(userId);
        if (roleOptional.isPresent()) {
            return ResponseEntity.ok(roleOptional.get());
        } else {
            return ResponseEntity.notFound().build();
    }



    }


    @GetMapping("/useremail/{userId}")
    public ResponseEntity<?> getEmailById(@PathVariable Integer userId) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isPresent()) {
            String email = userOptional.get().getEmail();
            return ResponseEntity.ok().body("{\"email\": \"" + email + "\"}");
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/users/{userId}")
    public ResponseEntity<User> getUserById(@PathVariable Integer userId) {
        try {
            User user = authService.getUserById(userId);
            return ResponseEntity.ok(user);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

  /* @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable(value = "id") Integer userId,
                                           @Valid @RequestBody User userDetails) {
        User updatedUser = authService.updateUser(userId, userDetails);
        return ResponseEntity.ok(updatedUser);
    }*/

   @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> login(
            @RequestBody User request
    ) {
        return ResponseEntity.ok(authService.authenticate(request));
    }
   @PutMapping("/users/{userId}")
    public ResponseEntity<AuthenticationResponse> updateUserById(@PathVariable Integer userId, @RequestBody User request) {
        AuthenticationResponse response = authService.updateUserById(userId, request);
        return ResponseEntity.ok(response);
    }



    @DeleteMapping("/{userId}")
    public ResponseEntity<String> deleteUser(@PathVariable Integer userId) {
        try {
            authenticationService.deleteUser(userId);
            return new ResponseEntity<>("User deleted successfully", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/logout")
    public ResponseEntity<?> logout(@AuthenticationPrincipal User user) {
        if (user == null) {
            // L'utilisateur n'est pas connecté
            return ResponseEntity.badRequest().body("User not logged in");
        }
        authenticationService.logout(user);
        return ResponseEntity.ok("Logout successful");
    }






}
