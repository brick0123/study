package concurrency;

import org.junit.jupiter.api.Test;

public class ThreadExample {

    @Test
    void test() throws InterruptedException {
        int x = 1337;
        final Result result = new Result();

        Thread t1 = new Thread(() -> result.left = f(x));
        Thread t2 = new Thread(() -> result.right = f(x));

        t1.start();
        t2.start();
        t1.join();
        t2.join();

        System.out.println(result.left + result.right);
    }

    private int f(int x) {
        return x;
    }

    private static class Result {
        private int left;
        private int right;
    }

}
