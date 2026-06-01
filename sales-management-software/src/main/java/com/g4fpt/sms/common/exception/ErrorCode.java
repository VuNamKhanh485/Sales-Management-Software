package com.g4fpt.sms.common.exception;


import lombok.Getter;

@Getter
public enum ErrorCode {


    VOUCHER_NOT_FOUND(404, "Không tìm thấy voucher"),
    VOUCHER_CODE_EXISTED(400, "Mã voucher đã tồn tại"),
    VOUCHER_EXPIRED(400, "Voucher đã hết hạn sử dụng"),
    VOUCHER_USAGE_LIMIT_EXCEEDED(400, "Voucher đã đạt giới hạn sử dụng"),
    VOUCHER_INACTIVE(400, "Voucher không còn hiệu lực"),
    VOUCHER_MIN_ORDER_NOT_MET(400, "Đơn hàng chưa đạt giá trị tối thiểu để dùng voucher");



    private final int httpStatus;
    private final String message;

    ErrorCode(int httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }
}