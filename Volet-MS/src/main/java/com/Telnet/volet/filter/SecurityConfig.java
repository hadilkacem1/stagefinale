package com.Telnet.volet.filter;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .cors().and()
                .authorizeRequests()
                .antMatchers(HttpMethod.GET, "/volet/**", "/auth/**", "/auth-service/login").permitAll()
                .antMatchers(HttpMethod.GET, "/api/pip/**").permitAll() // PermitAll for now
                .antMatchers("/activities", "/api/cadran/**", "/api/volet/**").permitAll()
                .antMatchers("/auth-service/**").permitAll()
                .anyRequest().authenticated()
                .and()
                // Configure other security parameters, such as session management and exception handling
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling()
                .accessDeniedHandler((request, response, accessDeniedException) -> response.setStatus(HttpStatus.FORBIDDEN.value()))
                .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
                .and()
                .logout()
                .logoutUrl("/logout")
                .logoutSuccessHandler((request, response, authentication) -> SecurityContextHolder.clearContext());
    }

    /*@Bean
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

                response.setHeader("X-Frame-Options", "SAMEORIGIN"); // Added X-Frame-Options directive
                filterChain.doFilter(request, response);
            }
        };
    }*/
}
