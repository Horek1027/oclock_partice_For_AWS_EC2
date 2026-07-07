package com.oclock.checkin.attendance.controller;

import com.oclock.checkin.attendance.dto.ClockOutRequest;
import com.oclock.checkin.attendance.dto.ClockRecordResponse;
import com.oclock.checkin.attendance.service.AttendanceService;
import com.oclock.checkin.common.ApiResponse;
import com.oclock.checkin.security.CustomUserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.LocalDate;
import java.util.List;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/attendance")
@Tag(name = "Attendance", description = "打卡與出勤紀錄")
public class AttendanceController {

    private final AttendanceService attendanceService;

    public AttendanceController(AttendanceService attendanceService) {
        this.attendanceService = attendanceService;
    }

    @PostMapping("/clock-in")
    @Operation(summary = "上班打卡")
    public ApiResponse<ClockRecordResponse> clockIn(@AuthenticationPrincipal CustomUserPrincipal principal) {
        return ApiResponse.ok(attendanceService.clockIn(principal.memberId()));
    }

    @PostMapping("/clock-out")
    @Operation(
            summary = "下班打卡",
            description = "上班時間至今未滿 9 小時視為提早下班，需帶 remark 說明理由，否則會回傳錯誤"
    )
    public ApiResponse<ClockRecordResponse> clockOut(
            @AuthenticationPrincipal CustomUserPrincipal principal,
            @RequestBody(required = false) ClockOutRequest request) {
        String remark = request == null ? null : request.remark();
        return ApiResponse.ok(attendanceService.clockOut(principal.memberId(), remark));
    }

    @GetMapping("/records/today")
    @Operation(summary = "查詢今日打卡紀錄")
    public ApiResponse<ClockRecordResponse> today(@AuthenticationPrincipal CustomUserPrincipal principal) {
        return ApiResponse.ok(attendanceService.getTodayRecord(principal.memberId()));
    }

    @GetMapping("/records")
    @Operation(summary = "查詢出勤紀錄", description = "可用 startDate、endDate 篩選日期區間")
    public ApiResponse<List<ClockRecordResponse>> records(
            @AuthenticationPrincipal CustomUserPrincipal principal,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ApiResponse.ok(attendanceService.listRecords(principal.memberId(), startDate, endDate));
    }
}
