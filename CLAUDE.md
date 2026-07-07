# CLAUDE.md

本檔案為 Claude Code 在此專案中工作時應遵循的開發規則與慣例。

## 專案簡介

本專案是一套**打卡系統**（員工上下班打卡、出勤紀錄管理）。

## 技術棧

- **前端**：Vue 3（Composition API + `<script setup>`）
- **後端**：Java 17 + Spring Boot 3.5
- **DAO 層**：MyBatis Plus
- **API 文件/測試**：Swagger 3（springdoc-openapi）
- **資料庫**：PostgreSQL

## 專案結構（規劃）

```
/frontend     # Vue 3 前端專案
/backend      # Spring Boot 後端專案
/postgreData  # PostgreSQL schema / SQL 檔案
```

新增功能時，前後端程式碼分別放在對應目錄下，不要混雜在一起。

## 後端開發規則（Spring Boot / Java 17）

後端分層、DAO（MyBatis Plus）、Swagger API 文件、JWT + BCrypt 登入認證等詳細規則，請見 [backend/CLAUDE.md](backend/CLAUDE.md)（編輯 `backend/` 內程式碼時會自動載入）。

## 前端開發規則（Vue 3）

- 一律使用 `<script setup>` 搭配 Composition API，不使用 Options API。
- 元件命名使用 PascalCase（例如 `ClockInButton.vue`）。
- API 呼叫統一封裝在獨立的 service/api 模組，元件內不要直接寫 fetch/axios 呼叫細節。
- 狀態管理視需求使用 Pinia，避免用全域變數或事件總線傳遞跨元件狀態。
- 樣式與邏輯分離，避免行內樣式（inline style），優先使用 scoped CSS 或既有樣式方案。

## 資料庫規則（PostgreSQL）

命名規範、禁止使用 FK 等詳細規則與資料表設計（`member`、`time_record`），請見 [postgreData/CLAUDE.md](postgreData/CLAUDE.md) 與 [postgreData/schema.sql](postgreData/schema.sql)（編輯 `postgreData/` 內容時會自動載入）。

## 部署環境現況（EC2）

- EC2（Amazon Linux 2023）已安裝好 Node.js（透過 nvm）與 PostgreSQL 15。
- 資料庫使用者：`horek`，資料庫名稱：`myapp_db`。
- `pg_hba.conf` 已改為 `md5` 驗證方式。
- 後端程式尚未上傳到 EC2，計畫透過 Git 部署（`git clone`/`git pull`），非本機建置後 scp 上傳。

## 通用規則

- 修 bug 不順便重構、不做超出需求範圍的變動。
- 新增功能前，先確認是否已有可重用的元件/服務，避免重複造輪子。
- commit 訊息需清楚描述「為什麼」而非只是「做了什麼」。
- 涉及打卡時間、出勤紀錄等核心邏輯時，需特別注意時區處理（統一使用 UTC 儲存，顯示時再轉換為在地時區）。
