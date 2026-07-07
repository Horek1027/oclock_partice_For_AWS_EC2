package com.oclock.checkin.member.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.ToString;

@Data
@TableName("member")
public class Member {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String employeeNo;
    private String name;
    private String email;
    @ToString.Exclude          // 排除在 toString 之外,避免進 log
    @JsonIgnore                // 若此 entity 可能被序列化回傳,避免出現在 API 回應
    private String passwordHash;
    private String phone;
    private MemberRole role;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
