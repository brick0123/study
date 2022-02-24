# modern-java-in-action

### 외부 반복, 내부 반복

컬렉션 인터페이스 → 사용자가 직접 요소 반복 = **외부 반복**

스트림 → 알아서 처리하고 결과 스트림값을 어딘가에 저장 = **내부 반복**

```java
List<String> names = List.of();
for (Dish dish: menu) { // 메뉴 리스트를 명시적으로 순차 반복
	names.add(dish.getName()); // 이름을 추출해서 리스트에 추가
}
```

**스트림**

```java
List<String> names = dish.stream()
  .map(Dish::getName)
  .collect(toList()); // 파이프 라인을 실행. 반복자 필요 x
```

### 중간 연산

게으른 연산 → 최적 효과 얻을 수 있다.

limit → 쇼트서킷, filter와 map은 다른 연산이지만 한 과정으로 병합됨 (루프 퓨전)

### 최적화 방법

- takeWhile: 프레디케이트가 처음으로 참이 되는 지점까지 요소를 취한다.
- dropWhile: 프레디케이트가 처음으로 거짓이 되는 지점까지 요소를 버린다.

### 스트림 평면화

["Hello", "World"]를 고유 문자로 바꿔보자 → ["H", "e" ... ]

```java
words.stream()
	.map(word -> word.split(""))
	.distinct()
	...();
```

위 코드는 map으로 `String[]` 을 반환한다. 따라서 최종적으로 `Stream<String[]>` 을 반환한다. 이는 **flapMap**으로 해결할 수 있다.

