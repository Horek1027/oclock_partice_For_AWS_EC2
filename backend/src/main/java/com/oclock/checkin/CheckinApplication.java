package com.oclock.checkin;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan(basePackages = {
        "com.oclock.checkin.member.mapper",
        "com.oclock.checkin.attendance.mapper"
})
public class CheckinApplication {

    public static void main(String[] args) {
        SpringApplication.run(CheckinApplication.class, args);
    }
}
