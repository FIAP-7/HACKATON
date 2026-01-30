package br.com.sus.ingestao.infra.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, KeycloakJwtConverter keycloakJwtConverter) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/actuator/**", "/swagger-ui/**", "/v3/api-docs/**").permitAll()
                        .requestMatchers("/api/v1/webhook/**").authenticated()
                        .requestMatchers("/api/v1/integracao/**").authenticated()
                        .anyRequest().permitAll()
                )
                .httpBasic(basic -> {})
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt.jwtAuthenticationConverter(keycloakJwtConverter))
                );
        return http.build();
    }

    @Bean
    public KeycloakJwtConverter keycloakJwtConverter() {
        return new KeycloakJwtConverter();
    }

    @Bean
    public UserDetailsService userDetailsService(Environment env) {
        String user = env.getProperty("WEBHOOK_BASIC_USER", "webhook-user");
        String pass = env.getProperty("WEBHOOK_BASIC_PASSWORD", "webhook-pass");
        UserDetails webhookUser = User.withUsername(user)
                .password("{noop}" + pass)
                .roles("WEBHOOK")
                .build();
        return new InMemoryUserDetailsManager(webhookUser);
    }
}
