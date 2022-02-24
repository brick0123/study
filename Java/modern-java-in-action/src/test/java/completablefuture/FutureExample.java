package completablefuture;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.junit.jupiter.api.Test;

public class FutureExample {

    @Test
    void future() {
        final ExecutorService executor = Executors.newCachedThreadPool();
        final Future<Long> future = executor.submit(this::getSome); // 비동기 실행
        doSomething(); // 비동기적으로 실행중 처리

        try {
            final Long value = future.get(1, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            // 스레드 대기 중 인터럽트
        } catch (ExecutionException e) {
            // 계산 중 예외 발생
        } catch (TimeoutException e) {
            // Future가 완료되기 전 타임아웃
        }

        // get 메서드를 호출했을 때, 이미 계산이 완료되어 준비되었다면
        // 결과를즉시 반환하지만, 준비되지 않았다면 스레드를 블록시킨다.
        // 작업이 끝나지 않을 수 있으므로, 최대 타임아웃 시간을 설정해야한다.
    }

    Long getSome() {
        return 1L;
    }

    void doSomething() {
        System.out.println("FutureExample.doSomething");
    }
}
