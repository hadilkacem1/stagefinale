package com.github.akhuntsaria.apigateway.filter;


import com.github.akhuntsaria.apigateway.communication.UserClient;

import com.github.akhuntsaria.apigateway.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Collection;
import java.util.stream.Collectors;

@Service
public class UserDetailsServiceImp implements UserDetailsService {

    @Autowired
    private UserClient userClient;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // Appel à l'API de auth-service pour récupérer l'utilisateur par e-mail
        User user = userClient.getUserByEmail(email);
        if (user == null) {
            throw new UsernameNotFoundException("User not found with email: " + email);
        }

        // Crée un objet UserDetails à partir de l'utilisateur récupéré
        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getEmail()) // Utilisez l'e-mail comme nom d'utilisateur
                .password(user.getPassword())
                .authorities(getAuthorities(user))
                .build();
    }

    @Transactional
    public Collection<? extends GrantedAuthority> getAuthorities(User user) {
        // Récupérer les rôles de l'utilisateur depuis l'entité User
        return user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getName().toString()))
                .collect(Collectors.toList());
    }
}
