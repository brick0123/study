package stream;


import stream.GroupingTest.CaloricLevel;

public class Dish {

    private Type type;
    private boolean isVegetarian;
    private int calories;

    public Dish(Type type, int calories) {
        this.type = type;
        this.calories = calories;
    }

    public Dish(Type type, boolean isVegetarian, int calories) {
        this.type = type;
        this.isVegetarian = isVegetarian;
        this.calories = calories;
    }

    public int getCalories() {
        return calories;
    }

    public void setCalories(int calories) {
        this.calories = calories;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "calories=" + calories;
    }

    public boolean isVegetarian() {
        return isVegetarian;
    }

    public CaloricLevel getCaloricType() {
        if (this.calories <= 10) {
            return CaloricLevel.DIET;
        }
        if (this.calories <= 30) {
            return CaloricLevel.NORMAL;
        }
        return CaloricLevel.FAT;
    }

    public enum Type {
        PIZZA, CHICKEN, PASTA
    }
}
