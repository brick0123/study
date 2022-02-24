package concurrency;

import java.time.Year;
import java.util.function.IntConsumer;
import javax.print.attribute.standard.RequestingUserName;
import org.junit.jupiter.api.Test;

public class CallbackStyleExample {

    @Test
    void name() {
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

    void f(int x, IntConsumer dealWithResult) {
    }

    void g(int x, IntConsumer dealWithResult) {
    }

    private static class Result {
        private int left;
        private int right;
    }
}
