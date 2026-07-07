package com.oclock.checkin.member.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequest {

    @NotBlank(message = "Email 為必填")
    @Email(message = "Email 格式不正確")
    private String email;

    @NotBlank(message = "密碼為必填")
    private String password;
}
