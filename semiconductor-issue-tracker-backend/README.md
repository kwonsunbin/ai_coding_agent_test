# Semiconductor Issue Tracker Backend

반도체 설비 이슈 트래커를 위한 Spring Boot 백엔드 API 서버입니다.

## 기술 스택

- **Java 17**
- **Spring Boot 3.2.0**
- **MyBatis 3.0.3**
- **H2 Database** (개발용)
- **MySQL** (운영용)
- **Maven**

## 프로젝트 구조

```
src/
├── main/
│   ├── java/com/semiconductor/issuetracker/
│   │   ├── IssueTrackerApplication.java     # 메인 애플리케이션 클래스
│   │   ├── config/
│   │   │   └── WebConfig.java               # CORS 및 웹 설정
│   │   ├── controller/
│   │   │   ├── ActionItemController.java    # Action Item REST API
│   │   │   └── InformController.java        # Inform REST API
│   │   ├── service/
│   │   │   ├── ActionItemService.java       # Action Item 비즈니스 로직
│   │   │   └── InformService.java           # Inform 비즈니스 로직
│   │   ├── mapper/
│   │   │   ├── ActionItemMapper.java        # Action Item MyBatis 매퍼
│   │   │   └── InformMapper.java            # Inform MyBatis 매퍼
│   │   └── model/
│   │       ├── ActionItem.java              # Action Item 엔티티
│   │       ├── Comment.java                 # Comment 엔티티
│   │       ├── CommentAttachment.java       # Comment 첨부파일 엔티티
│   │       ├── Inform.java                  # Inform 엔티티
│   │       └── InformAttachment.java        # Inform 첨부파일 엔티티
│   └── resources/
│       ├── application.yml                  # 애플리케이션 설정
│       ├── schema.sql                       # 데이터베이스 스키마
│       ├── data.sql                         # 샘플 데이터
│       └── mapper/
│           ├── ActionItemMapper.xml         # Action Item SQL 매핑
│           └── InformMapper.xml             # Inform SQL 매핑
```

## 데이터베이스 스키마

### 주요 테이블

1. **action_items** - 액션 아이템 관리
2. **comments** - 댓글 관리
3. **comment_attachments** - 댓글 첨부파일
4. **informs** - 인폼 관리
5. **inform_attachments** - 인폼 첨부파일
6. **pm_bm_history** - PM/BM 이력
7. **down_history** - Down 이력
8. **racb_history** - RACB 이력
9. **equipment** - 설비 정보

## 설치 및 실행

### 1. 사전 요구사항

- Java 17 이상
- Maven 3.6 이상

### 2. Maven 설치 (macOS)

```bash
# Homebrew를 사용한 Maven 설치
brew install maven

# 설치 확인
mvn --version
```

### 3. 프로젝트 빌드 및 실행

```bash
# 프로젝트 디렉토리로 이동
cd semiconductor-issue-tracker-backend

# 의존성 설치 및 컴파일
mvn clean compile

# 애플리케이션 실행
mvn spring-boot:run
```

### 4. 대안: IDE에서 실행

- IntelliJ IDEA 또는 Eclipse에서 `IssueTrackerApplication.java` 파일을 열고 실행

## API 엔드포인트

### Action Items API

- `GET /api/action-items` - 액션 아이템 목록 조회 (필터링, 페이징 지원)
- `GET /api/action-items/{id}` - 특정 액션 아이템 조회
- `POST /api/action-items` - 새 액션 아이템 생성
- `PUT /api/action-items/{id}` - 액션 아이템 수정
- `DELETE /api/action-items/{id}` - 액션 아이템 삭제
- `GET /api/action-items/statistics` - 액션 아이템 통계

### Informs API

- `GET /api/informs` - 인폼 목록 조회 (필터링, 페이징 지원)
- `GET /api/informs/{id}` - 특정 인폼 조회
- `POST /api/informs` - 새 인폼 생성
- `PUT /api/informs/{id}` - 인폼 수정
- `DELETE /api/informs/{id}` - 인폼 삭제
- `GET /api/informs/statistics` - 인폼 통계

## 설정

### 개발 환경 (기본)

- 포트: 8080
- 데이터베이스: H2 (인메모리)
- H2 콘솔: http://localhost:8080/api/h2-console

### 운영 환경

```bash
# MySQL 사용 시
mvn spring-boot:run -Dspring-boot.run.profiles=prod
```

환경변수 설정:
- `DB_USERNAME`: MySQL 사용자명
- `DB_PASSWORD`: MySQL 비밀번호

## 개발 가이드

### 새로운 엔티티 추가

1. `model/` 패키지에 엔티티 클래스 생성
2. `mapper/` 패키지에 MyBatis 매퍼 인터페이스 생성
3. `resources/mapper/` 에 XML 매퍼 파일 생성
4. `service/` 패키지에 서비스 클래스 생성
5. `controller/` 패키지에 컨트롤러 클래스 생성
6. `schema.sql`에 테이블 정의 추가

### 코드 스타일

- Lombok 사용으로 보일러플레이트 코드 최소화
- MyBatis를 통한 SQL 매핑
- RESTful API 설계 원칙 준수
- 적절한 HTTP 상태 코드 사용
- 예외 처리 및 로깅

## 문제 해결

### 포트 충돌

```bash
# 다른 포트로 실행
mvn spring-boot:run -Dspring-boot.run.jvmArguments="-Dserver.port=8081"
```

### 데이터베이스 연결 문제

1. H2 콘솔에서 데이터베이스 상태 확인
2. `application.yml` 설정 검토
3. 로그 파일 확인

## 라이센스

이 프로젝트는 MIT 라이센스 하에 배포됩니다.
