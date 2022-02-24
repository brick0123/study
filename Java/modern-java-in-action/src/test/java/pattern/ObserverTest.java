package pattern;

import static org.junit.platform.commons.util.StringUtils.*;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.util.StringUtils;

/**
 * 이벤트가 발생했을 때 한 객체가(주체) 다른 객체(옵저버)에 자동으로 알림을 보내는 상황
 */
public class ObserverTest {

    @Test
    void use() {
        final Feed feed = new Feed();
        feed.registerObserver(new Guardian());
        feed.registerObserver(new NyTimes());

        feed.notifyObservers("The queen said her favorite book is Modern Java in Action");

    }
}


interface Observer {

    void notify(String tweet);
}

class NyTimes implements Observer {

    @Override
    public void notify(String tweet) {
        if (isNotBlank(tweet) && tweet.contains("money")) {
            System.out.println("Breaking new in NY ! " + tweet);
        }
    }
}

class Guardian implements Observer {

    @Override
    public void notify(String tweet) {
        if (isNotBlank(tweet) && tweet.contains("queen")) {
            System.out.println("Yet more news from London..." + tweet);
        }
    }
}
// 주제 구현
interface Subject {

    void registerObserver(Observer o);
    void notifyObservers(String tweet);
}

class Feed implements Subject {

    private final List<Observer> observers = new ArrayList<>();

    @Override
    public void registerObserver(Observer o) {
        this.observers.add(o);
    }

    @Override
    public void notifyObservers(String tweet) {
        observers.forEach(o -> o.notify(tweet));
    }
}
