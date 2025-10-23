package com.zeta.backend.dto;

import com.zeta.backend.enums.Role;
import com.zeta.backend.models.User;

import java.time.ZonedDateTime;

//DTO for user response. Excludes password_hash for security.

public class UserResponse {

    private Integer id;
    private String name;
    private String email;
    private String phone;
    private Role role;
    private ZonedDateTime createdAt;

    //    constructors
    public UserResponse(){

    }
    public UserResponse(String email, Integer id, String string) {

    }

    public UserResponse(Integer id, String name, String email, String phone, Role role, ZonedDateTime createdAt) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.role = role;
        this.createdAt = createdAt;
    }

    public static UserResponse fromUser(User user) {
        return new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getPhone(),
                user.getRole(),
                user.getCreatedAt()
        );
    }

//    getters and setters

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public ZonedDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(ZonedDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
