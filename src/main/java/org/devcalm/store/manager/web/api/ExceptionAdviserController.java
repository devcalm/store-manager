package org.devcalm.store.manager.web.api;

import lombok.RequiredArgsConstructor;
import org.devcalm.store.manager.domain.exception.EntityNotFoundException;
import org.devcalm.store.manager.domain.exception.StoreException;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.server.ServerWebExchange;

import java.time.Instant;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestControllerAdvice
@RequiredArgsConstructor
public class ExceptionAdviserController {

    private final MessageSource messageSource;

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ProblemDetail entityNotFound(EntityNotFoundException ex, ServerHttpRequest request, ServerWebExchange exchange) {
        var problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, messageSource.getMessage(ex.getMessage(), null, ex.getLocalizedMessage(), extractLocale(exchange)));
        problemDetail.setTitle("Resource Not Found");
        problemDetail.setInstance(request.getURI());
        problemDetail.setProperty("timestamp", Instant.now());
        return problemDetail;
    }

    @ExceptionHandler(StoreException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    public ProblemDetail commonError(StoreException ex, ServerHttpRequest request, ServerWebExchange exchange) {
        var problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, messageSource.getMessage(ex.getMessage(), null, ex.getLocalizedMessage(), extractLocale(exchange)));
        problemDetail.setTitle("Client error");
        problemDetail.setInstance(request.getURI());
        problemDetail.setProperty("timestamp", Instant.now());
        return problemDetail;
    }

    @ExceptionHandler(WebExchangeBindException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    public ProblemDetail serviceException(WebExchangeBindException ex, ServerHttpRequest request, ServerWebExchange exchange) {
        var errors = ex.getBindingResult().getAllErrors().stream().map(
                error -> {
                    String fieldName = ((FieldError) error).getField();
                    String errorMessage = messageSource.getMessage(ex.getMessage(), error.getArguments(), error.getDefaultMessage(), extractLocale(exchange));
                    return Map.entry(fieldName, errorMessage == null ? "error" : errorMessage);
                }
        ).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (a, b) -> a));

        var detail =  "Validation failed for elements: " + String.join(",", errors.keySet());
        var problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.UNPROCESSABLE_ENTITY, detail);
        problemDetail.setTitle("Validation Errors");
        problemDetail.setInstance(request.getURI());
        problemDetail.setProperty("timestamp", Instant.now());
        problemDetail.setProperty("errors", errors);
        return problemDetail;
    }

    private Locale extractLocale(ServerWebExchange exchange) {
        return Optional.ofNullable(exchange.getLocaleContext().getLocale())
                .orElse(LocaleContextHolder.getLocale());
    }
}
