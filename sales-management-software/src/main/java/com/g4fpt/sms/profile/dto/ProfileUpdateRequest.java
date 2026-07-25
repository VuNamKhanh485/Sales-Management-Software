package com.g4fpt.sms.profile.dto;

import com.g4fpt.sms.employee.utils.Gender;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProfileUpdateRequest {

    @NotBlank(message = "Họ và tên không được để trống")
    private String fullName;

    @NotBlank(message = "Số điện thoại không được để trống")
    @Pattern(regexp = "^[0-9]{10}$", message = "Số điện thoại phải gồm đúng 10 chữ số")
    private String phone;

    private Gender gender;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @PastOrPresent(message = "Ngày sinh không được lớn hơn ngày hiện tại")
    private LocalDate dob;

    @NotBlank(message = "Địa chỉ không được để trống")
    private String address;
}
