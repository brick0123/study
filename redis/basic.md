# Redis란?

Redis는 db, 캐시, 메시지 브로커로 사용하는 오픈소스. 데이터 저장소로 디스크가 아닌 메모리를 사용해서 빠른 접근을 가능하게 한다.</br> 기본적으로 key-value 저장 방식이지만, `List`, `Set`, `Sorted Set`, `Hash` 등 다양한 저장 방식을 제공한다.

Redis는 기본적으로 싱글 스레드를 지원하며, 모든 자료구조는 `Atomic` 한 성질을 갖고 있어서, `race condition`을 피할 수 있다.


### 왜 사용할까?

- 자바 코드로는 서버가 여러대일 경우 consistency 문제가 있다.
- Atomic 자료구조
- 여러 서버에 같은 데이터 공유
- Cache 
- ..

### 기본 명령어

|종류| 설명 |
|--|--------|
|set| key value 데이터 저장|
|mset| 여러개의 key value 형태로 저장하기|
|get| key에 해당하는 value 조회|
|mget| 여러개의 key에 해당하는 value 조회|
|rename| key의 이름을 변경하기 기존key 변경할 key|
|keys| * 저장된 모든 key를 조회할 때|
|exists| 해당 키가 존재하는지|

</br>

### 실습

``` shell
# 도커로 레디스 실행 후 접속

docker run -p 6379:6379 --name redis_test -d redis

docker exec -it redis_test redis-cli
```

``` shell
127.0.0.1:6379> set 1 "brick" 
OK
127.0.0.1:6379> get 1         
"brick"
127.0.0.1:6379> exists 1      
(integer) 1
127.0.0.1:6379> mset 2 "brick2" 3 "brick3" 
OK
127.0.0.1:6379> mget 2 3
1) "brick2"
2) "brick3"
127.0.0.1:6379> keys *
1) "1"
2) "2"
3) "3"
127.0.0.1:6379> rename 1 11
OK
127.0.0.1:6379> exists 1
(integer) 0
127.0.0.1:6379> exists 11
(integer) 1
```


# 운영

- 레디스는 메모리 파편화가 발생할 수 있다.
- Swap이 없도록 해야한다. Swap이 한 번 발생하면 계속해서 디스크를 읽게됨
- Maxmemory 를 설정하더라도 이보다 더 사용할 가능성이 높다.

# 메모리관리

- Redis는 In-Memory Data Store.
- Physical Memory 이상을 사용하면 문제가 발생
    - swap이 있다면 swap 사용으로 해당 메모리 Page접근시 마다 늦어짐
    - swap이 없다면?
- Maxmemory를 설정하더라도 이보다 더 사용할 가능성이 큼
    - 레디스는 메모리 할당을 jemalloc에 의존한다. 그러면 자기가 얼마나 쓰는지 정확히 모를 수 있다.
    memory allocator 구현에 따라 달라질 수 있다.
    - 메모리 파편화가 일어날 수 있음.
- RSS 값을 모니터링 해야함

O(N) 관련 명령는 주의.

- 레디스는 싱글 스레드기 때문에 한 번에 하나의 커멘드만 실행할 수 있다. 처리시간이 긴 명령어를 수행하면 그 뒤 명령어들은 전부 대기해야한다.
- `Keys` , `FLUSHHALL`, `FLUSHDB`, `Delete Collections`, `Get All Collections`등의 명령어는 치명적이니 주의해야한다.

# 트랜잭션

`MULTI`, `EXEC`, `DISCARD`, `WATCH`는 Redis에서 트랜잭의 기반이다. 이들은 한 단계에서 명령 그룹을 실행할 수 있고, 두 가지 중요한 점을 보장한다.
- 이 명령어들은 트랜잭션에서 순차적으로 실행된다. 트랜잭션 실행 중에 다른 클라이언트가 발행 한 요청이 제공되는 일은 절대 없다. </br>
  즉 단일 isolated 작업으로 실행되는 것이 보장된다.
