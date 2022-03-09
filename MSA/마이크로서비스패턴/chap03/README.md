# chap03

# 프로세스간 통신

---

핵심 내용

- 다양한 `통신 패턴` 및 특징
- `비동기 메시징` 서비스의 장점

IPC가 필요한 이유

- 모놀리스 아키텍처에서는 서로 함수를 통해 호출하기 때문에 IPC는 크게 필요하지 않지만, msa 환경에서는 서비스간 요청을 처리해야 하므로 IPC를 통해 상호작용 해야한다.

---

## MSA IPC 개요

클라이언트 서버 간 상효 작용하는 스타일은 다음 두 가지로 나눌 수 있다.

1. 일대일 / 일대다
2. 동기 / 비동기

일대일 상호 작용

- **동기 요청 응답**: 클라이언트는 요청을 보내고 응답이 올 때 까지 블록킹될 수 된다. 서로 강한 결합이 되어있다.
- **비동기 요청 응답**: 클라이언트는 요청을 보내고, 블록킹 되지 않고 비동기적으로 응답을 받을 수 있다.
- **단방향 알림**: 클라이언트는 일방적으로 요청만 하고, 응답을 받지 않는다.

일대다 상호 작용

- **발행 / 구독**: 클라이언트는 알림 메세지를 발행하고, 여기에 관심있는 서비스들이 메세지를 소비한다.
- **발행/ 비동기 응답**: 클라이언트는 요청 메세지를 발행하고, 주어진 시간동안 서비스가 응답하길 기다린다.

## 메세지  포멧

### 텍스트 메세지 포맷

대표적으로 JSON이 있다. 사람이 편히 읽을 수 있는 장점이 있다. 단점은 메세지가 크면 오버헤드가 발생할 수 있다. 성능이 중요한 경우 이진 포맷을 고려할 수 있다.

### 이진 포맷

대표적으로 에이브로, 프로토콜 버퍼가 있다. 에이브로 컨슈머는 스키마를 알고 있어야 해석할 수 있기 때문에, API 발전 측면에서 프로토콜 버퍼가 더 용이하다.’

---

## 동기식 RPC 패턴 응용

### RPC

- 원격에 존재하는 프로시저를 로컬에 있는 것처럼 실행하는 것
- 원격과의 호출
- 호출 / 데이터 형식은 어떻게 맞출지 (Stub)
- 복잡하고 낮은 개발 생산성으로 잘 사용되지 않고 REST로 대체

