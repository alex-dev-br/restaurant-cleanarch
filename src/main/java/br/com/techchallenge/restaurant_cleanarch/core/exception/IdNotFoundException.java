package br.com.techchallenge.restaurant_cleanarch.core.exception;

public class IdNotFoundException extends BusinessException {
    public IdNotFoundException(String message) {
        super(message);
    }
}
