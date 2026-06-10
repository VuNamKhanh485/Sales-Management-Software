package com.g4fpt.sms.branch.dto;

import com.g4fpt.sms.branch.entity.BranchStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BranchRequest {

    private String branchCode;

    private String name;

    private String phone;

    private String email;

    private String address;

    private BranchStatus status;

    private String note;
}
