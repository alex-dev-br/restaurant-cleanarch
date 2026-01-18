package br.com.techchallenge.restaurant_cleanarch.infra.config;

import br.com.techchallenge.restaurant_cleanarch.core.domain.roles.MenuItemRoles;
import br.com.techchallenge.restaurant_cleanarch.core.domain.roles.RoleRoles;
import br.com.techchallenge.restaurant_cleanarch.core.domain.roles.UserManagementRoles;
import br.com.techchallenge.restaurant_cleanarch.core.domain.roles.UserTypeRoles;
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
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;

@Configuration
@EnableWebSecurity
@Profile("!dev")
@EnableMethodSecurity(securedEnabled = true) // Habilita segurança em métodos
public class SecurityConfig {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) {
        final var userTypeBaseUrl = "/user-types";
        final var userTypeWithIdUrl = "/user-types/{id}";
        final var userBaseUrl = "/user";
        final var userWithIdUrl = "/user/{id}";

        http
            .cors(Customizer.withDefaults())
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
                .requestMatchers(HttpMethod.GET,"/restaurants", "/restaurants/{id}", "/restaurants/{restaurant-id}/menu").permitAll()
                .requestMatchers(HttpMethod.GET, "/roles").hasAuthority(RoleRoles.VIEW_ROLE.getRoleName())

                .requestMatchers(HttpMethod.POST, userTypeBaseUrl).hasAuthority(UserTypeRoles.CREATE_USER_TYPE.getRoleName())
                .requestMatchers(HttpMethod.PUT, userTypeWithIdUrl).hasAuthority(UserTypeRoles.UPDATE_USER_TYPE.getRoleName())
                .requestMatchers(HttpMethod.DELETE, userTypeWithIdUrl).hasAuthority(UserTypeRoles.DELETE_USER_TYPE.getRoleName())
                .requestMatchers(HttpMethod.GET, userTypeBaseUrl).hasAuthority(UserTypeRoles.VIEW_USER_TYPE.getRoleName())
                .requestMatchers(HttpMethod.GET, userTypeWithIdUrl).hasAuthority(UserTypeRoles.VIEW_USER_TYPE.getRoleName())

                .requestMatchers(HttpMethod.POST, userBaseUrl).hasAuthority(UserManagementRoles.CREATE_USER.getRoleName())
                .requestMatchers(HttpMethod.GET, userBaseUrl).hasAuthority(UserManagementRoles.VIEW_USER.getRoleName())
                .requestMatchers(HttpMethod.PUT, userWithIdUrl).hasAuthority(UserManagementRoles.UPDATE_USER.getRoleName())
                .requestMatchers(HttpMethod.GET, userWithIdUrl).hasAuthority(UserManagementRoles.VIEW_USER.getRoleName())
                .requestMatchers(HttpMethod.DELETE, userWithIdUrl).hasAuthority(UserManagementRoles.DELETE_USER.getRoleName())

                .requestMatchers(HttpMethod.POST, "/restaurants/{restaurant-id}/menu").hasAuthority(MenuItemRoles.CREATE_MENU_ITEM.getRoleName())
                .requestMatchers(HttpMethod.PUT, "/restaurants/{restaurant-id}/menu/{id}").hasAuthority(MenuItemRoles.UPDATE_MENU_ITEM.getRoleName())
                .anyRequest().authenticated() // Boa prática: fechar com uma regra padrão
            )
            .httpBasic(Customizer.withDefaults())
            .csrf(AbstractHttpConfigurer::disable);
        http.exceptionHandling(customizer ->
            customizer.accessDeniedHandler(accessDeniedHandler())
                    .authenticationEntryPoint(authenticationEntryPoint())
        );
        return http.build();
    }

    private AccessDeniedHandler accessDeniedHandler() {
        return (request, response, accessDeniedException) -> {
            var writer = response.getWriter();
            response.setStatus(403);
            response.setContentType("application/json");
            writer.write(objectMapper.writeValueAsString(new SimpleErroResponse("The current user does not have permission.")));
        };
    }

    private AuthenticationEntryPoint authenticationEntryPoint() {
        return (request, response, authException) -> {
            var writer = response.getWriter();
            response.setStatus(401);
            response.setContentType("application/json");
            writer.write(objectMapper.writeValueAsString(new SimpleErroResponse("User not authenticated. Authentication is required to access this resource.")));
        };
    }
}