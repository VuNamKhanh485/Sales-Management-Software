package com.g4fpt.sms.auth.dto;

public enum OtpValidationResult {

    VALID,
    INVALID,
    EXPIRED,
    NOT_FOUND,
    MAX_ATTEMPTS_EXCEEDED
}