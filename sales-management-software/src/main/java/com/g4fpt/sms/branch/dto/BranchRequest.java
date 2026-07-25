package com.g4fpt.sms.branch.dto;

import com.g4fpt.sms.branch.entity.BranchStatus;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BranchRequest {

    @NotBlank(message = "Mã chi nhánh không được để trống")
    private String branchCode;

    @NotBlank(message = "Tên chi nhánh không được để trống")
    private String name;

    @NotBlank(message = "Số điện thoại không được để trống")
    private String phone;

    @NotBlank(message = "Email không được để trống")
    @Email(message = "Email không đúng định dạng")
    private String email;

    @NotBlank(message = "Địa chỉ không được để trống")
    private String address;

    @NotNull(message = "Trạng thái không được để trống")
    private BranchStatus status;

    private String note;
}
