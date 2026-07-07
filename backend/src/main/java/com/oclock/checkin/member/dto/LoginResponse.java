package com.oclock.checkin.member.dto;

public record LoginResponse(
        String token,
        String tokenType,
        long expiresInSeconds,
        MemberResponse member
) {
}
