package br.com.techchallenge.restaurant_cleanarch.core.exception;

public class UserCannotBeRestaurantOwnerException extends BusinessException {

    public UserCannotBeRestaurantOwnerException() {
        super("User cannot be restaurant owner.");
    }
}
