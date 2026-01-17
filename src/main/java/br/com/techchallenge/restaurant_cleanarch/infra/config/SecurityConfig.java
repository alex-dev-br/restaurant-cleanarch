package br.com.techchallenge.restaurant_cleanarch.infra.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.core.authority.mapping.SimpleAuthorityMapper;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@Profile("!dev")
@EnableMethodSecurity(securedEnabled = true) // Habilita segurança em métodos
public class SecurityConfig {

    /**
     * Desabilita o prefixo "ROLE_" padrão do Spring Security.
     * <p>
     * Por padrão, uma verificação hasRole("ADMIN") procura pela autoridade "ROLE_ADMIN".
     * Com esta configuração, a verificação hasRole("ADMIN") procurará pela autoridade "ADMIN",
     * tornando o comportamento consistente com o uso de hasAuthority("ADMIN").
     *
     * @return um mapper que não adiciona nenhum prefixo.
     */
    @Bean
    public GrantedAuthoritiesMapper grantedAuthoritiesMapper() {
        SimpleAuthorityMapper mapper = new SimpleAuthorityMapper();
        mapper.setPrefix(""); // A mágica acontece aqui: define o prefixo como uma string vazia.
        return mapper;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) {
        // Adicione aqui sua configuração de filter chain principal.
        // Exemplo mínimo para a aplicação funcionar:
        return http.csrf(AbstractHttpConfigurer::disable).build();
    }
}