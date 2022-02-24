package completablefuture;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class AsyncExample {

    @Test
    void v1() {
        final Shop shop = new Shop("BestShop");
        final long start = System.nanoTime();
        final Future<Double> futurePrice = shop.getPriceAsyncV1("my favorite product");
        final long invocationTime = (System.nanoTime() - start) / 1_000_000;
        System.out.println("Invocation returned after " + invocationTime + " msecs");

        doSomething(); // 다른 상점 검색 등 다른 작업 수행
        try {
            double price = futurePrice.get();
            System.out.println("price is " + price);
        } catch (Exception e) {
            throw new RuntimeException();
        }

        final long retrievalTime = (System.nanoTime() - start) / 1_000_000;
        System.out.println("Price returned after " + retrievalTime + " msecs");
    }

    @Test
    @DisplayName("에러 처리, 비동기 스레드에만 에러가 발생한 경우?")
    void v2() {
        final Shop shop = new Shop("BestShop");
        final Future<Double> futurePrice = shop.getPriceAsyncV2("my favorite product");
        // ..
    }

    @Test
    @DisplayName("supplier를 이용해서 간결한 처리")
    void v3() {
        final Shop shop = new Shop("BestShop");
        final Future<Double> futurePrice = shop.getPriceAsyncV3("my favorite product");

        // Supplier를 실행해서 비동기적으로 결과를 생성한다.
        // ForkJoinPool의 Executor 중 하나가 Supplier를 실행한다
        // Executor를 선택적으로 전달할 수도 있다.

        // ..
    }

    static class Shop {

        private String name;

        public Shop(String name) {
            this.name = name;
        }

        public Future<Double> getPriceAsyncV1(String product) {
            CompletableFuture<Double> futurePrice = new CompletableFuture<>();
            new Thread(() -> {
                double price = calculatePrice(product);
                futurePrice.complete(price);
            }).start();

            return futurePrice;
        }

        public Future<Double> getPriceAsyncV2(String product) {
            CompletableFuture<Double> futurePrice = new CompletableFuture<>();
            new Thread(() -> {
                try {
                    double price = calculatePrice(product);
                    futurePrice.complete(price);
                } catch (Exception e) {
                    futurePrice.completeExceptionally(e);
                }
            }).start();

            return futurePrice;
        }

        public Future<Double> getPriceAsyncV3(String product) {
            return CompletableFuture.supplyAsync(() -> calculatePrice(product));

        }

        public double getPrice(String product) {
            return calculatePrice(product);
        }

        private double calculatePrice(String product) {
            delay();
            return ThreadLocalRandom.current().nextDouble() * product.charAt(0) + product.charAt(1);
        }

        public void delay() {
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void doSomething() {
        System.out.println("AsyncExample.doSomething");
    }


}
