package com.dad.service.dashboard.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * User Entity (simplified for Feign)
 */
@Data
public class User {

    private Long id;
    private String guid;
    private String username;
    private String email;
    private String nickname;
    private String avatar;
    private String phone;
    private String status;
    private String role;
    private String department;
    private String position;
    private LocalDateTime lastLoginAt;
    private String lastLoginIp;
}
