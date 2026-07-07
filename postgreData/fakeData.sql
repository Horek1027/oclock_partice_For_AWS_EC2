-- =========================================================
-- 假資料（測試用）
-- 前提：schema.sql 已在乾淨的資料庫執行過，member.id 由 1 開始遞增。
-- time_record.member_id 為邏輯關聯（無 FK），需對應下方 member 實際產生的 id。
-- =========================================================

-- ---------------------------------------------------------
-- member：3 筆假資料（id 依序為 1, 2, 3）
-- password_hash 是abc123456 的Brcyut 的雜湊
-- ---------------------------------------------------------
INSERT INTO member (employee_no, name, email, password_hash, phone, role, status)
VALUES
    ('E001', '王小明', 'wang@example.com', '$2a$10$L5707jmi4TDI/.QuVKMqW.5ikwhKOnzhGmlxtYm16zE6QNncLsycm', '0912345678', 'ADMIN',    'ACTIVE'),
    ('E002', '林小華', 'lin@example.com',  '$2a$10$L5707jmi4TDI/.QuVKMqW.5ikwhKOnzhGmlxtYm16zE6QNncLsycm', '0922334455', 'EMPLOYEE', 'ACTIVE'),
    ('E003', '陳小美', 'chen@example.com', '$2a$10$L5707jmi4TDI/.QuVKMqW.5ikwhKOnzhGmlxtYm16zE6QNncLsycm', '0933445566', 'EMPLOYEE', 'INACTIVE');

-- ---------------------------------------------------------
-- time_record：5 筆假資料
-- member_id 對應上面 member 的 id（1=王小明, 2=林小華, 3=陳小美）
-- 涵蓋：正常、遲到、缺席（無打卡）、早退、只打上班卡未打下班卡 等情境
-- ---------------------------------------------------------
INSERT INTO time_record (member_id, work_date, clock_in_time, clock_out_time, status, remark)
VALUES
    (1, '2026-07-01', '2026-07-01 08:58:00', '2026-07-01 18:02:00', 'NORMAL',      NULL),
    (2, '2026-07-01', '2026-07-01 09:15:00', '2026-07-01 18:00:00', 'LATE',        '晚到 15 分鐘'),
    (3, '2026-07-01', NULL,                  NULL,                  'ABSENT',      '請假'),
    (1, '2026-07-02', '2026-07-02 08:55:00', '2026-07-02 17:30:00', 'EARLY_LEAVE', '提前離開'),
    (2, '2026-07-02', '2026-07-02 09:00:00', NULL,                  'NORMAL',      '尚未打下班卡');
