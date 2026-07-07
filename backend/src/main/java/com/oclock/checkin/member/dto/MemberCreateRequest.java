package com.oclock.checkin.member.dto;

import com.oclock.checkin.member.entity.Member;
import com.oclock.checkin.member.entity.MemberRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class MemberCreateRequest {

    @NotBlank(message = "員工編號為必填")
    private String employeeNo;

    @NotBlank(message = "姓名為必填")
    private String name;

    @NotBlank(message = "Email 為必填")
    @Email(message = "Email 格式不正確")
    private String email;

    @NotBlank(message = "密碼為必填")
    @Size(min = 8, message = "密碼至少 8 碼")
    private String password;

    private String phone;

    private MemberRole role;
}
