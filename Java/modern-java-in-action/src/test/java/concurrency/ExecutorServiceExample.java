package concurrency;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import org.junit.jupiter.api.Test;

public class ExecutorServiceExample {

    @Test
    void run() throws Exception {
        int x = 1337;
        final ExecutorService es = Executors.newFixedThreadPool(2);

        final Future<Integer> y = es.submit(() -> f(x));
        final Future<Integer> z = es.submit(() -> g(x));
        // submit 같은 불필요한 코드로 오염되었다.
        // 명시적으로 반복으로 병렬화를 수행하던 코드를 이용해 내부 반복으로 바꾼 것처럼
        // 비슷한 방법으로 문제를 해결해야한다.

        System.out.println(y.get() + z.get());
    }

    private int f(int x) {
        return x;
    }

    private int g(int x) {
        return x;
    }
}
