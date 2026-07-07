package com.oclock.checkin.attendance.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record ClockRecordResponse(
        Long id,
        LocalDate workDate,
        LocalDateTime clockInTime,
        LocalDateTime clockOutTime,
        String status,
        String remark
) {
}
