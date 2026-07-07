package com.oclock.checkin.security;

public record CustomUserPrincipal(Long memberId, String employeeNo, String name, String role) {
}
