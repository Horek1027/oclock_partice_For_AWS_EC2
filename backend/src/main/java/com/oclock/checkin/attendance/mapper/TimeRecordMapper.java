package com.oclock.checkin.attendance.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.oclock.checkin.attendance.entity.TimeRecord;
import java.time.LocalDate;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface TimeRecordMapper extends BaseMapper<TimeRecord> {

    /**
     * 3 個查詢參數（member_id + 日期區間），依專案規則須以 XML 撰寫，見 TimeRecordMapper.xml。
     */
    List<TimeRecord> selectByMemberAndDateRange(@Param("memberId") Long memberId,
                                                 @Param("startDate") LocalDate startDate,
                                                 @Param("endDate") LocalDate endDate);
}
