package br.com.techchallenge.restaurant_cleanarch.infra.config;

import br.com.techchallenge.restaurant_cleanarch.core.gateway.UserGateway;
import br.com.techchallenge.restaurant_cleanarch.infra.mapper.UserMapper;
import br.com.techchallenge.restaurant_cleanarch.infra.persistence.adapter.UserGatewayAdapter;
import br.com.techchallenge.restaurant_cleanarch.infra.persistence.repository.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {

    @Bean
    public UserGateway userGateway(UserRepository repository, UserMapper userMapper) {
        return new UserGatewayAdapter(repository, userMapper);
    }
}
