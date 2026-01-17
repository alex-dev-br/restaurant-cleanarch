package br.com.techchallenge.restaurant_cleanarch.infra.config;

import br.com.techchallenge.restaurant_cleanarch.core.exception.OperationNotAllowedException;
import br.com.techchallenge.restaurant_cleanarch.infra.controller.response.FieldErrorResponse;
import br.com.techchallenge.restaurant_cleanarch.infra.controller.response.SimpleErroResponse;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ArrayList;
import java.util.List;

@RestControllerAdvice
public class ErrorHandle {

    private final MessageSource messageSource;

    public ErrorHandle(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @ResponseStatus(code =  HttpStatus.FORBIDDEN)
    @ExceptionHandler({OperationNotAllowedException.class})
    public SimpleErroResponse handleOperationNotAllowedException(OperationNotAllowedException e) {
        return new SimpleErroResponse(e.getMessage());
    }

    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public List<FieldErrorResponse> handle(MethodArgumentNotValidException exception) {
        List<FieldError> fieldErrors = exception.getBindingResult().getFieldErrors();

        List<FieldErrorResponse> errors = new ArrayList<>();

        fieldErrors.forEach(error -> {
            String field  = error.getField();
            String message = messageSource.getMessage(error, LocaleContextHolder.getLocale());
            errors.add(new FieldErrorResponse(field, message));
        });

        return errors;
    }
}
