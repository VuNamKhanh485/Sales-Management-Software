package com.g4fpt.sms.product.exception;

import com.g4fpt.sms.product.util.ValidationError;

import java.util.ArrayList;
import java.util.List;

public class ValidationException extends RuntimeException {
    private final List<ValidationError> errors;

    public ValidationException(List<ValidationError> errors) {
        super("Validation failed: ");
        this.errors = errors;
    }

    public List<ValidationError> getErrors() {
        return errors;
    }
}
