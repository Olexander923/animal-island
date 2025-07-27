package animal;

import plant.Plants;

import java.util.HashSet;
import java.util.Set;

public class Location {
    private final int x;
    private final int y;
    private final Set<Animal> animals = new HashSet<>();
    private final Set<Plants> plants = new HashSet<>();
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
