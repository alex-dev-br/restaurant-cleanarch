package br.com.techchallenge.restaurant_cleanarch.core.gateway;

public interface PasswordHasherGateway {
    String hash(String rawPassword);
}
