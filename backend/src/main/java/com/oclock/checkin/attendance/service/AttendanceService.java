package com.oclock.checkin.attendance.service;

import com.oclock.checkin.attendance.dto.ClockRecordResponse;
import java.time.LocalDate;
import java.util.List;

public interface AttendanceService {

    ClockRecordResponse clockIn(Long memberId);

    ClockRecordResponse clockOut(Long memberId, String remark);

    ClockRecordResponse getTodayRecord(Long memberId);

    List<ClockRecordResponse> listRecords(Long memberId, LocalDate startDate, LocalDate endDate);
}
