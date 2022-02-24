package defaultmethod.collision;

public class C implements A, B{

    /**
     * compile error! must override it
     */
    @Override
    public void hello() {
        A.super.hello();
    }

    public static void main(String[] args) {
        new C().hello();
    }
}
