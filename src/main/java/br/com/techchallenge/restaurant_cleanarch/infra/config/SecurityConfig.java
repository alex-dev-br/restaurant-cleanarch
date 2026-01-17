package br.com.techchallenge.restaurant_cleanarch.infra.config;

import br.com.techchallenge.restaurant_cleanarch.core.domain.roles.RoleRoles;
import br.com.techchallenge.restaurant_cleanarch.infra.controller.response.SimpleErroResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;

@Configuration
@EnableWebSecurity
@Profile("!dev")
@EnableMethodSecurity(securedEnabled = true) // Habilita segurança em métodos
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) {
        http.csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults())
                .httpBasic(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(req -> req
                    .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                    .requestMatchers(
                            "/v3/api-docs/**",
                            "/swagger-ui/**",
                            "/swagger-ui.html",
                            "/actuator/health",
                            "/api/v1/_ping"
                    ).permitAll()
                    .requestMatchers(HttpMethod.GET, "/restaurants", "/restaurants/*").permitAll()
                    .requestMatchers(HttpMethod.GET, "/roles").hasAuthority(RoleRoles.VIEW_ROLE.getRoleName())
                    .anyRequest().authenticated() // Boa prática: fechar com uma regra padrão
                );
        http.exceptionHandling(customizer -> customizer.accessDeniedHandler(accessDeniedHandler()));
        return http.build();
    }

    private AccessDeniedHandler accessDeniedHandler() {
        return (request, response, accessDeniedException) -> {
            var writer = response.getWriter();
            response.setStatus(403);
            response.setContentType("application/json");
            writer.write(new ObjectMapper().writeValueAsString(new SimpleErroResponse("The current user does not have permission.")));
        };
    }
}