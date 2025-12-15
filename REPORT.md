# Spring Boot 포트폴리오/커뮤니티 서비스 보고서

## 1. 개요
- 스프링 부트 기반 개인 포트폴리오/커뮤니티 웹앱.
- JPA로 회원/포트폴리오/피드백/기술 스택/피드백 테마를 DB 연동.
- 회원가입, 로그인, 사용자 설정(비번/프로필/계정삭제), 포트폴리오 CRUD(관리자), 피드백 작성(로그인 사용자) 제공.

## 2. 기술 스택
- Spring Boot 3.5.x, Spring Security, Spring Data JPA, Thymeleaf, WebSocket(채팅), H2/MySQL 선택형.
- Lombok, HikariCP, Gradle 빌드.

## 3. 핵심 도메인 (JPA)
- `UserAccount`: id, username, password(BCrypt), role, profilePictureUrl, description.
- `Portfolio`: id, title, description, imageUrl, projectLink, extraUrl, techStack(콤마 구분), randomColor, visible, owner.
- `Feedback`: id, content, category(테마명), rating(1~5), createdAt, author, portfolio.
- `TechStack`: id, name.
- `FeedbackTheme`: id, name.

## 4. 주요 기능 흐름
- 인증/권한: DaoAuthenticationProvider+UserDetailsService(JPA).  
  - ROLE_USER: 포트폴리오 목록/상세, 피드백 작성.  
  - ROLE_ADMIN: 포트폴리오 생성/수정/삭제/가시성 토글, 기술 스택/테마 관리.
- 회원가입/로그인: `/register`, `/login`, BCrypt 해시 저장.
- 사용자 설정: `/settings`에서 비번 변경, 프로필 이미지(URL/업로드), 소개 수정, 계정 삭제.
- 포트폴리오: 공개 목록/상세, 관리자만 CRUD/가시성 토글. 이미지 URL 또는 업로드 `/uploads/**`.
- 피드백: 테마 선택 후 작성, 별점/내용/상대시간 표기. 관리자/사용자 모두 열람.
- 필터링: 기술 다중 선택+키워드 검색으로 포트폴리오 필터.
- 관리자 사이드바: 모든 페이지에 공통 프래그먼트로 삽입(관리자 로그인 시만 노출).

## 5. DB 설계 메모
- 추천 테이블: users, portfolios, feedback, tech_stack, feedback_theme.  
- 주요 연관: Portfolio - UserAccount(ManyToOne), Feedback - Portfolio(ManyToOne), Feedback - UserAccount(ManyToOne).  
- 색상/이미지 null 시 대체 색상 생성.

## 6. 페이지/화면
- 홈(`/home`): 포트폴리오 카드 그리드, 필터(다중 기술+키워드), 관리자 액션.
- 상세(`/portfolios/{id}`): 좌측 상세, 우측 피드백 리스트/폼(하단 고정).
- 폼(`/portfolios/new|edit`): 기술 다중 선택, 이미지 업로드/URL, 공개 여부(관리자).
- 회원가입/로그인: 공통 헤더/스타일 적용, 이미지 업로드 옵션.
- 설정(`/settings`): 비번/프로필/소개/계정 삭제.
- 관리자: 기술 스택 관리(`/admin/tech`), 피드백 테마 관리(`/admin/themes`), 사이드바 프래그먼트.

## 7. 실행 방법
1) DB 설정(`src/main/resources/application.properties`)  
   - MySQL 예시: `jdbc:mysql://localhost:3306/myapp_db?useSSL=false&allowPublicKeyRetrieval=true&sslMode=DISABLED&characterEncoding=UTF-8&serverTimezone=Asia/Seoul`  
   - 사용자/비밀번호 환경에 맞게 수정.
2) 빌드/테스트: `./gradlew test`  
3) 실행: `./gradlew bootRun` → `http://localhost:8080/login`

## 8. 제출 시 포함할 것 (별도 작성)
- 표지, 주제, 시스템 설계도, DB ERD, 시스템 흐름도, 대표 실행 화면 캡처(사용자 작성 예정).

## 9. 개발 과정 요약
- 요구사항 정의: JPA 기반 회원/포트폴리오/피드백/필터링/업로드/관리자 기능 명세.
- 설계: 도메인 모델링(User, Portfolio, Feedback, TechStack, FeedbackTheme), 권한/URL 매핑, 템플릿 구조(공통 헤더/사이드바 프래그먼트).
- 구현: 
  - 인증/회원: 회원가입·로그인·설정(비번/프로필/계정삭제) 및 DaoAuthenticationProvider 적용.
  - 포트폴리오: CRUD(관리자), 공개/비공개 토글, 이미지 URL·업로드, 기술 다중 선택/필터, 키워드 검색.
  - 피드백: 테마 선택(관리자 관리), 별점/내용/상대시간 표기, 로그인 사용자 작성.
  - 관리: 기술 스택/피드백 테마 관리 페이지, 관리자 사이드바.
  - UI: 공통 헤더/사이드바 프래그먼트, 반응형 카드/필터, 상세 좌우 분리 레이아웃.
- 테스트/검증: `./gradlew test`, 수동 UI 확인.

## 10. 기존 프로젝트와의 변경점
- 인메모리 Goal → JPA 기반 Portfolio/Feedback/TechStack/FeedbackTheme/Users로 전환.
- 인증: InMemoryUserDetails → JPA 연동 UserAccount + DaoAuthenticationProvider.
- 뷰: Goal 카드/목록 → 포트폴리오 목록/상세/필터/피드백/업로드 UI. 공통 헤더·관리자 사이드바 분리.
- 관리 기능: 기술 스택·피드백 테마 관리 페이지 추가.
- 사용자 기능: 회원가입·로그인·설정(비번 변경, 프로필 업로드/URL, 소개, 계정 삭제), 피드백 작성.
- 데이터 시드 제거 후 DB 기반으로 운용(필요 시 초기 데이터는 DataInitializer로 추가 가능).
