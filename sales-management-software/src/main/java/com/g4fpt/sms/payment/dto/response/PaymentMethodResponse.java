package com.g4fpt.sms.payment.dto.response;

import com.g4fpt.sms.payment.enums.PaymentStatus;
import lombok.*;

import java.time.LocalDateTime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class PaymentMethodResponse {

    private Long id;
    private String code;
    private String name;
    private PaymentStatus status;
    private LocalDateTime createdAt;
}
