# CLAUDE.md（資料庫 / postgreData）

本檔案為資料庫（PostgreSQL）相關的開發規則，編輯 `postgreData/` 內容（schema、migration SQL）時適用。

## 命名與基本規則

- 資料表命名使用小寫底線命名法（snake_case），例如 `time_record`。
- 每張表需有主鍵（統一使用 `BIGSERIAL` 自增），時間欄位（建立/更新時間）統一命名為 `created_at` / `updated_at`。
- Schema 變更（新增欄位、表格）需透過 migration 工具（如 Flyway）管理，不要直接手動改資料庫結構。

## 禁止使用外鍵（Foreign Key）

- **表格之間一律不建立 FK 約束**，避免表格間產生過多強耦合關聯，方便未來拆分服務或調整結構。
- 表格間的邏輯關聯改用一般欄位表示（例如 `time_record.member_id` 對應 `member.id`），欄位命名以 `xxx_id` 表示這是邏輯外鍵，但資料庫層不做約束檢查。
- 資料一致性（例如新增 `time_record` 前檢查 `member_id` 是否存在）改由後端 Service 層負責檢查，不依賴資料庫的 FK 約束。
- 為了維持查詢效能，邏輯關聯欄位（如 `member_id`）仍需建立一般索引（`CREATE INDEX`），只是不建立 `REFERENCES` 約束。

## 資料表

- `member`：員工基本資料表。
- `time_record`：打卡出勤紀錄表，透過 `member_id`（無 FK）邏輯關聯 `member`。

詳細欄位定義見同目錄下的 [schema.sql](schema.sql)。
