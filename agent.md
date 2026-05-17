# TICTIC Agent Guide

이 문서는 TICTIC 프로젝트에서 작업하는 개발자와 코딩 에이전트가 항상 먼저 참고해야 하는 로컬 개발 규약이다.
기능 추가, 리팩터링, 코드 생성, 네이밍, 패키지 설계, 테스트 작성 시 이 문서를 우선 기준으로 삼는다.

## 1. 기본 태도

- 코드는 "지금 동작하는 것"보다 "다음 기능을 버티는 구조"를 우선한다.
- 단순히 빠르게 붙이는 구현보다, 역할이 분리된 구조를 선호한다.
- 객체지향은 클래스 수를 늘리는 행위가 아니라 책임과 변경 이유를 분리하는 행위로 본다.
- 한 클래스는 가능한 한 하나의 핵심 책임만 가진다.
- 비즈니스 규칙은 컨트롤러가 아니라 도메인과 서비스 계층에 둔다.
- 공통화는 2번 반복된 뒤 검토하고, 3번 반복되기 전에 정리한다.

## 2. 프로젝트 방향

TICTIC은 성장형 서비스로 본다.
따라서 코드는 다음 특성을 가져야 한다.

- 기능이 늘어나도 패키지 경계가 무너지지 않아야 한다.
- LIVE 예매와 SIMULATION 예매가 공통 코어를 공유하되, 분기 책임은 명확해야 한다.
- 동시성, 대기열, 좌석 선점, 결제 전략처럼 확장 가능성이 높은 영역은 처음부터 역할을 분리한다.
- "임시 처리"는 가능하지만, 임시라는 사실이 코드에서 드러나야 한다.

## 3. 설계 원칙

### 3-1. 계층 원칙

- Controller: 요청/응답 변환, 인증 사용자 추출, 입력 검증 진입점만 담당한다.
- Service: 유스케이스 흐름과 트랜잭션 경계를 담당한다.
- Domain(Entity/VO): 상태와 규칙을 가진다. 의미 없는 getter/setter 묶음으로 두지 않는다.
- Repository: 영속성 접근만 담당한다.
- Infrastructure: Redis, Kafka, 외부 API, 결제사, SSE 같은 외부 시스템 연동을 담당한다.

### 3-2. 객체지향 원칙

- 상태를 가진 객체는 자신의 상태 변경 책임을 스스로 가진다.
- if/else 분기가 서비스 전반에 반복되면 Strategy, Policy, Resolver 분리를 우선 검토한다.
- 값의 조합이 의미를 가지면 primitive 나열 대신 Value Object를 검토한다.
- 외부 시스템 모델과 내부 도메인 모델을 바로 섞지 않는다.
- 엔티티 생성 시 의미 없는 빈 생성 후 setter 채우기보다 의도가 드러나는 생성 메서드나 생성자를 선호한다.

### 3-3. 확장 원칙

- LIVE / SIMULATION 분기는 enum + 전략 객체 조합을 우선 검토한다.
- 결제, 좌석 선점, 리포트 생성, 봇 실행 같은 영역은 인터페이스 뒤에 둔다.
- "나중에 다른 구현이 붙을 가능성"이 높은 영역만 추상화한다.
- 아직 하나뿐인 단순 로직은 과도하게 인터페이스로 쪼개지 않는다.

## 4. 패키지 규칙

기본 패키지 루트:

```text
com.hyse.tictic
```

권장 구조:

```text
com.hyse.tictic
├── global
│   ├── config
│   ├── error
│   ├── security
│   ├── common
│   └── infra
├── domain
│   ├── member
│   ├── event
│   ├── simulation
│   ├── queue
│   ├── seat
│   ├── order
│   ├── payment
│   └── report
└── admin
    ├── live
    └── simulation
```

세부 도메인 내부 권장 구조:

```text
domain/member
├── controller
├── service
├── domain
├── repository
├── dto
└── infrastructure
```

규칙:

- `global`에는 진짜 전역 관심사만 둔다.
- 도메인 간 참조는 최소화하고, 직접 참조보다 서비스 협력을 우선 검토한다.
- `util` 패키지는 남용하지 않는다. 이름이 `util`이어야만 하는 코드는 설계가 덜 끝난 경우가 많다.

