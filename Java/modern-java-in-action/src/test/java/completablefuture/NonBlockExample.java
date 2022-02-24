package completablefuture;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class NonBlockExample {

    List<Shop> shops = List.of(
        new Shop("BestPrice"),
        new Shop("MyFavorite"),
        new Shop("Hello")
    );

    @Test
    @DisplayName("1초의 대기시간이 있으므로 3초의 대기시간이 걸린다")
    void v1() {
        final long start = System.nanoTime();
        System.out.println(findPricesV1("myPhone27S"));
        long duration =  (System.nanoTime() - start) / 1_000_000;
        System.out.println("Done in " + duration + "ms");
    }

    @Test
    @DisplayName("병렬 처리")
    void v2() {
        final long start = System.nanoTime();
        System.out.println(findPricesV2("myPhone27S"));
        long duration =  (System.nanoTime() - start) / 1_000_000;
        System.out.println("Done in " + duration + "ms");
    }

    @Test
    @DisplayName("CompletableFuture 활용")
    void v3() {
        final long start = System.nanoTime();
        System.out.println(findPricesV3("myPhone27S"));
        long duration =  (System.nanoTime() - start) / 1_000_000;
        System.out.println("Done in " + duration + "ms");
    }

    public List<String> findPricesV1(String product) {
        return shops.stream()
            .map(shop -> String.format("%s price is %.2f", shop.getName(), shop.getPrice(product)))
            .collect(Collectors.toList());
    }

    public List<String> findPricesV2(String product) {
        return shops.stream()
            .parallel()
            .map(shop -> String.format("%s price is %.2f", shop.getName(), shop.getPrice(product)))
            .collect(Collectors.toList());
    }


    /**
     * 모든 CompletableFuture에 join을 호출해서 모든 동작이 끝나길 기다린다
     * CompletableFuture의 join은 Future 인터페이스의 get 메서드와 같은 의미.
     * 다만 join은 아무 예외도 발생하지 않는다. 따라서, map의 람다 표현식을 try / catch가 필요 없다.
     */
    public List<String> findPricesV3(String product) {
        final List<CompletableFuture<String>> priceFutures = shops.stream()
            .map(shop -> CompletableFuture.supplyAsync(() ->
                String.format("%s price is %.2f", shop.getName(), shop.getPrice(product))))
            .collect(Collectors.toList());

        // 두 개의 스트림을 사용한 것에 주목하자
        // 스트림은 게으른 특성이 있으므로, 하나로 처리했으면
        // 모든 가격 정보 요청 동작이 동기적, 순차적으로 이루어지는 결과가 된다.
        return priceFutures.stream()
            .map(CompletableFuture::join)
            .collect(Collectors.toList());
    }

    static class Shop {

        private String name;

        public Shop(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
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

}