- map과 [Arrays.stream](http://Arrays.stream) 활용

```java
words.stream()
    .map(word->word.split(""))
    .map(Arrays::stream)// Arrays.stream()각 배열을 별도의 스트림으로 생성
.distinct()
    .collect(Collectors.toList());
```

결국 `List<Stream<String>>` 이 만들어지고 문제가 해결되지 않았다.
문제가 해결되려면 먼저 각 단어를 개별 문자열로 이루어진 배열로 만든 다음에 각 배열을 별도의 스트림으로 만들어야한다.

- flapMap활용

```java
words.stream()
            .map(word -> word.split(""))
            .flatMap(Arrays::stream) // 생성된 스트림을 하나의 스트림으로 평면화
            .distinct()
            .collect(Collectors.toList());
```

flapMap은 각 배열을 스트림이 아니라 스트림의 콘텐츠로 매핑한다.

즉 map(Arrays::stream)과 달리 falMap은 하나의 평면화된 스트림을 반환한다.
요약하면 flapMap은 스트림의 각 값을 다른 스트림으로 만든 다음에 모든 스트림을 하나의 스트림으로 연결하는 기능을 수행한다.

### 리듀싱

모든 스트림 요소를 처리해서 값으로 도출한다.

```jsx
Supplier<A> supplier();

BiConsumer<A, T> accumulator();

BinaryOperator<A> combiner();

Function<A, R> finisher();

Set<Characteristics> characteristics();
```

# 병렬

병렬 스트림은 내부적으로 ForkJoinPool을 사용한다.

효과적으로 포크/조인 프레임웤을 사용하는 법

- join 메서드를 태스크에 호출하면 태스크가 생산하는 결과가 준비될 때까지 호출자를 블록시킨다.
따라서 두 서브태스크가 모두 시작된 다음에 join을 호출해야한다. 그렇지 않으면 각각의 서브태스크가 다른 태스크가 끝나길 기다리는 일이 발생하면 원래 순차 알고리즘보다 느려지고 복잡해질 수 있다.
- `RecursiveTask` 내에서는 ForkJoinPoold의 **invoke** 메서드를 사용하지 마라.
대신 `compute` 나 `fork` 메서드를 직접 호출할 수 있다. 순차 코드에서 병렬 계산을 시작할 때만 invoke를 사용하라
- 서브태스크에 fork 메서드를 호출해서 FokJoinPool의 일정을 조절할 수 있다.
**한쪽 작업에는 fork보다는 compute를 호출하는 것이 효율적**. 그러면 두 서브 태스크의 한 태스크에는 같
같은 **스레드를 재사용할 수 있어서 풀에서 불필요한 태스크를 할당하는 오버헤드를 피할 수 있음.**
- 포크 / 조인 프레임워크를 이용하는 병렬 계산  = 디버깅 어렵.
포크조인 프레임워크에서는 fork라 불리는 다른 스레드에서 compute를 호출하므로 스택 트레이스가 도움되지 않음
- 병렬 처리가 효율적이려면, 서브태스크의 실행시간보다 새로운 태스크를 포킹하는 데 시간보다 길어야한다.

### 작업 훔치기

각각의 서브태스크의 작업 시간이 크게 다를 수 있고, 분할 기법이 효율적이지 않거나 예기치 않게 디스크 접근 속도가 저하되거나, 외부 서비스 협력 과정에서 지연이 발생할 수 있다.
포크조인 프레임웤에선 **작업 훔치기**라는 기법으로 이 문제를 해결한다.

작업 훔치기 기법에는 ForkJoinPool의 모든 스레드를 거의 공정하게 분배.
각각 스레드는 자신에게 할당된 테스크를 포함한 `doubly linked list` 를 참조하면서 작업이 끝날 때 마다, 큐의 헤드에서 다른 태스크를 가져와 작업을 처리한다.

할 일이 없는 스레드는 유휴 상태가 되지 않고, 다른 큐의 꼬리에서 작업을 훔쳐온다. 모든 큐가 빌 때 까지 이 과정을 반복한다.

### Spliterator

자바8에는 Spliterator라는 인터페이스를 제공. Spliterator는 분할할 수 있는 반복자라는 의미다. Iterator처럼 소스의 요소 탐색 기능 제공하지만 병렬 작업에 특화처리되어있다.

- tryAdvance: 요소를 하나씩 순차적으로 소비하면서 탐색할 요소가 남아있으면 참을 반환.
- trySplit: Spliterator의 일부 요소(자신이 반환한 요소)를 분할해서 두 번째 Spliterator를 생성
- estimateSize: 탐색해야 할 요소 수 정보를 제공

## Refactor

익명클래스의 this: 익명 클래스 자신

람다의 this: 람다를 감싸는 클래스.

익명 클래스는 감싸고 있는 클래스의 변수를 가릴 수 있음(섀도 변수). 하지만 다음 코드에서 보여주는 것처럼 람도로는 변수 가리기 불가

```java
int a = 10;
Runnable r1 = () -> {
    int a = 2; // compile error
    System.out.println("a = " + a);
};

```

마지막으로 익명 클래스를 람다 표현식으로 바꾸면 콘텍스트 오버로딩에 따라 모호함이 초래될 수 있다. 익명 클래스는 인스턴스화 할 대 명시적으로 형식이 정해지는 반면, 람다는 콘텍스트에 따라 달라짐.

다음 코드는 문제가 발생할 수 있음.

```java
interface Task {

    void execute();
}

public static void doSomething(Runnable r) {
        r.run();
}

    public static void doSomething(Task r) {
        r.execute();
}

doSomething(new Task() {
            @Override
            public void execute() {
                System.out.println("danger danger");
            }
```

하지만 익명 클래스를 람다 표현식으로 바꾸면 메서드 호출이 Runnable과 Task모두 대상 형식이 되어 문제생길 수 있다.

```java
doSomething( () -> System.out.println("danger danger"));

이런 경우 명시적 형변환이 필요

doSomething((Task) () -> System.out.println("danger danger"));
```

## 테스트

### 람다를 사용하는 메서드의 동작에 집중하라.

람다의 목표는 정해진 동작을 다른 메서드에 사용할 수 있도록 하나의 조각으로 캡슐화하는 것.

세부 구현을 포함하는 람다 표현식을 공개하지 말아야한다.

람다 표현식을 사용하는 메서드의 동작을 테스트함으로써 람다를 공개하지 않으면서도, 람다 표현식을 검증할 수 있다.

```java
public static List<Point> moveAllPointsRightBy(List<Point> points, int x) {
            return points.stream()
                .map(p -> new Point(p.getX() + x, p.getY()))
                .collect(Collectors.toList());
}
```

위 코드에서 `p -> new Point(p.getX() + x, p.getY())` 를 테스트하는 부분은 없다.

그냥 `moveAllPointsRightBy` 메서드를 구현한 코드일뿐.

```java
@Test
    void testMoveAllPoints() {
        final List<Point> points = List.of(new Point(5, 5), new Point(10, 5));
        final List<Point> expected = List.of(new Point(15, 5), new Point(20, 5));

        final List<Point> result = Point.moveAllPointsRightBy(points, 10);
        assertEquals(expected, result);
    }
```

### 복잡한 람다를 개별 메서드로 분할하기

복잡한 람다를 어떻게 테스트할까? → 람다 표현식을 메서드 참조로 바꾸는 것.

일반 테스트하듯이 람다 표현식을 테스트할 수 있다.

### 고차원 함수 테스팅

고차원 함수: 함수를 인수로 받거나, 다른 함수를 반환하는 메서드.

메서드가 람다를 인수롤 받는다면, 다른 람다로 메서드의 동작을 테스트할 수 있다.

테스트해야 할 메서드가 함수를 반환하면 어떻게 할 까? → 함수형 인터페이스의 인스턴스로 간주하고 함수의 동작을 테스트할 수 있다.

# Optional

### 도메인 모델에 Optional을 사용했을 때 직렬화를 사용할 수 없는 이유

```java
우리는 도메인 모델에서 값이 꼭 있어야 하는지, 없어을 수 있는지 표현할 수 있다.
Optional설계자는 이와 다른 용도로만 사용할 것은 가정했다.
브라이언 고츠는 Optional의 용도가 선택형 반환값을 지원하는 것이라고 했다.

필드 형식으로 사용할 것을 가정하지 않아서 Serializable 인터페이스를 구현하지 않았다.
따라서 도메인 모델에 Optional을 사용한다면, 직려롸를 사용하는 도구에서 문제가 발생할 수 있디.
 
```

# Date

- DateFormat: 스레드 세이프하지 않다.
    - 두 스레드가 동시에 하나의 포매터로 날짜를 파싱할 때, 예기치 못한 결과 발생 가능.
- Date, Calendar 모두 가변 클래스다.

# Module

궁극적인 소프트웨어 아키텍처 즉 고수준에서는 기반 코드를 바꿔야 할 때, 유추하기 쉬우므로 생산성을 높일 수 있는 소프트웨어 프로젝트가 필요하다.

### 관심사분리 (separation of concerns, **SoC**)

SoC의 장점

- 개별 기능을 따로 작업할 수 있으므로, 협업이 원활함
- 재사용성이 높음
- 전체 유지보수성이 높아짐

### 정보 은닉

세부 구현을 숨겨서, 어떤 부분을 바꿨을 때 다른 부분까지 영향을 미치는 가능성을 줄일 수 있다.

자바 9 이전까진 클래스와 패키지가 의도된 대로 공개되었는지를 컴파일러로 확인할 수 없었다.

### 모듈 시스템이 설계된 이유

자바 9 이전까지 모듈화된 소프트웨어 프로젝트를 만드는 데 한계가 있었다.
자바는 클래스, JAR, 패키지 세 가지 수준의 코드 그룹화를 제공한다. 클래스는 접근 제한자와 캡슐화를 지원하지만, 패키지와 JAR 수준에서는 캡슐화가 어려웠다.

### 클래스 경로

클래스 모두 컴파일 한 뒤 JAR 파일에 넣고 클래스 경로에 이 JAR 파일을 추가해 사용할 수 있다. 그러면 JVM이 동적으로 클래스 경로에 정의된 클래스를 필요할 때 읽는다.

클래스 경로와 JAR 조합에는 몇 가지 약점이 있다.

1. 클래스 경로에는 같은 클래스를 구분하는 버전 개념이 없다. 클래스 경로에 두 가지 버전의 같은 라이브러리가 존재할 때 어떤 일이 일어나는지 예측 불가. 다양한 컴포넌트가 같은 라이브러리의 다른 버전을 사용할때 발생훌 수 있다.
2. 클래스 경로는 명시적인 의존성을 지원하지 않는다. 각각의 jar안에 있는 모든 클래스는 classes라는 한 주머니로 합쳐짐. 한 jar가 다른 jar에 포함된 클래스 집합을 사용하라고 명시적으로 의존성을 정의하는 기능을 제공 x. 클래스 경로 때문에 어떤 일이 일어나는지 파악하기 힘들며 다음과 같은 의문이 든다.
    1. 빠진 게 있는가?
    2. 충돌이 있는가?

메이븐이나 그레이들 같은 빌드 도구는 이럼 문제를 해결하는 데 도움을 주지만, 자바 9 이전에는 자바, JVM 누구도 명시적인 의존성을 정의하지 않았다. JVM이 `ClassNotFoundException` 같은 에러를 발생시키지 않고, 애플리케이션을 정상적으로 실행될 때까지 클래스 경로에 클래스 파일을 더하거나, 클래스 경로에서 클래스를 제거해보는 수 밖에 없다. 자바 9 모듈 시스템을 이용하면 컴파일 타임에 이런 종류의 에러를 검출할 수 있다.

### 거대한 JDK

jdk는 javac, 자바 애플리케이션을 로드하고 실행하는 java, 런타임 지원을 제공하는 라이브러리 등이 있다.  jdk는 발전하면서 덩치가 많이 커졌다.

### 자바 모듈

모듈은 module이라는 새 키워드에 이름과 바디를 추가해서 정의한다. **모듈 디스크럽터**는 module-info.java라는 파일에 저장된다.

모듈 디스크립터는 보통 패키지와 같은 폴더에 위치하며, 한 개 이상의 패키지를 서술하고 캡슐화할 수 있지만, 단순환 상황에서는 이들 패키지 중 한 개만 외부로 노출시킨다.

모듈 디스크립터의 핵심 구조(module-info.java)

```java
module 모듈명
exports 패키지명 (한 패키지를 노출시키는 간단한 형식)
requires 모듈명 (0개 이상의 모듈)

// 엄밀히 텍스트 형식 = 모듈 선언, module-info.class에 저장된 바이너리 형식을 모듈 디스크립터라고 한다.
```

# 개선된 동시성

### Executor와 스레드풀

자바 5는 Executor 프레임워크와 스레드 풀을 통해 스레드의 힘을 높은 수준을 끌어올리는 즉 자바 프로그래머가 테스크 제출과 실행을 분리할 수 있는 기능 제공.

**스레드의 문제**

자바 스레드는 직접 운영체제 스레드에 접근한다. 운영체제 스레드를 만들고 종료하면 비싼 비용을 치러야하며 더욱 운영체제 스레드의 숫자는 제한되어 있는 것이 문제다. 운영체제가 지원하는 스레드 수를 초과해 사용하면, 자바 애플리케이션이 크래시 될 수 있으므로, 기존 스레드가 실행되는 상태에서 계속 새로운 스데르를 만드는 상황이 발생하지 않도록 주의해야함.

보통 운영체제와 자바의 스레드 개수가 하드웨어 스레드 개수보다 많으므로, 일부 운영체제 스레드가 블록되거나 자고 있는 상황에서 모든 하드웨어 스레드가 코드를 실행하도록 할당된 상황에 놓을 수 있다. 예컨데 맥죽중 8코어 16스레드에서 서버에는 프로세서를 여러 개 포함할 수 있으므로, 하드웨어 스레드 64개를 보통 보유할 수 있다. 반면 노트북은 하드웨어 스레드를 한 두개 가지고 다양한 기기에서 실행할 수 있는 프로그램에서 미리 하드웨어 스레드 개수를 추측하지 않는 것이 좋다. 한편 주어진 프로그램에서 사용할 최적의 자바 스레드 개수는 사용할 수 있는 코어의 개수에 따라 달라진다.

### 스레드 풀이 더 좋은 이유

`ExecutorService` 는 테스트를 제출하고 나중에 결과를 수집할 수 있는 인터페이스를 제공한다. 

스레드 풀의 주의사항

- k 스레드를 가진 스레드 풀은 오직 k만큼의 스레드를 동시에 실행할 수 있다. IO를 기다리는 블록 상황에서 이들 태스크가 워커 스레드에 할당된 상태를 유지하지만, 아무 작업도 하지 않게 된다. 네 개의 하드웨어 스레드와 5개의 스레드를 갖는 스레드 풀에 20개의 테스크를 제출했다고 가정해보자. 모든 태스크가 병렬로 실행되면서 20개의 테스크를 실행할 것이라 예상할 수 있지만, 처음 제출한 세 스레드가 잠을 자거나 I/O를 기다린다고 가정하자. 그러면 나머지 15개의 태스크를 두 스레드가 실행해야 하므로 작업 효율성이 예상보다 절반으로 떨어진다. 처음 제출한 태스크가 기존 실행 중인 태스크가 나중의 태스크 제출을 기다리는 상황이라면 데드락에 걸릴 수도 있다.  핵심은 블록할 수 있는 태스크는 스레드 풀에 제출하지 말아야 하는 것이지만, 이를 항상 지킬 수 있는건 아니다.
- 중요한 코드를 실행하는 스레드가 죽는 일이 발생하지 않도록 자바 프로그램 main이 반환하기 전에 모든 스레드의 작업이 끝나길 기다린다. 따라서 프로그램을 종료하기 전에 모든 스레드 풀을 종료하는 습관을 가지는 것이 중요/

```java
@Test
    void test() throws InterruptedException {
        int x = 1337;
        final Result result = new Result();

        Thread t1 = new Thread(() -> result.left = f(x));
        Thread t2 = new Thread(() -> result.right = g(x));

        t1.start();
        t2.start();
        t1.join();
        t2.join();

        System.out.println(result.left + result.right);
    }
```

`Runnable` 대신 `Funture` API 인터페이스를 이용해서 코드를 더 단순화 할 수 있다.

```java
@Test
    void run() throws Exception {
        int x = 1337;
        final ExecutorService es = Executors.newFixedThreadPool(2);

        final Future<Integer> y = es.submit(() -> f(x));
        final Future<Integer> z = es.submit(() -> g(x));

        System.out.println(y.get() + z.get());
    }
```

이 코드도 submit 같은 불필요한 코드로 오염되었다. 명시적으로 반복으로 병렬화를 수행하던 코드를 이용해 내부 반복으로 바꾼 것처럼 비슷한 방법으로 문제를 해결해야한다.

**비동기 API**라는 기능으로 해결할 수 있다.  첫 번째 방법인 자바의 Future를 이용해서 이 문제를 조금 개선 가능. 자바5에서 소개된 Future는 자바 8의 `CompletableFuture` 로 이들을 조합할 수 있게 되면서 더욱 기능이 풍부해짐.

두 번째 방법은 발행-구독 프로토콜에 기반한 자바 9의 java.util.concurrent.Flow 인터페이스를 이용하는 방법이다.

### Future

대안을 이용하면 f의 시그니처가 다음처럼 바뀐다.

```java
Future<Integer> f(int x);
Future<Integer> g(int x);
```

그리고 다음처럼 호출이 바뀐다.

```java
Future<Integer> y = f(x);
Future<Integer> z = g(x);

sysout(y.get() + z.get();
```

메서드 f는 호출 즉시 자신의 원래 바디를 평가하는 태스크를 포함한 Future를 반환한다. 마찬가지로 g도 Future를 반환하여, get() 메서드를 이용해 두 Future가 완료되어 합쳐지길 기다린다.

예제에서는 API는 그대로 유지하고, g를 그대로 호출하면서 f에만 Future를 적용할 수 있었따. 하지만 더 큰 프로그램에서는 두 가지 이유로 이런 방식을 추천하지 않는다.

- 다른 상황에서는 g에도 Future 형식이 필요할 수 있으므로 API 형식을 통일하는 것이 바랍직하다.=
- 병렬 하드웨어 프로그램 실행 속도를 극대화하려면 여러 작은 하지만 합리적인 크기의 태스크로 나누는 것이 좋다

### 리액티브 형식

두 번째 대안에서 핵심은 f,g의 시그니처를 바꿔서 콜백 형식의 프로그래밍을 하는 것

```java
void f(int x, Inconsumer dealWithResult);
```

f에는 추가 인수로 콜백을 전달해서 f의 바디에서는 return 문으로 결과를 반환하는 것이 아니라, 결과가 준비되면 이를 람다로 호출하는 태스크 만드는 것이 비결이다.

다시 말해 f는 바디를 실행하면서 태스크를 만드는 다음 즉시 반환하므로 코드 형식이 다음처럼 바뀐다.

```java
		@Test
    void run() {
        int x = 1337;

        final Result result = new Result();

        f(x, (int y) -> {
            result.left = y;
            System.out.println(result.left + result.right);
        });

        g(x, (int z) -> {
            result.left = z;
            System.out.println(result.left + result.right);
        });
    }
```

f와 g의 호출 합계를 정확하게 출력하지 않고, 상황에 따라 먼저 계산된 결과를 출력한다.

락을 사용하지 않으므로 값을 두 번 출력할 수 있을 뿐더러 때로는 +에 제공된 두 피연산자가 println이 호출되기 전에 업데이트 될 수도 있다. 다음처럼 두 가지 방법으로 이 문제를 보완할 수있다.

- if-then-else를 이용해 적절한 락을 이용해 두 콜백이 모두 호출되었는지 확인한 다음 println을 호출해 원하는 기능을 수행할 수 있다.
- 리액티브 형식의 API는 보통 한 결과가 아니라, 일련의 이벤트에 반응하도록 설계도었으므로 Future를 이용하는 것이 적절하다

리액티브 형식의 프로그래밍으로 메서드 f와 g는 dealWithResult 콜백을 여러번 할 수 있다. 원래의 f, g 함수는 오직 한 번만 return을 사용하도록 되어있다.

마찬가지로 Future도 한 번만 완료되며, 그 결과는 get()으로 얻을 수 있다. 리액티브 형식의 비동기 API는 자연스럽게 일련의 값(나중에 스트림으로 연결)을, Future 형식의 API는 일회성의 값을 처리하는 데 적합하다.

### 잠자기는 해로운 것으로 간주

스레드는 잠들어도 여전히 시스템 자원을 점유한다. 스레드가 많아지고 그 중 대부분이 잠다면 심각해진다. 스레드 풀에서 잠을 자는 태스크는 다른 태스크가 시작되지 못하게 막으므로 자원을 소비한다 (운영제제가 이들 태스크를 관리하므로 일단 스레드로 할당된 태스크 중지시키지 못한다). 물론 블록도 마찬가지다.

태스크를 앞과 뒤 두부분을 나누고, 블록되지 않을 때만 뒷부분을 자바가 스케줄링하도록 요청할 수 있다

```java
@Test
void v1() throws InterruptedException {
    work1();
    TimeUnit.SECONDS.sleep(2);
    work2();
}

@Test
 void v2() {
    finalScheduledExecutorServicees = Executors.newScheduledThreadPool(1);

    work1();

    es.schedule(this::work2, 2,TimeUnit.SECONDS);
    // work1()이 끝난 다음 10초뒤에 work2를 개별 태스크로 스케줄함
    // main메서드가 아니라 실행되지는 않음
    es.shutdown();
}

```

v1은 스레드 풀 큐에 추가되며 나중에 차례가 되면 실행된다. 하지만 코드가 실행되면, 워커 스레드를 점유한 상태에서 10초간 잠들고 깨어난 뒤 work2를 실행하고 워커 스레드를 해제한다.

반면 v2는 work1을 실행하고 종료한다. 하지만 work2가 10초 뒤에 실행될 수 있도록 큐에 추가한다.

다른점은 v1은 자는 동안 스레드 자원을 점유하는 반면, v2는 다른 작업이 실행될 수 있도록 허용한다는 점이다. (스레드를 사용할 필요가 없이, 메모리만 조금 더 사용했다)

### 비동기 예외 처리

리액티브 형식의 비동기 API에서는 return 대신 기존 콜백이 호출되므로 예외가 발생했을 때 실행될 추가 콜백을 만들어 인터페이스를 바꿔야한다.

```java
void f(int x, Consumer<Integer> dealWithResult, Comsumer<Throwable> dealWithException);

// f의 바디는 다음을 수행할 수 있다.
dealWithException(e);
```

콜백이 여러 개면 이를 따로 제공하는 것보다, 한 객체로 이 메서드를 감싸는 것이 좋다.
자바 9 플로 API에서는 여러 콜백을 한 객체(네 개의 콜백을 각각 대표하는 네 메서드를 포함하는 Subscribe<T> 클래스)로 감싼다.

```java
void onComplete();
void onError(Throwable throwable);
void onNext(T item);
```

값이 있을 때(onNext), 도중 에러 발생(onError), 값을 다 소진하거나 에러가 발생해서 더이성 처리할 데이터가 없을 때(onComplete) 각각의 콜백이 호출됨. 이전 f에 적용하면 다음과 같다.

```java
void f(int x, Subscribe<Integer> s);
```

보통 이런 종류의 호출을 메시지 또는 이벤트라한다.

### CompletableFuture와 콤비네이터를 이용한 동시성

동시 코딩 작업을 Future 인터페이스로 생각하도록 유도하는 점이 Future 인터페이스의 문제다.

ComposableFuture가 아니라 CompletableFuture인 이유는? 뭘까?

- Future 실행해서 get()으로 결과를 얻을 수 있는 Callable로 만들어진다. 하지만 CompletableFuture는 실행할 코드 없이, Future 만들 수 있도록 하용하며, complete() 메서드를 이용해 나중에 어떤 값을 이용해 다른 스레드가 이를 완료할 수 있고, get()으로 값을 얻을 수 있도록 허용하기 때문이다.

```java
@Test
void run() throws Exception {
    finalExecutorServicees = Executors.newFixedThreadPool(10);
    int x = 1337;

    final CompletableFuture<Integer> a = new CompletableFuture<>();
    es.submit(() -> a.complete(f(x)));

    int b = g(x);
    System.out.println(a.get() + b);
    es.shutdown();
}
```

get()을 기다려야 하므로 자원을 낭비할 수 있다.

```java
@Test
void run2() throws ExecutionException, InterruptedException {
    finalExecutorServicees = Executors.newFixedThreadPool(10);
    int x = 1337;

    final CompletableFuture<Integer> a = new CompletableFuture<>();
    final CompletableFuture<Integer> b = new CompletableFuture<>();
    final CompletableFuture<Integer> c = a.thenCombine(b, Integer::sum);
    es.submit(() -> a.complete(f(x)));
    es.submit(() -> b.complete(g(x)));

    System.out.println(c.get());
    es.shutdown();
}
```

처음 두 작업이 끝나면 두 결과 모두에 fn을 적용하고 블록하지 않은 상태로 결과를 Future로 반환한다. Future a와 Future b의 결과를 알지 못한 상태에서 thenCombine은 두 연산이 끝났을 때 스레드 풀에서 실행된 연산을 만든다.
결과를 추가하는 세 번째 연산 c는 다른 두 작업이 끝날 때까지는 스레드에서 실행되지 않는다 (먼저 시작해서 블록되지 않는 점이 특징). 따라서 블록 문제가 생기지 않는다.

Future의 연산이 두 번째로 종료되는 상황에서 실제 필요한 스레드는 한 개지만, 스레드 풀의 두 스레드는 여전히 활성 상태다.

### 발행 - 구독, 리액티브 프로그래밍

리액티브 프로그래밍은 시간이 흐르면서 Future같은 객체를 통해 여러 결과를 제공한다.

자바 9에서는 java.util.concurrent.Flow 인터페이스에 pub-sub을 적용해 리액티브 프로그래밍을 제공한다. 플로 API는 간단하게 다음과 같이 정리할 수 있다.

- 구독자가 구독할 수 있는 발행자
- 이 연결을 구독이라한다.
- 이 연결을 이용해 메시지를 전송한다.

매 초마다 수천개의 메시지가 onNext로 전달된다면 어떤 상황이 발생할까? → **압력(pressure)**

출구로 추가될 공의 숫자를 제한하는 **역압력** 같은 기법이필요하다. 자바 9 플로 API에서는 발행자가 무한의 속도로 아이템을 방출하는 대신 요청했을 때만, 다음 아이템을 내보내도록 하는 request() 메서드를 제공한다.

### 역압력

정보의 흐름 속도를 억압력(흐름 제어)을 제어, 즉 Subscriber에서 Publisher로 정보를 요청해야할 필요가 있다.

Publisher는 여러 Subscriber를 갖고 있으므로, 억압력 요청이 한 연결에만 영향을 미쳐야 한다는 것이 문제가 될 수 있다. 자바 9 플로 API의 Subscriber인터페이스는 네 번째 메서드를 포함한다.

```java
void onSubscribe(Subscription subscription);
```

Publisher와 Subscriber 사이에 채널이 연결되면 첫 이벤트로 이 메서드가 호출된다. Subscription 객체는 Subscriber와 Publisher와 통신할 수 있는 메서드를 포함한다.

```java
interface Subscription {
	void cancel();
	void request(long n);
}
```

콜백을 통한 “역방향” 소통 효과에 주목하자. Publisher는 Subscription 객체를 만들어 Subscriber로 전달하면, Subscriber는 이를 이용해 Publisher로 정보를 보낼 수 있다.

### 실제 역압력의 간단한 형태

한 번의 한 개의 이벤트를 처리하도록 pub-sub 연결을 구성하려면 다음 작업이 필요

- Subscriber가 onSubscribe로 전달된 Subscription 객체를 subscription 같은 필드에 로컬로 저장
- Subscriber가 수많은 이벤트를 받지 않도록, onSubscribe, onNext, onError 마지막 동작에 channel.request(1)을 추가해 오직 한 이벤트만 요청한다.
- 요청을 보낸 채널에만 onNext, onError 이벤트를 보내도록 Publisher의 notifiyAllSubscriber 코드를 바꾼다. (보통 여러 Subsriber가 자신만의 속도를 유지할 수 있도록 Publisher는 새 Subscriber을 만들어 각 Subscriber와 연결)

역압력을 구현하려면 여러가지 장단점을 생각해야 한다

- 여러 Subscriberrk 있을 때 이벤트를 가장 느린 속도로 보낼 것인가? 아니면 각 Subscriber에게 보내지 않은 데이터를 저장할 별도의 큐를 가질것인가?
- 큐가 너무 커지면?
- Subscriber가 준비가 안 되었다면, 큐의 데이터를 폐기할 것인가?

# CompletableFuture

### Future 제한

여러 Future의 결과가 있을 때 이들의 의존성을 표현하기 어렵다.

- ex) 오래 걸리는 A라는 계산이 끝나면, 그 결과를 다른 오래 걸리는 계산 B로 전달 ..

