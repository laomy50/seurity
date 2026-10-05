package com.example.seurity.dto;



import lombok.Data;

@Data
public class RegisterUserDto {
    private String password;
    private String username;
    private String roleId;
    private String firstName;
    private String lastName;
    private String secondName;
    private String phoneNumber;
    private String email;

    public RegisterUserDto() {
    }

    public RegisterUserDto(String password, String username, String roleId, String firstName, String lastName, String secondName, String phoneNumber, String email) {
        this.password = password;
        this.username = username;
        this.roleId = roleId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.secondName = secondName;
        this.phoneNumber = phoneNumber;
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRoleId() {
        return roleId;
    }

    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getSecondName() {
        return secondName;
    }

    public void setSecondName(String secondName) {
        this.secondName = secondName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}



