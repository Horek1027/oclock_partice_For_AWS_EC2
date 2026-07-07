# CLAUDE.md（後端 / backend）

本檔案為 `backend/` 目錄下的開發規則，僅在編輯後端程式碼時適用。跨領域的通用規則請參考根目錄 `CLAUDE.md`。

## 分層與注入規則

- 套件分層採 `controller` / `service` / `mapper` / `entity` (or `domain`) / `dto` 的方式劃分，職責單一：
  - Controller 只負責接收請求與回傳結果，不寫商業邏輯。
  - Service 放商業邏輯。
  - Mapper（MyBatis Plus）只負責資料存取。
- DTO 與 Entity 分開，Controller 對外一律回傳 DTO，不要直接暴露 Entity。
- 使用建構子注入（constructor injection），不要用 `@Autowired` 欄位注入。
- 例外處理統一用 `@ControllerAdvice` / `@ExceptionHandler` 集中管理，不要在各個 Controller 各自 try-catch 亂丟訊息。
- API 路徑使用 RESTful 風格，例如：
  - `POST /api/attendance/clock-in`
  - `POST /api/attendance/clock-out`
  - `GET /api/attendance/records`
- 設定值（DB 連線字串、密鑰等）一律放在 `application.yml` 並依環境（`application-dev.yml` / `application-prod.yml`）區分，機密資訊不得寫死在程式碼或提交進 git。

## DAO 層規則（MyBatis Plus）

- **兩個以內查詢參數的單表 CRUD**：直接使用 MyBatis Plus 內建語法（`BaseMapper`、`LambdaQueryWrapper`、`QueryWrapper` 等），不另外寫 XML。
- **超過兩個參數，或牽涉 join（多表關聯查詢）的操作**：一律寫成 MyBatis 的 XML 對應檔（`resources/mapper/*.xml`），搭配 Mapper interface 中宣告的方法，不要用 Wrapper 硬湊複雜查詢或在 Java 裡拼字串 SQL。
- XML 中的 SQL 需使用 `<if>` / `<where>` / `<foreach>` 等動態標籤處理條件式查詢，避免手動字串拼接造成 SQL Injection 風險。
- Mapper XML 檔名與對應的 Mapper interface 同名，並放在 `resources/mapper/` 目錄下，路徑結構對應 Java package。

## API 文件與測試（Swagger 3 / springdoc-openapi）

- 使用 springdoc-openapi 產生 Swagger 3 (OpenAPI 3) 文件，所有 Controller 的 API 皆須加上 `@Tag`、`@Operation`、`@ApiResponse` 等註解，說明用途與回傳格式。
- 新增或修改 API 後，先透過 Swagger UI 實際呼叫測試，確認參數與回應格式正確，再交付或提交。
- DTO 欄位視需要加上 `@Schema` 註解說明欄位意義，方便前端與測試人員從 Swagger 文件理解 API。

## 會員登入 / 認證規則（JWT + BCrypt）

- 密碼儲存一律使用 **BCrypt 加鹽雜湊**（`BCryptPasswordEncoder`），不得明文或使用 MD5/SHA 等單純雜湊儲存密碼；加鹽由 BCrypt 內建機制自動處理，不自行實作 salt 邏輯。
- 登入成功後簽發 **JWT**，有效期限固定為 **10 分鐘**，過期後需重新登入或走 refresh 流程（若需要 refresh token，需另行討論並記錄於此）。
- JWT 內容（payload）僅放必要資訊（如使用者 ID、角色），不得放密碼、身分證字號等敏感資料。
- 簽章金鑰（JWT secret）放在 `application.yml` 的環境變數中，不得寫死在程式碼或提交進 git。
- 需要登入才能存取的 API，透過 Spring Security filter 驗證 JWT，並統一在 `@ControllerAdvice` 回傳 401/403，不在各 Controller 各自判斷 token。
- 密碼相關欄位（如 `password`、`password_hash`）不得出現在任何回傳給前端的 DTO 中。
