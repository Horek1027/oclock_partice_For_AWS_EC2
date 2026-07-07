-- =========================================================
-- 打卡系統資料庫 Schema
-- 規則：不使用 FK，表格間關聯僅以欄位命名 (xxx_id) 表示邏輯關聯，
--       資料一致性由後端 Service 層負責檢查。
-- =========================================================

-- ---------------------------------------------------------
-- member：員工基本資料
-- ---------------------------------------------------------
CREATE TABLE member (
    id              BIGSERIAL PRIMARY KEY,
    employee_no     VARCHAR(20)  NOT NULL UNIQUE,        -- 員工編號
    name            VARCHAR(50)  NOT NULL,                -- 姓名
    email           VARCHAR(100) NOT NULL UNIQUE,         -- 登入帳號 / Email
    password_hash   VARCHAR(100) NOT NULL,                -- BCrypt 加鹽雜湊後的密碼
    phone           VARCHAR(20),                          -- 聯絡電話
    role            VARCHAR(20)  NOT NULL DEFAULT 'EMPLOYEE', -- EMPLOYEE / ADMIN
    status          VARCHAR(20)  NOT NULL DEFAULT 'ACTIVE',   -- ACTIVE / INACTIVE（離職停用）
    created_at      TIMESTAMP    NOT NULL DEFAULT now(),
    updated_at      TIMESTAMP    NOT NULL DEFAULT now()
);

COMMENT ON TABLE  member IS '員工基本資料表';
COMMENT ON COLUMN member.employee_no IS '員工編號，唯一';
COMMENT ON COLUMN member.role IS '角色：EMPLOYEE 一般員工 / ADMIN 管理員';
COMMENT ON COLUMN member.status IS '帳號狀態：ACTIVE 在職 / INACTIVE 離職或停用';

-- ---------------------------------------------------------
-- time_record：打卡出勤紀錄
-- 與 member 的關聯僅透過 member_id 欄位表示，不建立 FK 約束
-- ---------------------------------------------------------
CREATE TABLE time_record (
    id              BIGSERIAL PRIMARY KEY,
    member_id       BIGINT      NOT NULL,                 -- 邏輯關聯 member.id（無 FK）
    work_date       DATE        NOT NULL,                 -- 出勤日期
    clock_in_time   TIMESTAMP,                            -- 上班打卡時間
    clock_out_time  TIMESTAMP,                            -- 下班打卡時間
    status          VARCHAR(20) NOT NULL DEFAULT 'NORMAL', -- NORMAL / LATE / EARLY_LEAVE / ABSENT
    remark          VARCHAR(255),                          -- 備註
    created_at      TIMESTAMP   NOT NULL DEFAULT now(),
    updated_at      TIMESTAMP   NOT NULL DEFAULT now(),

    CONSTRAINT uq_time_record_member_date UNIQUE (member_id, work_date)
);

COMMENT ON TABLE  time_record IS '打卡出勤紀錄表';
COMMENT ON COLUMN time_record.member_id IS '邏輯關聯 member.id，不建立 FK 約束，一致性由後端 Service 層檢查';
COMMENT ON COLUMN time_record.status IS '出勤狀態：NORMAL 正常 / LATE 遲到 / EARLY_LEAVE 早退 / ABSENT 缺席';

-- 邏輯關聯欄位仍建立一般索引，維持查詢效能
CREATE INDEX idx_time_record_member_id ON time_record (member_id);
CREATE INDEX idx_time_record_work_date ON time_record (work_date);
