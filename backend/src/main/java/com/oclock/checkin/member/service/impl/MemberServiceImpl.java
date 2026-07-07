package com.oclock.checkin.member.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.oclock.checkin.common.BusinessException;
import com.oclock.checkin.member.dto.LoginRequest;
import com.oclock.checkin.member.dto.LoginResponse;
import com.oclock.checkin.member.dto.MemberCreateRequest;
import com.oclock.checkin.member.dto.MemberResponse;
import com.oclock.checkin.member.entity.Member;
import com.oclock.checkin.member.entity.MemberRole;
import com.oclock.checkin.member.mapper.MemberMapper;
import com.oclock.checkin.member.service.MemberService;
import com.oclock.checkin.security.JwtProperties;
import com.oclock.checkin.security.JwtTokenProvider;
import java.time.LocalDateTime;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class MemberServiceImpl implements MemberService {

    private static final String STATUS_ACTIVE = "ACTIVE";

    private final MemberMapper memberMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final JwtProperties jwtProperties;

    public MemberServiceImpl(MemberMapper memberMapper,
                              PasswordEncoder passwordEncoder,
                              JwtTokenProvider jwtTokenProvider,
                              JwtProperties jwtProperties) {
        this.memberMapper = memberMapper;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
        this.jwtProperties = jwtProperties;
    }

    @Override
    public MemberResponse createMember(MemberCreateRequest request) {
        Member existing = memberMapper.selectOne(
                new LambdaQueryWrapper<Member>().eq(Member::getEmail, request.getEmail()));
        if (existing != null) {
            throw new BusinessException("此 Email 已被註冊");
        }

        if (!request.getRole().equals(MemberRole.EMPLOYEE) && !request.getRole().equals(MemberRole.ADMIN) ){
            throw new BusinessException("MemberRole 值錯誤");
        }


        LocalDateTime now = LocalDateTime.now();
        Member member = new Member();
        member.setEmployeeNo(request.getEmployeeNo());
        member.setName(request.getName());
        member.setEmail(request.getEmail());
        member.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        member.setPhone(request.getPhone());
        member.setRole(request.getRole());
        member.setStatus(STATUS_ACTIVE);
        member.setCreatedAt(now);
        member.setUpdatedAt(now);

        memberMapper.insert(member);
        return toResponse(member);
    }

    @Override
    public LoginResponse login(LoginRequest request) {
        Member member = memberMapper.selectOne(
                new LambdaQueryWrapper<Member>().eq(Member::getEmail, request.getEmail()));

        if (member == null || !passwordEncoder.matches(request.getPassword(), member.getPasswordHash())) {
            throw new BusinessException("帳號或密碼錯誤", HttpStatus.UNAUTHORIZED);
        }
        if (!STATUS_ACTIVE.equals(member.getStatus())) {
            throw new BusinessException("帳號已停用，請聯繫管理員", HttpStatus.FORBIDDEN);
        }

        return issueLoginResponse(member);
    }

    @Override
    public LoginResponse refreshToken(Long memberId) {
        Member member = memberMapper.selectById(memberId);
        if (member == null) {
            throw new BusinessException("找不到使用者", HttpStatus.NOT_FOUND);
        }
        if (!STATUS_ACTIVE.equals(member.getStatus())) {
            throw new BusinessException("帳號已停用，請聯繫管理員", HttpStatus.FORBIDDEN);
        }

        return issueLoginResponse(member);
    }

    @Override
    public MemberResponse getCurrentMember(Long memberId) {
        Member member = memberMapper.selectById(memberId);
        if (member == null) {
            throw new BusinessException("找不到使用者", HttpStatus.NOT_FOUND);
        }
        return toResponse(member);
    }

    private LoginResponse issueLoginResponse(Member member) {
        String token = jwtTokenProvider.generateToken(
                member.getId(), member.getEmployeeNo(), member.getName(), member.getRole().name());
        return new LoginResponse(token, "Bearer", jwtProperties.getExpirationMinutes() * 60, toResponse(member));
    }

    private MemberResponse toResponse(Member member) {
        return new MemberResponse(
                member.getId(),
                member.getEmployeeNo(),
                member.getName(),
                member.getEmail(),
                member.getPhone(),
                member.getRole().name(),
                member.getStatus(),
                member.getCreatedAt());
    }
}
