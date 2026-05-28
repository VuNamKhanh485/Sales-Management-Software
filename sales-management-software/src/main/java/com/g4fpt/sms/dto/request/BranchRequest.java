package com.g4fpt.sms.dto.request;

import com.g4fpt.sms.entity.BranchStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BranchRequest {

    private String name;

    private String address;

    private BranchStatus status;

    private Long managerId;
}