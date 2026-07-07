package com.oclock.checkin.attendance.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Data;

@Data
@TableName("time_record")
public class TimeRecord {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long memberId;
    private LocalDate workDate;
    private LocalDateTime clockInTime;
    private LocalDateTime clockOutTime;
    private String status;
    private String remark;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
