package com.g4fpt.sms.employee.dto;

import com.g4fpt.sms.employee.utils.Gender;
import com.g4fpt.sms.employee.utils.WorkStatus;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class EmployeeForm {

    private Long id;

    @NotBlank(message = "Mã nhân viên không được để trống")
    private String employeeCode;

    @NotBlank(message = "Họ tên không được để trống")
    private String fullName;

    @NotBlank(message = "Email không được để trống")
    @Email(message = "Email không đúng định dạng")
    private String email;

    private String password;

    private String phone;

    private String address;

    private Gender gender;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate dob;

    @NotNull(message = "Ngày vào làm không được để trống")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate hiredDate;

    private BigDecimal baseSalary;

    @NotNull(message = "Trạng thái không được để trống")
    private WorkStatus workStatus = WorkStatus.ACTIVE;

    private String note;

    private Long branchId;

    @NotNull(message = "Vai trò không được để trống")
    private Long roleId;
}