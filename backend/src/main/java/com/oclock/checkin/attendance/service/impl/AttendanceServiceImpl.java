package com.oclock.checkin.attendance.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.oclock.checkin.attendance.dto.ClockRecordResponse;
import com.oclock.checkin.attendance.entity.TimeRecord;
import com.oclock.checkin.attendance.mapper.TimeRecordMapper;
import com.oclock.checkin.attendance.service.AttendanceService;
import com.oclock.checkin.common.BusinessException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class AttendanceServiceImpl implements AttendanceService {

    private static final String STATUS_NORMAL = "NORMAL";
    private static final String STATUS_LATE = "LATE";
    private static final String STATUS_EARLY_LEAVE = "EARLY_LEAVE";

    private static final LocalTime LATE_THRESHOLD = LocalTime.of(9, 0);
    private static final Duration MIN_WORK_DURATION = Duration.ofHours(9);

    private final TimeRecordMapper timeRecordMapper;

    public AttendanceServiceImpl(TimeRecordMapper timeRecordMapper) {
        this.timeRecordMapper = timeRecordMapper;
    }

    @Override
    public ClockRecordResponse clockIn(Long memberId) {
        LocalDate today = LocalDate.now();
        TimeRecord existing = findByMemberAndDate(memberId, today);
        if (existing != null) {
            throw new BusinessException("今日已完成上班打卡");
        }

        LocalDateTime now = LocalDateTime.now();
        TimeRecord record = new TimeRecord();
        record.setMemberId(memberId);
        record.setWorkDate(today);
        record.setClockInTime(now);
        record.setStatus(now.toLocalTime().isAfter(LATE_THRESHOLD) ? STATUS_LATE : STATUS_NORMAL);
        record.setCreatedAt(now);
        record.setUpdatedAt(now);

        timeRecordMapper.insert(record);
        return toResponse(record);
    }

    @Override
    public ClockRecordResponse clockOut(Long memberId, String remark) {
        LocalDate today = LocalDate.now();
        TimeRecord existing = findByMemberAndDate(memberId, today);
        if (existing == null) {
            throw new BusinessException("尚未完成上班打卡，無法下班打卡");
        }
        if (existing.getClockOutTime() != null) {
            throw new BusinessException("今日已完成下班打卡");
        }

        LocalDateTime now = LocalDateTime.now();
        boolean isEarlyLeave = Duration.between(existing.getClockInTime(), now).compareTo(MIN_WORK_DURATION) < 0;

        if (isEarlyLeave) {
            if (remark == null || remark.isBlank()) {
                throw new BusinessException("需要輸入提早下班理由，不能空白");
            }
            existing.setStatus(STATUS_EARLY_LEAVE);
            existing.setRemark(remark);
        } else if (remark != null && !remark.isBlank()) {
            existing.setRemark(remark);
        }

        existing.setClockOutTime(now);
        existing.setUpdatedAt(now);

        timeRecordMapper.updateById(existing);
        return toResponse(existing);
    }

    @Override
    public ClockRecordResponse getTodayRecord(Long memberId) {
        TimeRecord record = findByMemberAndDate(memberId, LocalDate.now());
        return record == null ? null : toResponse(record);
    }

    @Override
    public List<ClockRecordResponse> listRecords(Long memberId, LocalDate startDate, LocalDate endDate) {
        return timeRecordMapper.selectByMemberAndDateRange(memberId, startDate, endDate)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private TimeRecord findByMemberAndDate(Long memberId, LocalDate date) {
        return timeRecordMapper.selectOne(new LambdaQueryWrapper<TimeRecord>()
                .eq(TimeRecord::getMemberId, memberId)
                .eq(TimeRecord::getWorkDate, date));
    }

    private ClockRecordResponse toResponse(TimeRecord record) {
        return new ClockRecordResponse(
                record.getId(),
                record.getWorkDate(),
                record.getClockInTime(),
                record.getClockOutTime(),
                record.getStatus(),
                record.getRemark());
    }
}
