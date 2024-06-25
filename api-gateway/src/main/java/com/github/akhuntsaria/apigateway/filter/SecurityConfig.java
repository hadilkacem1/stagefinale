package com.github.akhuntsaria.apigateway.filter;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

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

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .authorizeRequests()
               // .requestMatchers(r -> r.getRemoteAddr().equals("169.254.169.254")).denyAll()
                .antMatchers("/login/**", "/register/**", "/api/users/**", "/auth-service/**", "/auth/**").permitAll()
                .antMatchers("/auth-service/**", "/projet/**", "/volet/**", "/analyse/**", "/auth/**").permitAll()
                .antMatchers("/tokens/**").permitAll()
                .antMatchers("/tokens/userDetails").permitAll()
                .antMatchers("/projet-service/**").permitAll()
                .antMatchers("/volet-service/**").permitAll()
                .antMatchers("/pip-service/**").permitAll()
                .antMatchers("/analyse-service/**").permitAll()
                .antMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                .and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
          //      .addFilterAfter(cspHeaderFilter(), UsernamePasswordAuthenticationFilter.class) // Ajout du filtre CSP
                .exceptionHandling()
                .accessDeniedHandler((request, response, accessDeniedException) -> response.setStatus(HttpStatus.FORBIDDEN.value()))
                .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
                .and()
                .logout()
                .logoutUrl("/logout")
                .logoutSuccessHandler((request, response, authentication) -> SecurityContextHolder.clearContext());
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsServiceImp).passwordEncoder(passwordEncoder());
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

/*    @Bean
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
    }*/
}
