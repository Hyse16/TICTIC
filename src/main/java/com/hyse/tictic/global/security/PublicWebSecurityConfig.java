package com.hyse.tictic.global.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class PublicWebSecurityConfig {

    @Bean
    public SecurityFilterChain configure(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(
                                "/",
                                "/index.html",
                                "/styles/**",
                                "/images/**",
                                "/actuator/health",
                                "/actuator/info"
                        ).permitAll()
                        .anyRequest().permitAll()
                )
                .httpBasic(Customizer.withDefaults())
                .formLogin(form -> form.disable());

        return http.build();
    }
}
