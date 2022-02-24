package concurrency.flow;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Flow.Publisher;
import java.util.concurrent.Flow.Subscriber;
import java.util.concurrent.Flow.Subscription;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGenerator.Simple;
import org.junit.jupiter.api.Test;

public class SimpleCellTest {

    @Test
    void exam() {
        final SimpleCell c2 = new SimpleCell("c2");
        final SimpleCell c1 = new SimpleCell("c1");

        // c1이나 c2의 값이 바뀌었을 때 c3가 두 값을 더하도록 지정하려면?
        // c1과 c2에 이벤트개 발생했을 때 c3를 구독하도록 만들어야 한다. (Publisher)
        // Publisher는 통신할 구독자를 인수로 받는다.
    }

    @Test
    @DisplayName("c1이나 c2의 값이 바뀌었을 때, c3가 두 값을 더하도")
    void run() {
        final SimpleCell c2 = new SimpleCell("c2");
        final SimpleCell c1 = new SimpleCell("c1");
        final SimpleCell c3 = new SimpleCell("c3");

        c1.subscribe(c3);

        c1.onNext(10); // C1의 값을 10으로 갱신
        c2.onNext(20); // C2의 값을 20으로 갱신

        // c3 = c1 + c2은 어떻게 구현할까?
    }

    @Test
    void run2() {
        final ArithmeticCell c3 = new ArithmeticCell("c3");
        final SimpleCell c2 = new SimpleCell("c2");
        final SimpleCell c1 = new SimpleCell("c1");

        c1.subscribe(c3);
        c2.subscribe(c3);

        c1.onNext(10);
        c2.onNext(20);
        c1.onNext(15);
    }

    static class SimpleCell implements Publisher<Integer>, Subscriber<Integer> {

        private int value = 0;
        private String name;
        private List<Subscriber> subscribers = new ArrayList<>();

        public SimpleCell(String name) {
            this.name = name;
        }

        @Override
        public void subscribe(Subscriber<? super Integer> subscriber) {
            subscribers.add(subscriber);
        }

        private void notifyAllSubscribers() {
            subscribers.forEach(subscriber -> subscriber.onNext(this.value));
        }

        @Override
        public void onSubscribe(Subscription subscription) {

        }

        @Override
        public void onNext(Integer item) {
            this.value = item; // 구독한 셀에 새 값이 생겼을 때 값을 갱신해서 반응함
            System.out.println(this.name + ":" + this.value);
            notifyAllSubscribers(); // 값이 갱신되었음을 모든 구독자에게 알림
        }

        @Override
        public void onError(Throwable throwable) {

        }

        @Override
        public void onComplete() {

        }
    }

    static class ArithmeticCell extends SimpleCell {

        private int left;
        private int right;

        public ArithmeticCell(String name) {
            super(name);
        }

        public void setLeft(int left) {
            this.left = left;
            onNext(left + this.right); // 셀 값을 갱신하고 모든 구독자에 알림
        }

        public void setRight(int right) {
            this.right = right;
            onNext(right + this.left); // 셀 값을 갱신하고 모든 구독자에 알림
        }
    }
}
