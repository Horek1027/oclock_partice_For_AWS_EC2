package com.oclock.checkin.member.dto;

public record PasswordEncodeResponse(
        String rawPassword,
        String encodedPassword
) {
}
