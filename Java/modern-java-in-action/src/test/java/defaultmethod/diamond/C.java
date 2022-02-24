package defaultmethod.diamond;

public interface C extends A {

    // C의 추상 메서드가 A의 디폴트 메서드보다 우선권을 갖는다.
    void hello();
}
