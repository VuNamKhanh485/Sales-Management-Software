package com.g4fpt.sms.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus; // Đảm bảo đã import class này

@Getter
public enum ErrorCode {

    VOUCHER_NOT_FOUND(4040, "Voucher không tồn tại", HttpStatus.NOT_FOUND),
    VOUCHER_CODE_EXISTED(4090, "Mã voucher đã tồn tại", HttpStatus.CONFLICT),
    VOUCHER_INVALID_TIME_RANGE(4000, "Thời gian kết thúc phải sau thời gian bắt đầu", HttpStatus.BAD_REQUEST),
    VOUCHER_INVALID_PERCENT(4001, "Giá trị phần trăm không được vượt quá 100", HttpStatus.BAD_REQUEST),
    VOUCHER_START_DATE_PAST(4002, "Thời gian bắt đầu không được ở trong quá khứ", HttpStatus.BAD_REQUEST),
    VOUCHER_ALREADY_USED(4003, "Voucher này đã được sử dụng, không thể xóa", HttpStatus.BAD_REQUEST);

    private final int customCode;
    private final String message;
    private final HttpStatus httpStatus;


    ErrorCode(int customCode, String message, HttpStatus httpStatus) {
        this.customCode = customCode;
        this.message = message;
        this.httpStatus = httpStatus;
    }
}