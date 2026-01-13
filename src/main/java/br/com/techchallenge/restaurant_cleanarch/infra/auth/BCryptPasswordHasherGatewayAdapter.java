package br.com.techchallenge.restaurant_cleanarch.infra.auth;


import br.com.techchallenge.restaurant_cleanarch.core.gateway.PasswordHasherGateway;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class BCryptPasswordHasherGatewayAdapter implements PasswordHasherGateway {

    private final PasswordEncoder encoder;

    public BCryptPasswordHasherGatewayAdapter(PasswordEncoder encoder) {
        this.encoder = Objects.requireNonNull(encoder, "PasswordEncoder cannot be null");
    }

    @Override
    public String hash(String rawPassword) {
        Objects.requireNonNull(rawPassword, "rawPassword cannot be null");
        return encoder.encode(rawPassword);
    }

    @Override
    public boolean matches(String rawPassword, String hashedPassword) {
        Objects.requireNonNull(rawPassword, "rawPassword cannot be null");
        Objects.requireNonNull(hashedPassword, "hashedPassword cannot be null");
        return encoder.matches(rawPassword, hashedPassword);
    }
}
