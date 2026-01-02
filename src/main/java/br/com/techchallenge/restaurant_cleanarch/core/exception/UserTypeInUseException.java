package br.com.techchallenge.restaurant_cleanarch.core.exception;

public class UserTypeInUseException extends BusinessException {

    public UserTypeInUseException() {
        super("The user type is in use and cannot be deleted.");
    }
}
