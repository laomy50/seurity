package com.example.seurity.dto;

import lombok.Data;

@Data
public class RoleDTO {
    private String roleId;
    private String roleName;
    private int roleStatus;

    public RoleDTO() {
    }

    public RoleDTO(String roleId, String roleName, int roleStatus) {
        this.roleId = roleId;
        this.roleName = roleName;
        this.roleStatus = roleStatus;
    }

    public String getRoleId() {
        return roleId;
    }

    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public int getRoleStatus() {
        return roleStatus;
    }

    public void setRoleStatus(int roleStatus) {
        this.roleStatus = roleStatus;
    }
}


