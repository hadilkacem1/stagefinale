package com.Telnet.AuthService.config;

import com.Telnet.AuthService.filter.JwtAuthenticationFilter;
import com.Telnet.AuthService.model.ERole;
import com.Telnet.AuthService.service.UserDetailsServiceImp;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final UserDetailsServiceImp userDetailsServiceImp;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final CustomLogoutHandler logoutHandler;

    public SecurityConfig(UserDetailsServiceImp userDetailsServiceImp,
                          JwtAuthenticationFilter jwtAuthenticationFilter,
                          CustomLogoutHandler logoutHandler) {
        this.userDetailsServiceImp = userDetailsServiceImp;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.logoutHandler = logoutHandler;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .cors().and()
                .authorizeRequests()
                .antMatchers("/login/**", "/register/**", "/api/users/**","/auth-service/**","/auth/**").permitAll()
                .antMatchers("/api/analyse/**")
                .hasAnyAuthority("ROLE_" + ERole.RESPONSABLEQUALITE.name(), "ROLE_" + ERole.CHEFDEPROJET.name())
                .antMatchers("/admin_only/**").hasAuthority("ROLE_" + ERole.ADMIN.name())
                .antMatchers("/api/kpi/**")
                .hasAnyAuthority("ROLE_" + ERole.RESPONSABLEQUALITE.name(), "ROLE_" + ERole.CHEFDEPROJET.name(), "ROLE_" + ERole.ADMIN.name())
                .antMatchers("/api/processus/**").hasAuthority("ROLE_" + ERole.ADMIN.name())
                .antMatchers("/api/resultspip/**")
                .hasAnyAuthority("ROLE_" + ERole.CHEFDEPROJET.name(), "ROLE_" + ERole.RESPONSABLEQUALITE.name())
                .antMatchers("/tokens/**").permitAll()
                .antMatchers("/tokens/userDetails").permitAll()
                .antMatchers("/usercurrent").permitAll()
                .antMatchers("/*/role").permitAll()
                .antMatchers(HttpMethod.GET, "/list", "/api/users/{email}", "/roles", "/{userId}/role", "/usercurrent", "/users/{userId}").permitAll()
                .antMatchers(HttpMethod.POST, "/login", "/register").permitAll()
                .antMatchers(HttpMethod.PUT, "/users/{userId}").permitAll()
                .antMatchers(HttpMethod.DELETE, "/{userId}", "/logout").permitAll()
                .antMatchers("/useremail").permitAll()
                .anyRequest().authenticated()
                .and()
                .userDetailsService(userDetailsServiceImp)
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling()
                .accessDeniedHandler((request, response, accessDeniedException) -> response.setStatus(HttpStatus.FORBIDDEN.value()))
                .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
                .and()
                .logout()
                .logoutUrl("/logout")
                .addLogoutHandler(logoutHandler)
                .logoutSuccessHandler((request, response, authentication) -> SecurityContextHolder.clearContext());

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public OncePerRequestFilter cspHeaderFilter() {
        return new OncePerRequestFilter() {
            @Override
            protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
                    throws ServletException, IOException {
                response.setHeader("Content-Security-Policy",
                        "default-src 'self' https://localhost:4200; " +
                                "script-src 'self' 'unsafe-inline' 'unsafe-eval' https://trusted-scripts.com; " +
                                "style-src 'self' 'unsafe-inline' https://trusted-styles.com; " +
                                "img-src 'self' data:; " +
                                "font-src 'self' https://trusted-fonts.com; " +
                                "object-src 'none'; " +
                                "frame-ancestors 'self'; " +
                                "base-uri 'self'; " +
                                "form-action 'self';");

                response.setHeader("X-Frame-Options", "SAMEORIGIN"); // Ajout de la directive X-Frame-Options
                filterChain.doFilter(request, response);
            }
        };
    }
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

}