- 모든 Commandem들이 처리, 혹은 수행되지 않음으로써 `atomic`을 보장한다. `EXEC`명령어는 트랜잭션에 모든 명령어를 수행한다. </br>
  따라서 클라이언트가 EXEC 명렁어를 호출하기 전에 서버와 연결이 끊어진다면, Transaction context 내의 작업들은 하나도 수행되지 않는다. </br>
  그러나 EXEC 명령어가 호출되었다면, 모든 작업들이 수행된다.

### 실습

레디스 트랜잭션은 `MULTI` 명령어로 실행을 한다. Redis는 명렁어를 대기열에 추가한다. EXEC가 호출되면 모든 명령어가 실행된다. </br>
`DISCARD`를 호출하면 트랜잭션 대기열이 비워지고 트랜잭션이 종료된다.

key foo와 bar의 값을 atomic하게 증가시킨다.
``` shell
127.0.0.1:6379> MULTI
OK
127.0.0.1:6379(TX)> INCR foo
QUEUED
127.0.0.1:6379(TX)> INCR bar
QUEUED
127.0.0.1:6379(TX)> EXEC # 실행
1) (integer) 1
2) (integer) 1
```

``` shell
127.0.0.1:6379> flushall
OK
127.0.0.1:6379> MULTI
OK
127.0.0.1:6379(TX)> SET 1 brick
QUEUED
127.0.0.1:6379(TX)> SET 2 woodcock
QUEUED
127.0.0.1:6379(TX)> DISCARD # 실행 X
OK
127.0.0.1:6379> keys *
(empty array)
```

### Errors inside a transaction

트랜잭션 실행도중 다음 두 가지 에러가 발생할 수 있다.
- `EXEC`를 실행하기 전에 Queue에 적재하는 도중 실패할 수 있다. 예를 들어 명령어가 문법적으로 틀렸거나, 메모리가 부족과 같은 치명적인 상황에 발생할 수 있다.
- `EXEC`가 호출되고 나서 실패할 수 있다. 예를들어 잘못된 명령어(sting value에 list 연산을 호출)를 호출한 경우 발생할 수 있다.
클라이언트는 queue command의 반환 값을 확인하여 EXEC 호출 전 발생하는 첫번째 에러를 감지할 수 있다. 응답 값으로 `QUEUE`가 온 경우 성공적으로 처리된 것이다. 그렇지 않은 경우 Error를 응답 받는다. </br>

``` shell
127.0.0.1:6379> MULTI
OK
127.0.0.1:6379(TX)> SET a abc
QUEUED
127.0.0.1:6379(TX)> LPOP a # Wrong Command
QUEUED
127.0.0.1:6379(TX)> SET a 123
QUEUED
127.0.0.1:6379(TX)> GET a
QUEUED
127.0.0.1:6379(TX)> EXEC
1) OK
2) (error) WRONGTYPE Operation against a key holding the wrong kind of value
3) OK
```
명령이 실패하더라도 Queue의 다른 모든 명령어들이 처리된다. Redis는 명령 처리를 중단하지 않는다.
 
###  롤백을 하지 않는 이유?
트랜잭션 처리 도중 Command가 일부 실패해도 레디스는 롤백하지 않고 계속 실행해 보이는 게 이상해 보일 수 있지만, 이런 방식은 다음과 같은 이점이 있다.

- 레디스 명령은 잘못된 구문으로 호출한 경우에만 실패할 수 있으며, 혹은 잘못된 데이터 유형에 대한 요청이 실패할 수 있다. </br>
  이 뜻은 실질적인 측면에서 명령 실패는 프로그래밍 오류의 결과이며, 대부분 개발 중에 발견될 가능성이 높으므로 Production에 발생할 경우가 적다.
- Redis는 롤백 기능이 필요하지 않기 때문에 내부적으로 단순화되고 더 빠르다.