다음과 같은 선언형 기능이 있다면 유용할 것이다

- 두 개의 비동기 계산 결과를 하나로 합친다. 서로 독립적이거나 두 번째 결과가 첫 번째 결과에 의존할 수 있는 상황이다.
- Future 집합이 실행하는 모든 태스크의 완료를 기다린다.
- 프로그램적으로 Future를 완료 시킨다.(비동기 동작에 수동으로 결과 제공)
- Future 집합에서 가장 빨리 완료되는 태스크를 기다렸다가, 결과를 얻는다
- Future 완료 동작에 반응한다 (즉, 결과를 기다리면서 블록되지 않고, 결과가 중비되었다는 알림을 받는 다음에 원하는 동작 수행)

### 타임아웃 효과적으로 사용

orTimeout 메서드는 지정된 시간이 지난 후에 TimeouException으로 완료하면서 또 다른 CompletableFuture를 반환할 수 있도록 내부적으로 ScheduledThreadExecutor를 활용한다.

이 메서드를 이용하면 계산 파이프라인을 연결하고 여기서 TimeoutException이 발생했을 때, 사용자가 쉽게 이해할 수 있는 메시지를 제공한다.

```java
Future<Double> futurePriceInUSD =
            CompletableFuture.supplyAsync(() -> shop.gerPrice(product))
                .thenCombine(
                    CompletableFuture.supplyAsync(() ->
                        exchangeService.getRate(Money.EUR, Money.USD)),
                    (price, rate) -> price * rate
                )
                .orTimeout(3, TimeUnit.SECONDS);
```

