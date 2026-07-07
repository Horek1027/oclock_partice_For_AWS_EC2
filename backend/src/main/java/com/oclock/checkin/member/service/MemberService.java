package com.oclock.checkin.member.service;

import com.oclock.checkin.member.dto.LoginRequest;
import com.oclock.checkin.member.dto.LoginResponse;
import com.oclock.checkin.member.dto.MemberCreateRequest;
import com.oclock.checkin.member.dto.MemberResponse;

public interface MemberService {

    MemberResponse createMember(MemberCreateRequest request);

    LoginResponse login(LoginRequest request);

    LoginResponse refreshToken(Long memberId);

    MemberResponse getCurrentMember(Long memberId);
}
