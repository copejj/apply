package com.braindribbler.apply.config;

import java.util.Arrays;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.security.web.util.matcher.IpAddressMatcher;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true)
public class SecurityConfig {

        @Bean
        public PasswordEncoder passwordEncoder() {
                return Argon2PasswordEncoder.defaultsForSpringSecurity_v5_8();
        }

        @Bean
        public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
                http
                                .authorizeHttpRequests(auth -> auth
                                                .requestMatchers("/", "/error", "/login", "/about", "/join/**",
                                                                "/auth/**", "/css/**", "/js/**",
                                                                "/images/**", "/favicon.ico")
                                                .permitAll()

                                                .requestMatchers("/actuator/health")
                                                .access(hasIpAddresses("127.0.0.1", "::1"))
                                                .requestMatchers("/login?error", "/login?logout").permitAll()
                                                .anyRequest().authenticated())
                                .formLogin(form -> form
                                                .loginPage("/login")
                                                .loginProcessingUrl("/login")
                                                .defaultSuccessUrl("/", true)
                                                .permitAll())
                                .logout(logout -> logout
                                                .logoutSuccessUrl("/login?logout")
                                                .permitAll());

                return http.build();
        }

        private static AuthorizationManager<RequestAuthorizationContext> hasIpAddresses(String... ipAddresses) {
                List<IpAddressMatcher> matchers = Arrays.stream(ipAddresses)
                                .map(IpAddressMatcher::new)
                                .toList();
                return (authentication, context) -> {
                        boolean matches = matchers.stream()
                                        .anyMatch(matcher -> matcher.matches(context.getRequest()));
                        return new AuthorizationDecision(matches);
                };
        }
}