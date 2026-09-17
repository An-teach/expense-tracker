這份介紹文案非常適合直接填入你 GitHub 的初始頁面（正如 image_717a44.png 畫面中提示的 "Add a README"），內容已整合你在「專案介紹學習 以用來甄試面試」檔案中準備的工程技術底蘊，能讓瀏覽者或面試教授一眼看懂系統架構與技術深度。

**全端消費行為分析與帳務管理系統 (Personal Expense Tracker)**

**專案摘要**
本專案為一套前後端分離的雲端帳務管理與財務視覺化分析系統。針對市面記帳應用操作繁瑣的痛點，以「操作直覺、分類彈性、數據清晰」為核心，獨立實作從底層資料庫到前端互動的完整架構，協助使用者即時掌握個人資金流向。

**核心技術棧**

* **前端架構**：React、JavaScript (ES6+)、Axios、Chart.js


* **後端架構**：Java (Spring Boot 3)、RESTful API 設計


* **資料庫與 ORM**：MySQL 8.0、Spring Data JPA (Hibernate)、HikariCP 連線池


* **系統資安**：Spring Security、JWT (JSON Web Token)、BCrypt 雜湊加密



**系統核心功能**

* **無狀態安全登入**：採用 BCrypt 進行密碼雜湊儲存，並由伺服器簽發具備 HMAC-SHA256 簽名的 JWT，透過自訂 JwtAuthenticationFilter 嚴格把關 API 端點與跨使用者資料隔離。


* **即時記帳與分類管理**：提供自訂日期收支紀錄與個人化標籤設定，介面即時結算當日總額並支援單筆明細增刪查改。


* **月度財務視覺化**：動態聚合特定月份的支出紀錄，自動計算並繪製類別佔比圓餅圖與金額排行榜，提供直觀的數據統計。



**工程實作亮點**

* **連線池效能調優**：主動關閉 Spring 預設的 `spring.jpa.open-in-view=false`，避免高流量下 Controller 渲染佔用導致 HikariCP 連線池資源枯竭。


* **事務邊界與延遲載入控制**：在 Service 業務層精準定義 `@Transactional` 事務邊界，徹底解決 Entity 關聯轉換 DTO 時因 `FetchType.LAZY` 引發的 `LazyInitializationException`，兼顧連線隨借隨還的效能與資料完整性。
