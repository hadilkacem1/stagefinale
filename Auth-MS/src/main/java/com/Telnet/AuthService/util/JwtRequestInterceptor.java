package com.Telnet.AuthService.util;

import com.Telnet.AuthService.model.User;
import com.Telnet.AuthService.repository.TokenRepository;
import com.Telnet.AuthService.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import feign.RequestInterceptor;
import feign.RequestTemplate;

@Component
public class JwtRequestInterceptor implements RequestInterceptor {

    @Autowired
    private TokenRepository tokenRepository;

    @Autowired
    UserRepository userRepository;

    @Override
    public void apply(RequestTemplate template) {
        // Récupérer l'objet d'authentification du contexte de sécurité
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // Vérifier si l'authentification est présente et si elle contient un jeton JWT
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();

            // Extraire le jeton JWT de UserDetails
            String jwtToken = extractJwtToken(userDetails);

            // Ajouter le jeton JWT à l'en-tête Authorization de la requête
            template.header("Authorization", "Bearer " + jwtToken);
        }
    }

    private String extractJwtToken(UserDetails userDetails) {
        // Récupérer l'email de l'utilisateur à partir de UserDetails
        String email = userDetails.getUsername();

        // Récupérer l'utilisateur à partir de votre repository en utilisant l'email
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        // Maintenant, vous pouvez récupérer le jeton associé à l'utilisateur
        if (user.getTokens() != null && !user.getTokens().isEmpty()) {
            // Ici, vous devez choisir le jeton approprié à envoyer dans l'en-tête de la requête
            // Par exemple, si vous voulez envoyer le premier jeton de la liste des jetons de l'utilisateur
            // vous pouvez le récupérer comme ceci :
            return user.getTokens().get(0).getToken(); // Adaptation nécessaire selon votre implémentation User
        } else {
            // Gérer le cas où l'utilisateur n'a pas de jeton associé
            return null;
        }
    }

}