`completeOnTimeout` 을 이용해서 일시적으로 서비스 이용 불가일대 미리 지정된 값으로 사용할 수 있다.

```java
Future<Double> futurePriceInUSD =
    CompletableFuture.supplyAsync(() -> shop.gerPrice(product))
        .thenCombine(
            CompletableFuture.supplyAsync(() ->
                exchangeService.getRate(Money.EUR, Money.USD))
                .completeOnTimeout(DEFAULT_RATE, 1,TimeUnit.SECONDS),
            (price,rate) ->price*rate
)
        .orTimeout(3,TimeUnit.SECONDS);
```

### CompletableFuture의 종료에 대응하는 방법

서버 부하에서 네트워크 문제까지 다양한 지연 요소가 있다.

# 리액티브 프로그래밍

### 애플리케이션 수준의 리액티브

이벤트 스트림을 블록하지 않고 비동기 처리하는 것이 최신 멀티코어 cpu의 사용률을 극대화(내부적으로 경쟁하는 cpu의 스레드)할 수 있는 방법이다. 이 목표를 달성할 수 있도록, 리액티브 프레임크는 스레드를 퓨쳐, 액터,  일련의 콜백을 발생시키는 이벤트 루프 등과 공유하고 처리할 이벤트를 변환하고 관리함.

