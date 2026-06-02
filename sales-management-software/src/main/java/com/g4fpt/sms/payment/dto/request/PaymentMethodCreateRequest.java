package com.g4fpt.sms.payment.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class PaymentMethodCreateRequest {

    @NotBlank(message = "Mã phương thức không được để trống")
    @Size(max = 255, message = "Mã phương thức không quá 255 ký tự")
    private String code;

    @NotBlank(message = "Tên phương thức không được để trống")
    @Size(max = 255, message = "Tên phương thức không quá 255 ký tự")
    private String name;

    private String status;
}
