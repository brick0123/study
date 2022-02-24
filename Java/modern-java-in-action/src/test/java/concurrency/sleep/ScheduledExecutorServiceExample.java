package concurrency.sleep;

import java.sql.Time;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Test;

public class ScheduledExecutorServiceExample {

    @Test
    void v1() throws InterruptedException {
        work1();
        TimeUnit.SECONDS.sleep(1);
        work2();
    }

    @Test
     void v2() {
        final ScheduledExecutorService es = Executors.newScheduledThreadPool(1);

        work1();
        es.schedule(this::work2, 1, TimeUnit.SECONDS);
        // work1()이 끝난 다음 10초뒤에 work2를 개별 태스크로 스케줄함
        // 현재 main 메서드가 아니라 실행되지는 않음.
        es.shutdown();
    }

    public void work1() {
        System.out.println(Thread.currentThread().getName());
        System.out.println("Work1");
    }

    public void work2() {
        System.out.println(Thread.currentThread().getName());
        System.out.println("Work2");
    }

}