## 5. 네이밍 규칙

TICTIC의 네이밍은 "트렌디하지만 가벼워 보이지 않고", "성장 가능성이 보이며", "역할이 바로 읽히는 이름"을 기준으로 한다.

### 5-1. 클래스명

- 클래스명은 명사 또는 명사구를 사용한다.
- 역할이 드러나야 한다.
- 축약어는 업계 표준만 허용한다.

좋은 예:

- `QueueEntry`
- `SeatReservationService`
- `SimulationRunner`
- `PaymentStrategy`
- `ReservationPolicy`
- `QueuePositionSnapshot`
- `SimulationResultReport`

피해야 할 예:

- `CommonService`
- `TicketUtil`
- `DataManager`
- `DoReserve`
- `SeatThing`

### 5-2. 인터페이스명

- 접두사 `I`는 사용하지 않는다.
- 역할 중심 명사를 사용한다.

예:

- `PaymentProcessor`
- `QueueScheduler`
- `SimulationBotLauncher`
- `ReservationLockManager`

구현체 예:

- `PortOnePaymentProcessor`
- `RedisQueueScheduler`
- `KafkaSimulationBotLauncher`
- `RedissonReservationLockManager`

### 5-3. 메서드명

- 메서드는 동사로 시작한다.
- 한 번 읽고 반환/행동이 예측되어야 한다.
- 조회와 명령을 섞지 않는다.

예:

- `createSimulation`
- `startSimulation`
- `reserveSeat`
- `cancelReservation`
- `publishQueueUpdate`
- `findActiveEvent`
- `calculateWaitingRank`

피해야 할 예:

- `doThing`
- `process`
- `handleData`
- `setInfo`

### 5-4. 변수명

- 컬렉션은 복수형을 사용한다.
- boolean은 질문형으로 읽히게 짓는다.
- 숫자/시간은 단위를 이름에 포함한다.

예:

- `queueEntries`
- `reservedSeats`
- `isOpen`
- `isSimulation`
- `waitSeconds`
- `maxTicketCount`
- `rampUpSeconds`

### 5-5. Enum명

- enum 타입은 명사, enum 값은 도메인 의미를 드러내는 상수형으로 작성한다.

예:

- `ServiceType { LIVE, SIMULATION }`
- `EventStatus { SCHEDULED, OPEN, CLOSED, CANCELLED }`
- `DifficultyPreset { EASY, NORMAL, HARD, HELL }`

## 6. 코드 생성 규칙

새 코드를 만들 때 아래 규칙을 기본값으로 삼는다.

### 6-1. Controller 규칙

- 컨트롤러는 얇게 유지한다.
- 요청 DTO와 응답 DTO를 분리한다.
- 엔티티를 직접 응답하지 않는다.
- URI는 리소스 중심으로 설계한다.

예:

- `POST /api/simulations`
- `POST /api/simulations/{simulationId}/start`
- `POST /api/queues/enter`

### 6-2. Service 규칙

- 서비스 메서드는 유스케이스 단위로 만든다.
- 트랜잭션은 서비스 레이어에서 시작한다.
- 서비스가 너무 커지면 "도메인 서비스", "정책", "전용 협력 객체"로 분리한다.

### 6-3. DTO 규칙

- 요청/응답 DTO 이름은 목적이 드러나게 짓는다.
- `Request`, `Response`, `Command`, `Result`, `Summary` 접미사를 일관되게 사용한다.

예:

- `CreateSimulationRequest`
- `SimulationDetailResponse`
- `ReserveSeatCommand`
- `QueueEntryResult`

### 6-4. Entity 규칙

- 엔티티는 비즈니스 의미 없는 public setter를 열어두지 않는다.
- 생성/상태변경 메서드로 의도를 드러낸다.
- 연관관계는 필요한 방향만 둔다.
- 컬렉션은 기본적으로 캡슐화한다.

### 6-5. Repository 규칙

- 단순 조회는 메서드명 기반으로 시작한다.
- 조건이 복잡해지면 QueryDSL 전용 리포지토리로 분리한다.
- Repository는 비즈니스 판단을 하지 않는다.

## 7. 객체지향을 지키기 위한 실전 규칙

