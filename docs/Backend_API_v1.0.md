# 고객 지원 포털 (SupportLink)


## 1. 개요 (Overview)
RESTful API를 제공하여 프론트엔드와 데이터를 교환하고, 고객 문의 데이터를 안전하게 저장 및 관리하는 API 서비스 

## 2. 시스템 아키텍처 및 기술 스택
* **API 스타일:** RESTful API (JSON 응답).
* **인증 방식:** JWT (JSON Web Token) 기반 인증 (Access Token + Refresh Token 권장).
* **데이터베이스:** RDBMS (MySQL 권장) - 관계형 데이터 모델링 필요.
* **언어/프레임워크:** Java (Spring Boot)

## 3 API 기능

### 3.1. 인증 (Authentication)
* `POST /api/auth/register`: 사용자 회원가입.
* `POST /api/auth/login`: 이메일/비밀번호 로그인, JWT 발급.
* `POST /api/auth/refresh`: 토큰 갱신 (선택 사항).
* `GET /api/auth/me`: 현재 로그인한 사용자 정보 조회.

### 3.2. FAQ / 지식 기반 (Knowledge Base)
* `GET /api/faqs`: FAQ 목록 조회 (검색어 `q`, 카테고리 `category` 필터링 지원).
* `GET /api/faqs/:id`: 특정 FAQ 상세 조회 (조회수 증가 로직 포함).
* `POST /api/faqs`: (관리자 전용) FAQ 생성.
* `PUT /api/faqs/:id`: (관리자 전용) FAQ 수정.
* `DELETE /api/faqs/:id`: (관리자 전용) FAQ 삭제.

### 3.3. 티켓 (Tickets)
* `POST /api/tickets`: 티켓 생성 (제목, 카테고리, 내용).
* `GET /api/tickets`: 티켓 목록 조회.
    * **고객:** 본인이 작성한 티켓만 조회.
    * **상담원:** 모든 티켓 조회 (상태 `status`, 담당자 `assignee` 필터링 지원).
* `GET /api/tickets/:id`: 티켓 상세 정보 조회.
* `PATCH /api/tickets/:id`: 티켓 상태/정보 수정 (상담원 전용 - 상태, 중요도, 담당자 변경).

### 3.4. 티켓 답변 (Ticket Replies)
* `GET /api/tickets/:id/replies`: 특정 티켓의 답변 목록 조회.
* `POST /api/tickets/:id/replies`: 답변 작성.
    * **고객:** 본인 티켓에만 작성 가능.
    * **상담원:** 모든 티켓에 작성 가능. 작성 시 티켓 상태 자동 변경 로직 고려 (예: 'New' -> 'Open').


## 4. 데이터베이스 스키마 (Database Schema)

### 4.1. Users (고객)
| Column | Type | Description |
| :--- | :--- | :--- |
| `user_id` | INT (PK) | 고객 고유 ID |
| `email` | VARCHAR | 이메일 (Unique) |
| `name` | VARCHAR | 이름 |
| `password_hash` | VARCHAR | 암호화된 비밀번호 |
| `created_at` | DATETIME | 가입일 |

### 4.2. Agents (상담원)
| Column | Type | Description |
| :--- | :--- | :--- |
| `agent_id` | INT (PK) | 상담원 고유 ID |
| `email` | VARCHAR | 이메일 (Unique) |
| `name` | VARCHAR | 이름 |
| `password_hash` | VARCHAR | 암호화된 비밀번호 |
| `role` | VARCHAR | 역할 ('Agent', 'Admin') |

### 4.3. KnowledgeBase (FAQ)
| Column | Type | Description |
| :--- | :--- | :--- |
| `article_id` | INT (PK) | 게시글 ID |
| `category` | VARCHAR | 카테고리 |
| `title` | VARCHAR | 제목 |
| `content` | TEXT | 본문 |
| `author_id` | INT (FK) | 작성자(Agent) ID |
| `view_count` | INT | 조회수 |

### 4.4. Tickets (티켓)
| Column | Type | Description |
| :--- | :--- | :--- |
| `ticket_id` | INT (PK) | 티켓 ID |
| `user_id` | INT (FK) | 작성자(User) ID |
| `assigned_agent_id` | INT (FK) | 담당 상담원 ID (Nullable) |
| `subject` | VARCHAR | 제목 |
| `status` | VARCHAR | 상태 ('New', 'Open', 'Pending', 'Closed') |
| `priority` | VARCHAR | 중요도 ('Low', 'Medium', 'High') |
| `created_at` | DATETIME | 생성일 |
| `updated_at` | DATETIME | 수정일 |

### 4.5. TicketReplies (답변)
| Column | Type | Description |
| :--- | :--- | :--- |
| `reply_id` | INT (PK) | 답변 ID |
| `ticket_id` | INT (FK) | 티켓 ID |
| `author_id` | INT | 작성자 ID (User or Agent) |
| `author_type` | VARCHAR | 작성자 타입 ('User', 'Agent') |
| `message` | TEXT | 내용 |
| `created_at` | DATETIME | 작성일 |

## 5. 데이터 초기화 (Data Initialization)

### 5.1. 샘플 데이터 로딩
* 시스템은 시작 시(또는 별도의 관리자 API/명령어를 통해) `databases` 폴더 내의 CSV 파일로부터 샘플 데이터를 로드할 수 있어야 합니다.
* **대상 파일:**
    * `databases/Users.csv` -> `Users` 테이블
    * `databases/Agents.csv` -> `Agents` 테이블
    * `databases/KnowledgeBase.csv` -> `KnowledgeBase` 테이블
    * `databases/Tickets.csv` -> `Tickets` 테이블
    * `databases/TicketReplies.csv` -> `TicketReplies` 테이블

* **로딩 규칙:**
    * 데이터베이스 테이블이 비어있는 경우에만 로딩을 수행합니다 (중복 적재 방지).
    * 외래 키(FK) 의존성을 고려하여 순서대로 로딩해야 합니다 (예: Users/Agents -> Tickets -> TicketReplies).
    * CSV 파일의 `password_hash` 컬럼은 이미 해싱된 값이 들어있다고 가정하거나, 로딩 시 해싱 처리를 수행해야 합니다 (현재 샘플 데이터는 해싱된 값 포함).