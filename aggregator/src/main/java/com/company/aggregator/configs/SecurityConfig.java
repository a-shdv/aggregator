package com.company.aggregator.configs;

import com.company.aggregator.enums.Role;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.LogoutConfigurer;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import java.io.IOException;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(requests ->
                        requests
                                .requestMatchers(new AntPathRequestMatcher("/sign-in")).permitAll()
                                .requestMatchers(new AntPathRequestMatcher("/sign-up")).permitAll()
                                .requestMatchers(new AntPathRequestMatcher("/images/**")).permitAll()
                                .requestMatchers(new AntPathRequestMatcher("/img/**")).permitAll()
                                .requestMatchers(new AntPathRequestMatcher("/about/**")).permitAll()
                                .requestMatchers(new AntPathRequestMatcher("/admin")).hasAuthority(Role.ADMIN.getAuthority())
                                .requestMatchers(new AntPathRequestMatcher("/statistics")).hasAuthority(Role.ADMIN.getAuthority())
                                .anyRequest().authenticated()
                ).formLogin(form ->
                        form
                                .loginPage("/sign-in")
                                .loginProcessingUrl("/sign-in")
                                .defaultSuccessUrl("/")
                                .failureHandler(authenticationFailureHandler())
                                .permitAll()
                ).logout(LogoutConfigurer::permitAll);
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationFailureHandler authenticationFailureHandler() {
        return new CustomAuthenticationFailureHandler();
    }
}

class CustomAuthenticationFailureHandler implements AuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest request,
                                        HttpServletResponse response,
                                        AuthenticationException exception) throws IOException {
        response.sendRedirect("sign-in?error=" + exception.getClass().getSimpleName());
    }
}
