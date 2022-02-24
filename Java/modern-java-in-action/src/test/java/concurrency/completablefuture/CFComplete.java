package concurrency.completablefuture;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.junit.jupiter.api.Test;

public class CFComplete {

    @Test
    void run() throws Exception {
        final ExecutorService es = Executors.newFixedThreadPool(10);
        int x = 1337;

        final CompletableFuture<Integer> a = new CompletableFuture<>();
        es.submit(() -> a.complete(f(x)));

        int b = g(x);
        System.out.println(a.get() + b);
        es.shutdown();
    }

    @Test
    void run2() throws ExecutionException, InterruptedException {
        final ExecutorService es = Executors.newFixedThreadPool(10);
        int x = 1337;

        final CompletableFuture<Integer> a = new CompletableFuture<>();
        final CompletableFuture<Integer> b = new CompletableFuture<>();
        final CompletableFuture<Integer> c = a.thenCombine(b, Integer::sum);
        es.submit(() -> a.complete(f(x)));
        es.submit(() -> b.complete(g(x)));

        System.out.println(c.get());
        es.shutdown();
    }

    private int f(int x) {
        System.out.println(Thread.currentThread());
        return x;
    }

    private int g(int x) {
        System.out.println(Thread.currentThread());
        return x + 1;
    }
}
