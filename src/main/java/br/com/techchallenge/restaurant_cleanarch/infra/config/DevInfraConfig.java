package br.com.techchallenge.restaurant_cleanarch.infra.config;

import br.com.techchallenge.restaurant_cleanarch.infra.auth.FakeLoggedUserContext;
import br.com.techchallenge.restaurant_cleanarch.infra.auth.FakeUsers;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@Profile("dev")
public class DevInfraConfig {

    @Bean
    public FakeLoggedUserContext fakeLoggedUserContext() {
        return new FakeLoggedUserContext(FakeUsers.devAdminOwnerUser());
    }

    @Bean
    SecurityFilterChain devSecurityFilterChain(HttpSecurity http) throws Exception {
        return http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/h2-console/**").permitAll()
                        .anyRequest().permitAll()
                )
                .csrf(AbstractHttpConfigurer::disable)
                .headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable))
                .build();
    }
}