리액티브 프로그래밍은 리액티브 스트림을 사용하는 프로그래밍이다. 

- 리액티브 스트림: 잠재적으로 무한의 비동기 데이터를 순서대로, 블록하지 않는 역압력을 전제해 처리하는 표준 기술
- 역압력: 발행 - 구독 프로토콜에서 이벤트 스트림의 구독자가 발행자가 이벤트를 제공하는 속도보다 느린 속도로 이벤트를 소비하면서 문제가 발생하지 않도록 보장하는 장치

부하가 발생한 컴포넌트는 이벤트 발생 속도를 늦추라고 알리거나, 얼마나 많은 이벤트를 수신할 수 있는지 알리거나, 다른 데이터를 받기 전에 기존의 데이터를 처리하는 데 얼마나 시간이 걸리는지 업스트림 발행자에게 알릴 수 있어야함.

비동기를 이용하면 다른 느린 다운스트림 컴포넌트에 부하를 줄 가능성도 생긴다. 이런 상황을 방지할 수 있도록 역압력이나 제어 흐름 기법이 필요함. 이들을은 데이터 수신자가 스레드를 블록하지 않고 데이터 수신자가 처리할 수 없을 만큼 많은 데이터를 받는 일을 방지하는 프로토콜을 제공함.

### Flow

