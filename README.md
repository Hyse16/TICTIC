# TICTIC

## 요구 환경

- Java 17
- Docker / Docker Compose

## 로컬 인프라 실행

```bash
cp .env.example .env
docker compose up -d
```

기본 포함 서비스:

- MySQL 8
- Redis 7
- Kafka
- Zookeeper
- Kafka UI

## 애플리케이션 실행 예정 명령

Gradle wrapper를 추가한 뒤:

```bash
./gradlew bootRun
```

로컬 프로필은 기본값이므로 별도 지정이 없으면 `application-local.yml`로 실행됩니다.

## 다음 단계

1. Gradle wrapper 추가
2. 도메인별 패키지와 공통 베이스 엔티티 구성
3. 인증/회원 Phase 진행
