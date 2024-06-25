package com.Telnet.AuthService.service;

import com.Telnet.AuthService.model.User;
import com.Telnet.AuthService.repository.RoleRepository;
import com.Telnet.AuthService.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import javax.transaction.Transactional;
import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;


@Service
public class UserDetailsServiceImp implements UserDetailsService {



    @Autowired
    public UserRepository userRepository;
    @Autowired
    RoleRepository roleRepository;

    public UserDetailsServiceImp(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // Recherche de l'utilisateur par e-mail
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

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
