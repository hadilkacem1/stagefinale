package com.Telnet.AuthService.service;

import com.Telnet.AuthService.model.*;
import com.Telnet.AuthService.repository.TokenRepository;
import com.Telnet.AuthService.repository.RoleRepository;
import com.Telnet.AuthService.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.*;

@Service
public class AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final TokenRepository tokenRepository;
    private final RoleRepository roleRepository;
    private final AuthenticationManager authenticationManager;


    @Autowired
    public AuthenticationService(UserRepository userRepository,
                                 PasswordEncoder passwordEncoder,
                                 JwtService jwtService,
                                 TokenRepository tokenRepository,
                                 RoleRepository roleRepository, AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.tokenRepository = tokenRepository;
        this.roleRepository = roleRepository;
        this.authenticationManager = authenticationManager;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(@RequestBody User request) {
        // Vérifie si l'utilisateur existe déjà avec cet e-mail
        userRepository.findByEmail(request.getEmail()).ifPresent(user -> {
            throw new RuntimeException("Error: Email is already in use!");
        });

        // Crée un nouvel utilisateur
        User user = new User();
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail()); // Utilisez l'e-mail pour l'inscription
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        // Vérifie si le rôle est nul, attribue un rôle par défaut si nécessaire
        ERole defaultRole = ERole.ADMIN; // Rôle par défaut
        ERole userRole = request.getRole() != null ? request.getRole() : defaultRole;

        // Convertit l'ERole en Role
        Role role = roleRepository.findByName(userRole)
                .orElseThrow(() -> new RuntimeException("Error: Role not found!"));

        // Initialise la liste des rôles de l'utilisateur s'il est nul
        if (user.getRoles() == null) {
            user.setRoles(new HashSet<>());
        }

        // Ajoute le rôle à l'utilisateur
        user.getRoles().add(role);

        // Récupère et associe les activités de la requête
        Set<Activity> activitySet = request.getActivities() != null ? new HashSet<>(request.getActivities()) : new HashSet<>();
        user.setActivities(activitySet);

        // Enregistre l'utilisateur dans la base de données après avoir ajouté le rôle et les activités
        user = userRepository.save(user);

        // Génère le jeton JWT
        String jwt = jwtService.generateToken(user);

        // Crée la réponse avec le jeton, le message de réussite et d'autres informations
        String message = "User registration was successful for username: " + user.getUsername();
        AuthenticationResponse response = new AuthenticationResponse(jwt, message, userRole.toString(), user.getUsername(), user.getFirstName(), user.getLastName(), user.getEmail(), new ArrayList<>(user.getActivities()), user.getId());

        return ResponseEntity.ok(response);
    }

    public Optional<Role> getRoleByUserId(Integer userId) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            // Assuming there's a method to get roles associated with user
            Set<Role> roles = user.getRoles();
            // You may need to handle multiple roles here, like selecting one or returning a collection
            if (!roles.isEmpty()) {
                // For simplicity, return the first role found
                return Optional.of(roles.iterator().next());
            } else {
                // Handle case where user has no roles
                return Optional.empty();
            }
        } else {
            // Handle case where user with given ID is not found
            return Optional.empty();
        }
    }



    public User getUserById(Integer userId) throws ResourceNotFoundException {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
    }

    public void logout(User user) {
        revokeAllTokenByUser(user);
    }

    public void deleteUser(Integer userId) {
        userRepository.deleteById(userId);
    }

    private void revokeAllTokenByUser(User user) {
        List<Token> validTokens = tokenRepository.findAllTokensByUser(user.getId());
        if (validTokens.isEmpty()) {
            return;
        }

        validTokens.forEach(t -> {
            t.setLoggedOut(true);
        });

        tokenRepository.saveAll(validTokens);
    }


    private void saveUserToken(String jwt, User user) {
        Token token = new Token();
        token.setToken(jwt);
        token.setLoggedOut(false);
        token.setUser(user);
        tokenRepository.save(token);
    }

    public AuthenticationResponse authenticate(User request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(), // Utilisez l'e-mail pour l'authentification
                        request.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Récupérer l'utilisateur par e-mail
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Vérifiez si les rôles de l'utilisateur sont définis
        if (user.getRoles() == null || user.getRoles().isEmpty()) {
            throw new RuntimeException("User role not set");
        }

        String jwt = jwtService.generateToken(user);

        // Révoquer tous les jetons de l'utilisateur et enregistrer le nouveau jeton
        revokeAllTokenByUser(user);
        saveUserToken(jwt, user);

        // Récupérer les informations de l'utilisateur
        String username = user.getUsername();
        String firstName = user.getFirstName();
        String lastName = user.getLastName();
        String email = user.getEmail();

        // Récupérer le rôle de l'utilisateur (supposons que l'utilisateur a un seul rôle pour simplifier)
        String userRole = user.getRoles().iterator().next().getName().toString();

        // Convertir la liste des activités en une chaîne de caractères séparées par des virgules
        StringBuilder activitiesString = new StringBuilder();
        Collection<Activity> activities = user.getActivities();
        if (activities != null && !activities.isEmpty()) {
            for (Activity activity : activities) {
                activitiesString.append(activity.getName()).append(", ");
            }
            activitiesString.delete(activitiesString.length() - 2, activitiesString.length()); // Supprimer la dernière virgule et l'espace
        }

        // Retourner l'objet AuthenticationResponse avec l'ID de l'utilisateur
        return new AuthenticationResponse(jwt, "User login was successful", userRole, username, firstName, lastName, email, new ArrayList<>(activities), user.getId());
    }


    public AuthenticationResponse updateUserById(Integer userId, User request) {
        // Recherche de l'utilisateur par son ID
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Mettre à jour les informations de l'utilisateur
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());

        // Mettre à jour le rôle de l'utilisateur
        ERole newRole = request.getRole();
        if (newRole != null) {
            Role role = roleRepository.findByName(newRole)
                    .orElseThrow(() -> new RuntimeException("Role not found"));
            user.setRoles(new HashSet<>(Arrays.asList(role)));
        }

        // Mettre à jour la liste des activités de l'utilisateur
        Collection<Activity> newActivities = request.getActivities();
        if (newActivities != null) {
            user.setActivities(newActivities);
        }

        // Enregistrer les modifications dans la base de données
        User updatedUser = userRepository.save(user);

        // Générer le jeton JWT
        String jwt = jwtService.generateToken(updatedUser);

        // Convertir la liste des activités en une chaîne de caractères
        StringBuilder activitiesString = new StringBuilder();
        if (updatedUser.getActivities() != null && !updatedUser.getActivities().isEmpty()) {
            for (Activity activity : updatedUser.getActivities()) {
                activitiesString.append(activity.getName()).append(", ");
            }
            activitiesString.delete(activitiesString.length() - 2, activitiesString.length()); // Supprimer la dernière virgule et l'espace
        }

        // Récupérer le nom du rôle
        String userRole = updatedUser.getRoles().isEmpty() ? "" : updatedUser.getRoles().iterator().next().getName().toString();

        // Créer la réponse avec le jeton, les informations de l'utilisateur et la liste des activités
        String message = "User details updated successfully for username: " + updatedUser.getUsername();
        return new AuthenticationResponse(jwt, message, userRole, updatedUser.getUsername(), updatedUser.getFirstName(), updatedUser.getLastName(), updatedUser.getEmail(), new ArrayList<>(updatedUser.getActivities()), user.getId());
    }



}