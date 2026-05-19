# MetaLearning — KDT 국비지원 교육 관리 플랫폼

Spring Boot 기반의 KDT(국비지원 디지털 훈련) 교육 관리 팀 프로젝트입니다.  
수강생·강사·매니저·관리자 역할을 구분하고, 수강 신청부터 출석·테스트·수료까지 전 과정을 지원합니다.

---

## 목차

1. [프로젝트 소개](#프로젝트-소개)
2. [주요 기능](#주요-기능)
3. [기술 스택](#기술-스택)
4. [아키텍처](#아키텍처)
5. [실행 방법](#실행-방법)
6. [EC2 배포](#ec2-배포)
7. [환경 변수](#환경-변수)

---

## 프로젝트 소개

| 항목 | 내용 |
|------|------|
| 프로젝트 유형 | 팀 프로젝트 5인 (포트폴리오) |
| 개발 기간 | 2025.02 ~ 2025.03 |
| 서버 포트 | 9091 |
| 데이터베이스 | MySQL 8.0 |
| 배포 주소 | https://metalearning.wonjae.cloud |

---

## 주요 기능

### 회원
- 회원가입 / 로그인 / 로그아웃 (Spring Security, 역할별 권한 분리)
- 이메일 인증 기반 회원가입 및 비밀번호 초기화
- 프로필 이미지 업로드 및 수정

### 수강생
- KDT 강좌 목록 및 상세 페이지
- 일반 온라인 강의 구매 (장바구니 → 결제 → 환불)
- 수강 영상 시청 (S3 영상 스트리밍)
- 마이페이지 (수강 내역, 구매 내역)

### 강사
- 강사 프로필 등록 및 승인 요청
- 강좌 커리큘럼 및 영상 등록 / 수정 / 삭제
- 강의 진행 현황 및 대시보드

### 매니저
- 수강생 출석 관리 및 출석 로그 조회
- KDT 상담 목록 관리
- 강좌 자료 및 게시판 관리

### 관리자
- 회원 목록 조회 / 역할 관리
- KDT 과정 / 세션 / 커리큘럼 등록 및 수정
- 테스트(퀴즈) 출제 및 채점
- IP 기반 출석 접근 제어
- 통계 대시보드 (월별·역할별·연령별·성별 분석)
- 강좌 마켓플레이스 관리 (강의 등록, 결제 내역 조회)

### 커뮤니티
- KDT 게시판 작성 / 수정 / 삭제 (파일 첨부, 페이지네이션)
- 학습 자료 공유

---

## 기술 스택

| 구분 | 기술 |
|------|------|
| Language | Java 17 |
| Framework | Spring Boot 3.x |
| ORM | Spring Data JPA, MyBatis |
| View | Thymeleaf |
| Security | Spring Security 6 |
| Database | MySQL 8.0 |
| Storage | AWS S3 (동영상 업로드) |
| Build | Gradle (bootWar) |
| Mail | Spring Mail (Gmail SMTP) |
| Container | Docker, Docker Compose |

---

## 아키텍처

```
EC2 (t3.small, 서울 리전)
├── Nginx (80/443) — Let's Encrypt HTTPS
│   ├── 9090 → P2 KosLearn
│   └── 9091 → P3 MetaLearning  ← 이 프로젝트
├── Docker
│   ├── p2-koslearn (포트 9090)
│   └── p3-metalearning (포트 9091)  ← 이 프로젝트
└── MySQL 8.0 (P2/P3 공유, 스키마 분리)
```

---

## 실행 방법

### 사전 요구사항

- Docker Desktop
- `.env` 파일 (직접 작성 필요, git 미포함)

### 환경 변수 파일 준비

`.env` 파일을 프로젝트 루트에 생성합니다:

```env
DB_NAME=metalearning
DB_USERNAME=root
DB_PASSWORD=your_db_password
AWS_ACCESS_KEY=your_aws_access_key
AWS_SECRET_KEY=your_aws_secret_key
AWS_S3_BUCKET=your_s3_bucket_name
MAIL_USERNAME=your_email@gmail.com
MAIL_PASSWORD=your_gmail_app_password
```

### Docker 실행 (EC2 기준)

```bash
# 1. 레포 클론
git clone https://github.com/leewj12/metalearning.git
cd metalearning

# 2. .env 파일 작성
vi .env

# 3. WAR 빌드 후 EC2에 배포 (로컬에서 빌드)
./gradlew bootWar
scp build/libs/*.war ubuntu@<EC2_IP>:~/metalearning/app.war

# 4. P2 MySQL에 metalearning 스키마 추가 (최초 1회)
docker exec -it koslearn-mysql-1 mysql -u root -p -e \
  "CREATE DATABASE metalearning CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"

# 5. 실행
docker compose up -d
```

> **중요**: SCP 시 파일명을 반드시 `app.war`로 지정해야 합니다 (`Dockerfile`이 `COPY app.war`를 기대합니다).

### 재배포

```bash
# 로컬에서 빌드 후 EC2로 전송
./gradlew bootWar
scp build/libs/*.war ubuntu@<EC2_IP>:~/metalearning/app.war

# EC2에서 컨테이너 재시작
docker compose up -d --force-recreate
```

---

## EC2 배포

- **서버**: AWS EC2 t3.small (서울 리전)
- **도메인**: [metalearning.wonjae.cloud](https://metalearning.wonjae.cloud)
- **Elastic IP**: 3.37.108.102
- **SSL**: Let's Encrypt (Certbot)
- **리버스 프록시**: Nginx (443 → 9091)
- **실행 방식**: Docker Compose (P2 MySQL 공유)
- **파일 스토리지**: 프로필/이미지 → Docker named volume, 강의 동영상 → AWS S3

### 업로드 파일 경로 (Docker 볼륨)

| 볼륨명 | 컨테이너 내 경로 | 용도 |
|--------|-----------------|------|
| `profile_images` | `/app/tomcat/.../KDT/profileimages` | KDT 프로필 이미지 |
| `course_images` | `/app/tomcat/.../static/images` | 강의 이미지 |
| `course_uploads` | `/app/tomcat/.../static/uploads` | 강의 첨부파일 |

### Nginx 설정 예시

```nginx
server {
    listen 443 ssl;
    server_name metalearning.wonjae.cloud;

    ssl_certificate     /etc/letsencrypt/live/metalearning.wonjae.cloud/fullchain.pem;
    ssl_certificate_key /etc/letsencrypt/live/metalearning.wonjae.cloud/privkey.pem;

    client_max_body_size 500M;

    location / {
        proxy_pass         http://127.0.0.1:9091;
        proxy_set_header   Host              $host;
        proxy_set_header   X-Real-IP         $remote_addr;
        proxy_set_header   X-Forwarded-For   $proxy_add_x_forwarded_for;
        proxy_set_header   X-Forwarded-Proto $scheme;
    }
}
```

---

## 환경 변수

`.env` 파일에 작성 (git 미포함).

| 변수명 | 설명 |
|--------|------|
| `DB_NAME` | MySQL 데이터베이스 이름 |
| `DB_USERNAME` | DB 사용자명 |
| `DB_PASSWORD` | DB 비밀번호 |
| `AWS_ACCESS_KEY` | AWS IAM 액세스 키 |
| `AWS_SECRET_KEY` | AWS IAM 시크릿 키 |
| `AWS_S3_BUCKET` | S3 버킷 이름 (동영상 저장) |
| `MAIL_USERNAME` | 발신 이메일 주소 |
| `MAIL_PASSWORD` | Gmail 앱 비밀번호 |