![image](https://user-images.githubusercontent.com/61832162/157371948-f470481c-68f0-4e6d-98d4-99ca56d72d54.png)
Refernece: [https://stackoverflow.com/questions/49628943/rpc-remote-procedure-call-process/49629189](https://stackoverflow.com/questions/49628943/rpc-remote-procedure-call-process/49629189)

### REST API

현대 가장 많이 사용하는 IPC. REST는 동사로 행위를 정의하고, url로 자원을 표현한다.
실무에서는 HATEOAS를 지키지 않아서 완벽한 REST API에 해당하지 않는다.

**장점**

- 단순하다.
- URL을 보고 쉽게 동작을 추측할 수 있다.

**단점**

- 작업을 http 동사에 매핑하는 데 한계가 있다.
    - 데이터를 수정할 때 PUT을 사용하지만, 데이터를 업데이트 하는 사유는 굉장히 많이 존재한다. 
    ex) 상품 교환.. 상품 취소..
- 가용성이 떨어진다 (중간 메세지 브로커 없음)
- 서비스 인스턴스의 URL을 알고 있어야함. (서비스 디스커버리를 이용하면 극복)

### gRPC

모든 환경에서 실행할 수 있는 **고성능** RPC 프레임워크.

![image](https://user-images.githubusercontent.com/61832162/157372057-c72702ff-256f-46aa-909d-ff4fcd0f87db.png)

Reference: [https://docs.microsoft.com/ko-kr/aspnet/core/grpc/comparison?view=aspnetcore-6.0](https://docs.microsoft.com/ko-kr/aspnet/core/grpc/comparison?view=aspnetcore-6.0)

---

## 부분 실패 처리: 회로 차단기 패턴

분산 시스템에서는 다른 서비스를 동기 호출할 때마다 실패할 가능성이 항상 존재한다. 응답이 늦어지거나, 서비스가 내려가서 통신 불능 등의 사유가 있다.

`회로 차단기`: 연속 실패 횟수가 주어진 임계치를 초과하면 일정 시간동안 호출을 즉시 거부하는 RPI

 프록시.

서버측에서 응답을 내려주지 않으면, 스레드가 응답을 기다리면서 무한정 블록킹할 수도 있다. 솔루션은 크게 두 가지가 있다.

- 무응답 원격 서비스를 처리하기 위해, 견고한 RPC Proxy 설계
- 원격 서비스가 실패하면 어떻게 조치할지 결정한다.

### 견고한 타임 RPI 프록시 설계

- 네트워크 타임아웃
- 미처리 요청 개수 제한: 클라이언트가 특정 서비스에 요청 가능한 미처리 요청의 최대 개수를 설정한다.
- 회로 차단기 패턴: 성공/실패 요청 개수를 지켜보다 에러율이 주어진 임계치 초과하면 그 이후 시도는 바로 실패처리. 타임아웃 시간 이후 클라이언트가 재시도해서 성공하면 차단기는 닫힌다.

[https://github.com/Netflix/Hystrix](https://github.com/Netflix/Hystrix)

### 불능 서비스 복구

부분 실패시 미리 정해진 값이나, 해당 값을 제외하고 반환하는 등 결과를 반환할 수 있다.

---

## 서비스 디스커버리

동적으로 할당된 IP주소를 알아내서 자동으로 매핑시킨다.

### 애플리케이션 수준의 적용

서비스 인스턴스는 자신의 네트워크 위치를 서비스 레지스트리에 등록하고, 서비스 클라이언트는 이 서비스 레지스트리로부터 목록 중 한 인스턴스로 요청을 라우팅한다.

1. 자가 등록: 서비스 인스턴스는 자신의 네트워크 위치를 서비스 레지스트리 등록 API를 호출해서 등록한다.
2. 클라이언트 쪽 디스커버리 패턴: 클라이언트는 서비스를 호출할 때, 먼저 서비스 레지스트리에 서비스 인스턴스 목록을 요청해서 넘겨받는다(캐시 가능).

[https://github.com/Netflix/eureka](https://github.com/Netflix/eureka)

단점

- 언어에 맞는 서비스 라이브러리가 필요하다.
- 서비스 레지스트리를 직접 설정 / 관리 해줘야함.

### 플랫폼에 내장된 서비스디스커버리 패턴

플랫폼에는 DNS명, 가상 IP 등을 각 서비스마다 부여한다. 서비스 클라이언트가 DNS/VIP를 요청하면 배포 플랫폼이 알아서 가용 서비스에 라우팅한다. 플랫폼이 서비스 등록, 디스커버리, 요청 라우팅을 전부 관장한다.

서비스 IP를 추적하는 서비스 레지스트리는 배포 플랫폼에 내장되어있다.

이 방식은 다음 두 가지 패턴을 적용했다.

- 서드파티 등록 패턴: 서비스 자신을 서비스 레지스트리에 등록하는 것이 아니라, 배포 플랫폼의 일부인 등록기라는 서드파티가 작업을 대행
- 서버 쪽 디스커버리 패턴: 클라이언트가 서비스 레지스트리를 질의하지 않고, DNS명을 요청한다. 그럼 서비스 레지스트리를 쿼리하고 요청 부하 분산하는 요청 라우터로 핵석된다.

장점

- 플랫폼이 모두 알아서 처리하므로 편함.
- 서비스 디스커버리 관련 코드가 클라이언트 / 서버 어디에도 없으므로 언어에 종속적이지 않음

---

## 비동기 메세징 패턴 응용 통신

비동기 통신을 하기 때문에 클라이언트가 응답을 기다리며 블록킹하지 않는다.

### 메세지

메세지 종류

- 문서: 데이터만 포함된 제네릭한 메세지.
- 커맨드: RPC 요청과 동등한 메세지. 호출할 작업과 전달할 매개변수가 지정되어있다.
- 이벤트: 송신자에게 어떤 사건이 발생했음을 알리는 메세지. (주로 변화 상태를 알리는 메세지)

### 메세지 채널

메세지는 채널을 통해 교환된다.

메세지 채널의 종류

- 점대점: 채널을 읽는 컨슈머 중 딱 하나만, 지정하여 메시지를 전달 (queue)
- 발행 - 구독: 같은 채널을 바라보는 모든 컨슈머에 메시지를 전달. (일대다) (topic)

## 메세지 상호 작용 스타일 구현

### 동기, 비동기 요청/응답

메세징 성격 자체가 비동기적이라, 비동기 요청/응답만 제공하지만, 응답을 수신할 때까지 클라이언트를 블록킹할 수도 있다. 보통은 비동기적으로 상호 작용한다.

- 메세지를 요청하고 응답 메세지까지 받는 경우는 어느때 사용? 쿼리?

### 단방향 알림

점대점 채널로 클라이언트가 메세지를 보내면, 서버는 이 채널을 구독해서 메세지를 처리하는 구조다. 여기서 단방향은 응답을 반환하지 않는다.

### 발행/구독

발행/구독 스타일은 상호 작용을 기본 지원한다. 클라이언트는 여러 컨슈머가 읽는 발행/구독 채널에 메세지를 발행하고, 서비스는 도메인 객체의 변경 사실을 알리는 도메인 이벤트를 발행. 서비스는 자신이 관심 있는 도메인 객체의 이벤트 채널을 구독함.

### 발행/비동기 응답

클라이언트는 발행하고, 컨슈머는 응답 메세지를 지정된 응답 채널에 메세지를 발송한다.

## 메세징 기반 API 명세 작성

### 비동기 작업 문서화

서비스 작업은 두 가지 상호 작용 스타일 중 하나로 호출할 수 있다.

- **요청/비동기 응답 스타일 API**: 서비스 커맨드 메세지 채널, 서비스가 받는 커맨드의 타입과 포맷, 서비스가 반환하는 응답 메세지의 타입과 포맷으로 구성된다.
- **단방향 알림 스타일 API**: 서비스의 커맨드 메세지 채널, 서비스가 받는 커맨드 메세지의 타입과 포맷으로 구성된다.

### 발행 이벤트 문서화

이런 API 명세는 이벤트 채널, 서비스가 채널에 발행하는 이벤트 메세지의 타입과 포맷으로 구성됨.

## 메세지 브로커

![image](https://user-images.githubusercontent.com/61832162/157372091-f1866541-e231-4562-9c62-e2f2a6ef5a44.png)

Reference: [https://ademcatamak.medium.com/what-is-message-broker-4f6698c73089](https://ademcatamak.medium.com/what-is-message-broker-4f6698c73089)

메세지 브로커는 서비스가 통신할 수 있게 해주는 인프라 서비스.

### 브로커리스 메세징

![image](https://user-images.githubusercontent.com/61832162/157372134-ae691059-0296-4241-aa80-c8b976328070.png)

Reference: [https://www.linkedin.com/pulse/how-make-microservices-communicate-noorain-panjwani/](https://www.linkedin.com/pulse/how-make-microservices-communicate-noorain-panjwani/)

브로커리스 아키텍처의 서비스는 메세지를 서로 직접 교환한다. (대표적 ZeroMQ)

장점

- 수신자로 직접 전달하기 때문에, latency가 줄어든다.
- 메세지 브로커가 성능 병목점이나 SPOF가 될 일이 없다.
- 운영 복잡도가 낮다.

단점

- 서비스의 위치를 알아야 하므로, 서비스 디스커버리 메커니즘을 사용해야한다.
- 메세지 송신/수신 서버가 실행중이어야 하므로 가용성 저하
- 신뢰성이 떨어진다. (전달 보장)

### 브로커 기반 메세지

메세지 브로커는 모든 메세지가 지나가는 중간 지점이다. 송신자 → 메세지 브로커 → 수신자로 전달한다.

장점

- `느슨한 결합`: 컨슈머의 위치를 몰라도 된다.
- `버퍼링`: 메세지 브로커는 처리 가능한 시점까지 버퍼링한다. 컨슈머가 처리할 수 있을 때까지 큐에 메세지가 쌓인다. 덕분에 컨슈머가 불능상태가 되어도 컨슈머쪽에서는 메세지를 계속 발행할 수 있다.
- `신뢰성 보장`: 메세지 전달 보장 가능
- `가용성 상승`: 송신자 서버가 다운되어도, 메세지 브로커에 메세지가 보관

메세지 브로커를 선택하기 전 검토해야될 것

- 프로그래밍 언어 지원: 다양한 언어 지원할 수록 유리
- 메세지 순서: 메세지의 순서 유지?
- 전달 보장: 어떤 종류의 전달 보장?
- 영속화: 브로커가 고장나더라도 문제가 없도록 메세지를 디스크에 저장?
- 지연 시간: 종단 간 지연시간

### 메세지 브로커로 메세지 채널 구현

메세지 채널은 메세지 큐보다 구현 방식이 조금씩 다르다.

---

## 수신자 경합과 메세지 순서

스케일 아웃 상황에서 메세지를 어떻게 한 번만 처리를 보장할 수 있을까?

카프카 등 요즘 메세지 브로커는 샤딩된 채널을 이용한다.

솔루션은 세 부분으로 구성된다.

1. 샤딩된 채널은 복수의 샤드로 구성되며, 각 샤드는 채널처럼 동작
2. 송신자는 메세지 헤더에 키를 지정한다. 메세지 브로커는 메세지를 샤드 키별로 샤드/파티션에 배정. 
ex) 샤드 키 해시 값을 샤드 개수로 나눈 나머지를 선택해서 선택하는 식
3. 메세징 브로커는 여러 수신자 인스턴스를 묶어, 마치 동일한 논리의 수신자처럼 취급. 메세지 브로커는 각 샤드를 하나의 수신자에 배정하고, 수신자가 시동/종료하면 재배정함

순서처리는 이해 못함.

## 중복 메세지 처리

mb는 각 메세지를 한 번만 전달한다. 하지만 클라이언트나 네트워크, mb에 문제가 생길 경우, 같은 메세지를 여러번 전달할 수도 있다.

중복 메세지를 처리하는 방법

- 멱등한 메세지 핸들러 작성
- 메세지를 추적하고 중복을 솎아 낸다. (테이블에 따로 기록)

## 트랜잭셔널 메세징

서비스는 보통 DB를 업데이트 하는 동안 트랜잭션의 일부로 메세지를 발행한다. DB업데이트와 메세지 전송을 한 트랜잭션으로 묶지 않으면, DB업데이트 후 메세지는 아직 전송되지 않은 상태에서 서비스가 중단될 수 있기 때문에 문제가 발생할 수 있다. (메세지 전송을 트랜잭션으로 묶으면 무슨 이점?)

애플리케이션에서 메세지를 확실하게 보내느 방법.

### 트랜잭셔널 아웃박스

DB 테이블을 임시 메세지 큐로 사용.

메세지를 보내는 서비스에 outbox라는 테이블을 만들고, 커맨드 DB 트랜잭션의 일부로 outbox 테이블에 메세지 삽입.

![image](https://user-images.githubusercontent.com/61832162/157372176-f4bbca13-dbdb-44a5-b137-24c5046dc1a0.png)

[https://microservices.io/patterns/data/transactional-outbox.html](https://microservices.io/patterns/data/transactional-outbox.html)

outbox는 임시 메세지 큐 역할을 한다.

### 폴링 발행기 패턴

메세지 릴레이로 테이블을 폴링해서 미발행 메세지를 조회.

```java
select * from outbox orderby ~; // 주기적으로 실행
```

그리고 발행된 메세지는 outbox테이블에서 삭제.

단점: DB를 자주 폴링하는 비용 발생

### 트랜잭션 로그 테일링 패턴

메세지 릴레이로 DB 트랜잭션 커밋 로그를 테일링.

단점: 개발 공수가 크다.

### 메세징 라이브러리

[https://github.com/eventuate-tram/eventuate-tram-core](https://github.com/eventuate-tram/eventuate-tram-core)

## 비동기 메세징으로 가용성 개선

- 데이터 복제: 서비스가 필요한 데이터의 레플리카를 유지한다. 이 데이터는 이벤트를 구독해서 최신 데이터로 유지할 수 있다.

---

추가 정리

### Avro, Protocol Buffers

공통점: 이진 포맷

`프로토콜 버퍼`

구글에서 개발한 직렬화 데이터 자료구조 오픈소스. 다양한 언어를 지원하고 직렬화 속도가 빠르고, 직렬화된 파일 크기도 작아서 Avro 파일 포맷과 함께 사용됨.

gRPC는 메세지를 프로토콜 버퍼를 이용해서 직렬화 한다.
특징

- 하나의 파일에 최대 64MB
- JSON 파일을 프로토콜 버퍼 포맷으로 서로 전환 가능

```json
// JSON
{
  “userName”: “Martin”,
  “favouriteNumber”: 1337,
  “interests”: [“daydreaming”, “hacking”] 
}

```

```protobuf
// Protocol Buffer
message Person {
    required string user_name        = 1;
    optional int64  favourite_number = 2;
    repeated string interests        = 3;
}

```

![image](https://user-images.githubusercontent.com/61832162/157372242-dfe4d38b-d79b-4047-af9c-9912929665d1.png)

Reference: [https://martin.kleppmann.com/2012/12/05/schema-evolution-in-avro-protocol-buffers-thrift.html](https://martin.kleppmann.com/2012/12/05/schema-evolution-in-avro-protocol-buffers-thrift.html)

```protobuf
// Avro
// Json 포맷
{
  “type”: “record”,
  “name”: “Person”,
  “fields”: [
      {“name”: “userName”,        “type”: “string”},
      {“name”: “favouriteNumber”, “type”: [“null”, “long”]},
      {“name”: “interests”,       “type”: {“type”: “array”, “items”: “string”}}
  ]
}
// IDL
record Person {
    string               userName;
    union { null, long } favouriteNumber;
    array<string>        interests;
}
```

### queue vs topic

![image](https://user-images.githubusercontent.com/61832162/157372275-eb00974c-cd17-4e55-82f7-ada9d2fe991c.png)

Reference: [https://pediaa.com/what-is-the-difference-between-queue-and-topic/](https://pediaa.com/what-is-the-difference-between-queue-and-topic/)

### Reference

- [https://docs.microsoft.com/ko-kr/windows/win32/rpc/how-rpc-works](https://docs.microsoft.com/ko-kr/windows/win32/rpc/how-rpc-works)
- [https://youtu.be/sKWy7BJxIas](https://youtu.be/sKWy7BJxIas)
- [https://www.geeksforgeeks.org/remote-procedure-call-rpc-in-operating-system/](https://www.geeksforgeeks.org/remote-procedure-call-rpc-in-operating-system/)
- [https://pediaa.com/what-is-the-difference-between-queue-and-topic/](https://pediaa.com/what-is-the-difference-between-queue-and-topic/)