- 서비스에 `if (serviceType == ...)`가 여러 번 반복되면 전략 분리를 검토한다.
- 좌석 상태 변경은 `Seat`가, 주문 상태 변경은 `Order`가 직접 책임지게 한다.
- 도메인 정책은 `Policy` 또는 `Rule` 객체로 분리할 수 있어야 한다.
- 시간 계산, 점수 계산, 대기 순위 계산처럼 순수 규칙은 테스트 가능한 작은 객체로 만든다.
- 외부 의존이 들어가는 순간 인터페이스 뒤에 두고, 도메인 로직과 분리한다.

## 8. 테스트 규칙

- 핵심 비즈니스 로직은 단위 테스트를 우선한다.
- 동시성, Redis, Kafka, DB가 걸리는 구간은 통합 테스트로 보강한다.
- 버그 수정 시 재현 테스트를 먼저 추가한다.
- 로컬 프로필은 가능한 한 외부 인프라 없이도 최소 부팅 가능해야 한다.

테스트 메서드명 예:

- `createSimulation_createsReadySimulation_whenRequestIsValid`
- `reserveSeat_fails_whenSeatAlreadyReserved`
- `startSimulation_publishesBotLaunchEvent`

## 9. 금지 규칙

- 의미 없는 `Util`, `Manager`, `Helper` 남발 금지
- 엔티티를 단순 DB row 취급하는 anemic model 지향 금지
- 컨트롤러에서 비즈니스 분기 처리 금지
- 하나의 서비스 클래스에 여러 도메인 책임 누적 금지
- "나중에 쓰일 것 같아서" 과한 추상화 추가 금지
- 도메인 의미 없는 축약 네이밍 금지

## 10. 추천 스타일

TICTIC은 아래 스타일을 권장한다.

- 읽었을 때 서비스가 성장할 준비가 된 느낌의 이름
- 서비스 흐름보다 도메인 개념이 먼저 보이는 코드
- 얇은 컨트롤러, 명확한 서비스, 책임 있는 엔티티
- 분기보다 정책 객체
- 재사용보다 응집도 우선
- 짧은 코드보다 의도가 보이는 코드

## 11. 커밋 규칙

TICTIC은 작업 단위를 작게 유지하고, 작업 하나가 끝날 때마다 커밋 후 푸시하는 것을 기본 원칙으로 한다.

- 한 작업 단위당 한 커밋을 우선한다.
- 의미 없는 중간 저장 커밋은 지양한다.
- 커밋 메시지는 항상 같은 형식을 유지한다.
- 기본 형식은 `type : 설명` 이다.
- `:` 앞뒤 공백도 동일하게 유지한다.

권장 타입:

- `feat :` 새로운 기능 개발
- `fix :` 버그 수정
- `refactor :` 동작 변화 없는 구조 개선
- `docs :` 문서 수정
- `test :` 테스트 추가 또는 보완
- `chore :` 빌드, 설정, 의존성, 환경 정리

예시:

- `feat : 메인페이지 개발`
- `fix : 로그인 버그 수정`
- `refactor : 예매 대기열 전략 분리`
- `docs : agent 규칙 문서 추가`

규칙:

- 기능 개발은 가능한 한 `feat : 기능 설명`으로 작성한다.
- 버그 수정은 반드시 `fix : 문제 설명` 형식을 사용한다.
- 커밋 메시지 첫 줄만 봐도 작업 목적이 이해되어야 한다.
- 여러 성격이 섞인 큰 작업은 쪼개서 각각 커밋한다.
- 커밋 후 바로 원격에 푸시하는 것을 기본 흐름으로 한다.

## 12. 작업 전 체크리스트

새 코드를 만들기 전에 항상 확인한다.

- 이 로직의 책임은 어디에 있어야 하는가
- 이 이름은 3개월 뒤에도 의미가 유지되는가
- LIVE / SIMULATION 확장 시 구조가 버티는가
- 외부 시스템 의존과 도메인 규칙이 섞이지 않았는가
- setter 나열 대신 의미 있는 행위 메서드로 표현했는가
- 이 코드는 테스트하기 쉬운가

## 13. 작업 원칙 요약

- 이름은 선명하게
- 책임은 좁게
- 분기는 전략으로
- 도메인은 풍부하게
- 전역은 절제해서
- 구조는 성장 가능하게
