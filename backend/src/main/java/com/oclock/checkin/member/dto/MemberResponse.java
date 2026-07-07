package com.oclock.checkin.member.dto;

import java.time.LocalDateTime;

public record MemberResponse(
        Long id,
        String employeeNo,
        String name,
        String email,
        String phone,
        String role,
        String status,
        LocalDateTime createdAt
) {
}
