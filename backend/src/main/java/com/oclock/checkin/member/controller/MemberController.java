package com.oclock.checkin.member.controller;

import com.oclock.checkin.common.ApiResponse;
import com.oclock.checkin.member.dto.MemberCreateRequest;
import com.oclock.checkin.member.dto.MemberResponse;
import com.oclock.checkin.member.dto.PasswordEncodeResponse;
import com.oclock.checkin.member.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/members")
@Tag(name = "Member", description = "會員資料管理")
@Validated
public class MemberController {

    private final MemberService memberService;
    private final PasswordEncoder passwordEncoder;

    public MemberController(MemberService memberService, PasswordEncoder passwordEncoder) {
        this.memberService = memberService;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping
    @Operation(summary = "建立會員資料", description = "建立員工基本資料，密碼會以 BCrypt 加鹽雜湊後儲存；僅限 ADMIN 角色呼叫")
    public ApiResponse<MemberResponse> createMember(@Valid @RequestBody MemberCreateRequest request) {
        return ApiResponse.ok(memberService.createMember(request));
    }

    @GetMapping("/test-password-encode")
    @Operation(
            summary = "[測試用] BCrypt 密碼加密",
            description = "僅供 Swagger 手動測試使用，輸入明文密碼回傳對應的 BCrypt 加鹽雜湊值，方便準備測試資料（例如 fakeData.sql），正式環境請勿依賴此 API"
    )
    public ApiResponse<PasswordEncodeResponse> testPasswordEncode(
            @RequestParam @NotBlank(message = "密碼為必填") String rawPassword) {
        String encoded = passwordEncoder.encode(rawPassword);
        return ApiResponse.ok(new PasswordEncodeResponse(rawPassword, encoded));
    }
}
