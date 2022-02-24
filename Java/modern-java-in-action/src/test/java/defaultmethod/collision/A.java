package defaultmethod.collision;

public interface A {

    default void hello() {
        System.out.println("A");
    }

}
