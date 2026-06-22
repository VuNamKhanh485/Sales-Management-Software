package com.g4fpt.sms.common.exception;

import com.g4fpt.sms.product.util.ValidationError;
import lombok.Getter;

import java.util.List;
@Getter
public class ValidationException extends RuntimeException {
    private final List<ValidationError> errors;

    public ValidationException(List<ValidationError> errors) {
        super("Validation failed: ");
        this.errors = errors;
    }
}
