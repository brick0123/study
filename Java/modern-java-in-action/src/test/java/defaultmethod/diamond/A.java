package defaultmethod.diamond;

public interface A {

    default void hello() {
        System.out.println("A");
    }

}
