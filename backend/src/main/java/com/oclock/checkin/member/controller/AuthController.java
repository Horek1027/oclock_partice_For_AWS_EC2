package com.oclock.checkin.member.controller;

import com.oclock.checkin.common.ApiResponse;
import com.oclock.checkin.member.dto.LoginRequest;
import com.oclock.checkin.member.dto.LoginResponse;
import com.oclock.checkin.member.dto.MemberResponse;
import com.oclock.checkin.member.service.MemberService;
import com.oclock.checkin.security.CustomUserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Auth", description = "登入與 Token 驗證")
public class AuthController {

    private final MemberService memberService;

    public AuthController(MemberService memberService) {
        this.memberService = memberService;
    }

    @PostMapping("/login")
    @Operation(summary = "會員登入", description = "帳密驗證成功後簽發有效期 10 分鐘的 JWT")
    public ApiResponse<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return ApiResponse.ok(memberService.login(request));
    }

    @GetMapping("/me")
    @Operation(summary = "驗證目前 Token", description = "需帶有效 JWT，用於驗證 token 是否有效並回傳使用者資訊")
    public ApiResponse<MemberResponse> me(@AuthenticationPrincipal CustomUserPrincipal principal) {
        return ApiResponse.ok(memberService.getCurrentMember(principal.memberId()));
    }

    @PostMapping("/refresh")
    @Operation(
            summary = "背景換發 Token",
            description = "需帶目前尚未過期的 JWT，換發一組新的 10 分鐘效期 token，供前端在到期前背景自動續期使用"
    )
    public ApiResponse<LoginResponse> refresh(@AuthenticationPrincipal CustomUserPrincipal principal) {
        return ApiResponse.ok(memberService.refreshToken(principal.memberId()));
    }
}
