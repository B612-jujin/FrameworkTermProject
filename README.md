# MyApp 개선 보고서

## 1. 개요
- 기존 인메모리 목표(Goal) 관리와 로그인 데이터를 JPA/H2 기반 포트폴리오·회원 관리로 전환했습니다.
- 포트폴리오 카드를 통해 사진, 프로젝트 링크, 기타 URL, 설명, 사용 기술을 입력·수정·삭제할 수 있습니다.
- 회원가입/로그인 흐름을 DB 연동으로 변경하고 기본 계정을 시드(seed)했습니다.

## 2. 주요 변경 사항
- **데이터 계층**: `spring-boot-starter-data-jpa`, H2 추가 및 `ddl-auto=update` 설정.  
  - 엔터티: `UserAccount`(회원), `Portfolio`(포트폴리오).  
  - 레포지토리: `UserAccountRepository`, `PortfolioRepository`.
- **서비스**:  
  - `UserAccountService`로 회원 등록/조회 및 `UserDetailsService` 구현.  
  - `PortfolioService`로 포트폴리오 CRUD 및 색상/이미지 기본값 처리.
- **시큐리티**: 인메모리 사용자 제거 → JPA 연동. `/register` 허용, H2 콘솔 허용, ADMIN만 포트폴리오 생성/수정/삭제.
- **컨트롤러/뷰**:  
  - `AuthController`(회원가입), `LoginController`(홈/로그인 라우팅), `PortfolioController`(포트폴리오 CRUD).  
  - `home.html`을 포트폴리오 카드 뷰로 변경, `portfolio-form.html` 신설, `login.html`/`register.html` 갱신.
- **시드 데이터**: `DataInitializer`로 기본 계정/포트폴리오 생성  
  - 관리자: `admin / admin123`  
  - 사용자: `user1 / pass123`

## 3. 실행 방법
1) MySQL 연결 (myapp_db)
   - 설정: `src/main/resources/application.properties`
   - URL: `jdbc:mysql://localhost:3306/myapp_db?useSSL=false&characterEncoding=UTF-8&serverTimezone=Asia/Seoul`
   - 사용자/비밀번호: 기본값 `root / (빈 값)` → 환경에 맞게 수정 필요
2) 서버 실행:  
   ```bash
   ./gradlew bootRun
   ```
3) 브라우저 접속: `http://localhost:8080/login`

## 4. 테스트
- 전체 테스트: `./gradlew test` (현재 성공)

## 5. 향후 작업 제안
- 실운영 DB(MySQL/PostgreSQL 등)로 전환하고 마이그레이션 도입(Flyway/Liquibase).  
- 포트폴리오 소유자 검증/권한 강화(비관리자 편집 제한).  
- 입력값 검증 및 파일 업로드(이미지 업로드) 기능 추가.  
- 다국어(i18n) 지원 및 UI 개선.  

## 6. 기존 프로젝트 대비 변경점 정리
- **데이터/인증 구조**
  - 기존: 인메모리 Goal 리스트 + 인메모리 사용자.
  - 변경: JPA/MySQL 기반 `UserAccount`, `Portfolio`, `Feedback` 엔터티 도입. DaoAuthenticationProvider로 JPA 연동 로그인.
- **권한 모델**
  - 기존: ADMIN만 Goal CRUD.
  - 변경:  
    - `ROLE_USER`: 공개 포트폴리오 목록/상세 조회, 피드백 작성 가능.  
    - `ROLE_ADMIN`: 포트폴리오 생성/수정/삭제, 가시성 토글, 피드백 열람 가능.
- **포트폴리오 기능**
  - 기존: Goal 카드 단순 목록.
  - 변경: 포트폴리오 카드 클릭 시 상세 페이지 이동(이미지/링크/기술/설명/피드백 표시), 공개/비공개 토글(ADMIN), 이미지 URL 또는 파일 업로드 지원.
- **피드백**
  - 기존: 없음.
  - 변경: `Feedback` 엔터티/서비스로 프로젝트 평가, 면접 태도 등 피드백 작성/열람.
- **회원가입/프로필**
  - 기존: URL만 입력.
  - 변경: 프로필 이미지 URL 또는 파일 업로드 선택 가능.
- **정적 자원**
  - `/uploads/**` 리소스 핸들러 추가 → 업로드 파일을 정적으로 제공.
- **시큐리티 설정**
  - 공개: `/`, `/home`, `/login`, `/register`, `/portfolios`(목록/상세), 정적 리소스.  
  - 보호: 포트폴리오 생성/수정/삭제/가시성 토글(ADMIN), 피드백 작성(로그인 사용자).
- **페이지 구성**
  - Home: 공개 포트폴리오 목록, ADMIN만 CRUD 버튼 노출.  
  - Portfolio Form: 파일 업로드/노출 체크박스(ADMIN).  
  - Portfolio Detail: 공개 여부 표시, ADMIN 가시성 토글, 피드백 목록/작성 폼.  
  - Register: 프로필 파일 업로드 추가.  

## 7. 추가 확인/운영 메모
- 기존 DB 레코드에 `visible` 값이 null이면 true로 업데이트 후 사용 권장.
- 업로드 파일은 로컬 `uploads/`에 저장되며 `/uploads/**`로 접근.
