package defaultmethod.multi;

public interface Rotatable {

    void setRotationAngle(int angleInDegrees);

    int getRotationAngle();

    default void rotateBy(int angleInDegrees) {
        setRotationAngle((getRotationAngle() + angleInDegrees) % 360);
    }

}
