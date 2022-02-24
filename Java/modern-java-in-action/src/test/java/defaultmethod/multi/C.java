package defaultmethod.multi;

public class C extends D implements B, A{

    // 시그니처가 같은 default 메서드

    /**
     * 1. 클래스가 항상 이긴다. 클래스나 슈퍼클래스에서 정의한 메서드가 디폴트 메서드보다 우선권
     *
     * 2. 1번 규칙 이외 상황에서는 서브인터페이스가 이긴다. 상속관계를 갖는 인터페이스에서
     * 같은 시그니처를 갖는 메서드를 정의할 때는 서브 인터페이스가 이긴다.
     * 즉 B가 A를 상속받는다면 B가 이긴다.
     *
     * 3. 순위가 결정나지 않았으면, 여러 인터페이스를 상속받는 클래스가 명시적으로 디폴트 메서드를
     * 오버라이딩하고 호출해야한다.
     */

    public static void main(String[] args) {
        new C().hello();
    }
}
