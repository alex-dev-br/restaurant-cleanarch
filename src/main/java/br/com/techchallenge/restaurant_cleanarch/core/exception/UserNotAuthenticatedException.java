package br.com.techchallenge.restaurant_cleanarch.core.exception;

public class UserNotAuthenticatedException extends BusinessException{
    public UserNotAuthenticatedException() {
        super("User is not authenticated.");
    }
}
