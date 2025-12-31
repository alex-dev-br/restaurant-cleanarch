package br.com.techchallenge.restaurant_cleanarch.core.exception;

public class UserTypeWithoutRolesException extends BusinessException {
    public UserTypeWithoutRolesException() {
        super("User type must have at least one role valid.");
    }
}
