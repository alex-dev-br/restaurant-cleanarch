package br.com.techchallenge.restaurant_cleanarch.core.exception;

public class RestaurantNameIsAlreadyInUseException extends BusinessException {
    public RestaurantNameIsAlreadyInUseException() {
        super("Restaurant name is already in use.");
    }
}
