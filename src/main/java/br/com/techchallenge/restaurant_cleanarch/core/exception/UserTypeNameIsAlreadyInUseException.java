package br.com.techchallenge.restaurant_cleanarch.core.exception;

public class UserTypeNameIsAlreadyInUseException extends BusinessException {
    public UserTypeNameIsAlreadyInUseException() {
        super("User type name is already in use.");
    }
}