자바9에서 리액티브 프로그래밍을 제공하기 위해 추가됐다. 리액티브 스트림 프로젝트의 표준에 따라 pub-sub 모델을 지원할 수 있도록 Flow 클래스는 중첩된 인터페이스 네 개를 포함한다.

- Publisher
- Subscriber
- Subscription
- Processor

Publisher가 발행하면 Subscriber가 하나 혹은 여러 개를 소비하는데,  Subscription이 이 과정을 관리할 수 있도록 Flow 클래스 관련된 인터페이스와 정적 메서드를 제공함. (Publisher, Subscriber 사이의 제어 흐름, 억압력 관리)

- Publisher는 Subscription의 request 메서드에 정의된 개수 이하의 요소만 Subscriber에 전달 해야됨. Publisher는 지정된 개수보다 적은 요소를 onNext로 전달 가능. 동작이 성공적으로 끝나면 onComplete 호출, 문제가 생기면 onError를 호출해서 Subscription 종료
- Subscriber는 Publisher에게 요소를 받아 처리 할 수 있음을 알려야함. 이런 방식으로 Subscriber는 역압력을 행사할 수 있고, onComplete, onError 신호를 처리하는 상황에서 Subscriber는 Publisher나 Subscription의 어떤 메서드도 호출 불가하며 Subscription이 취소되었다고 가정해야됨. Subscriber는 Subscription.request() 메서드 호출이 없어도 언제든 종료 시그널 받을 준비가 되어야하며 Subscription.cancel()이 호출된 이후에라도 한 개 이상의 onNext받을 준비가 되어있어야함.
