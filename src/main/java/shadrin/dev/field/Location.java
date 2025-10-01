package shadrin.dev.field;
import shadrin.dev.animal.Animal;
import shadrin.dev.plant.Plants;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class Location {
    private final int x;
    private final int y;
    private final Set<Animal> animals = ConcurrentHashMap.newKeySet();
    private final Set<Plants> plants = ConcurrentHashMap.newKeySet();
    private boolean isWater;//является ли клетка водой

    public Location(int x, int y) {
        this.x = x;
        this.y = y;
        this.isWater = false;
    }

    public Set<Animal> getAnimals() {
        return animals;
    }

    public Set<Plants> getPlants() {return plants;}

    public void setWater(boolean water) {
        isWater = water;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public boolean isWater() {
        return isWater;
    }
}
