package com.g4fpt.sms.auth.dto;

public class SessionUser {

    private Long id;
    private String employeeCode;
    private String fullName;
    private String email;
    private String roleCode;
    private String roleName;
    private Long branchId;
    private String branchName;

    public SessionUser() {
    }

    public SessionUser(Long id, String employeeCode, String fullName, String email,
                       String roleCode, String roleName, Long branchId, String branchName) {
        this.id = id;
        this.employeeCode = employeeCode;
        this.fullName = fullName;
        this.email = email;
        this.roleCode = roleCode;
        this.roleName = roleName;
        this.branchId = branchId;
        this.branchName = branchName;
    }

    public boolean hasRole(String roleCode) {
        return this.roleCode != null && this.roleCode.equals(roleCode);
    }

    public boolean hasAnyRole(String... roleCodes) {
        if (this.roleCode == null || roleCodes == null) {
            return false;
        }

        for (String code : roleCodes) {
            if (this.roleCode.equals(code)) {
                return true;
            }
        }

        return false;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmployeeCode() {
        return employeeCode;
    }

    public void setEmployeeCode(String employeeCode) {
        this.employeeCode = employeeCode;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRoleCode() {
        return roleCode;
    }

    public void setRoleCode(String roleCode) {
        this.roleCode = roleCode;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public Long getBranchId() {
        return branchId;
    }

    public void setBranchId(Long branchId) {
        this.branchId = branchId;
    }

    public String getBranchName() {
        return branchName;
    }

    public void setBranchName(String branchName) {
        this.branchName = branchName;
    }
}
