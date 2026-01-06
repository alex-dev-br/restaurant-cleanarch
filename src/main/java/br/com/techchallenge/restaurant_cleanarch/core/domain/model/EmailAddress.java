package br.com.techchallenge.restaurant_cleanarch.core.domain.model;

import br.com.techchallenge.restaurant_cleanarch.core.exception.BusinessException;
import java.util.regex.Pattern;

public record EmailAddress(String address) {

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");

    public EmailAddress {
        if (address == null || !EMAIL_PATTERN.matcher(address).matches()) {
            throw new BusinessException("Invalid email address.");
        }
    }
}
