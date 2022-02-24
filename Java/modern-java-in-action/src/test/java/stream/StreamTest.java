package stream;

import java.util.stream.IntStream;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;

public class StreamTest {

    @Test
    void infinity() {
        Stream.iterate(0, n -> n + 2)
            .limit(10)
            .forEach(System.out::println);
        // 0, 2.. 10
    }

    @Test
    void fibonacci() {
        Stream.iterate(new int[]{0, 1}, t -> new int[]{t[1], t[0] + t[1]})
            .limit(20)
            .forEach(t -> System.out.println("(" + t[0] + "," + t[1] + ")"));

    }

    @Test
    void java9_predicate() {
        IntStream.iterate(0, n -> n < 100, n -> n + 4)
            .skip(1)
            .forEach(System.out::println);
    }
    @Test
    void stream_predicate_2() {
        IntStream.iterate(0, n -> n + 4)
            .takeWhile(n -> n < 100)
            .forEach(System.out::println);

    }

    @Test
    void name() {
    }
}
