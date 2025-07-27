package animal;

import config.AnimalType;
import config.Edible;
import animal.Location;
import config.SimulationConfig;
import field.Island;

import java.util.Set;

public abstract class Animal implements Eatable {
    private final SimulationConfig config;
    private final Island island;
    private final double weight;//Вес одного животного
    private final int maxAnimalsPerCell;//Максимальное количество животных этого вида на одной клетке
    private final int speedMoving;//Скорость перемещения, не более чем, клеток за ход
    private final double foodToSaturate;//Сколько килограммов пищи нужно животному для полного насыщения
    private final AnimalType type;
    protected final Set<Edible> diet;//хранит типы добычи для каждого животного
    //protected Set<AnimalType> reproduction;

    private double satiety;//уровень насыщения животного(насколько голодный)
    private Location currentLocation;//местоположение текущее
    private boolean isAlive;

    public Animal(SimulationConfig config, Island island, AnimalType type, double weight, int maxAnimalsPerCell, int speedMoving, double foodToSaturate, Set<Edible> diet) {
        this.config = config;
        this.island = island;
        this.type = type;
        this.weight = weight;
        this.maxAnimalsPerCell = maxAnimalsPerCell;
        this.speedMoving = speedMoving;
        this.foodToSaturate = foodToSaturate;
        this.diet = diet;
        this.satiety = 0;
        this.isAlive = true;
    }


    public abstract void eat();
    public abstract void multiply();
    public abstract void chooseDirectionOfMovement();

    @Override
    public Edible getEdible() {
        return Edible.valueOf(this.getClass().getSimpleName().toUpperCase());
    }

    @Override
    public double getWeight() {
        return this.weight;   // поле final уже есть
    }

    @Override
    public void removeFrom(Location location) {
       location.getAnimals().remove(this);
       this.isAlive = false;
    }

    public int getMaxAnimalsPerCell() {
        return maxAnimalsPerCell;
    }

    public int getSpeedMoving() {
        return speedMoving;
    }

    public double getFoodToSaturate() {
        return foodToSaturate;
    }

    public boolean isAlive() {
        return isAlive;
    }

    public double getSatiety() {
        return satiety;
    }

    public Set<Edible> getDiet() {
        return diet;
    }


    public Location getCurrentLocation() {
        return currentLocation;
    }

    public void setCurrentLocation(Location currentLocation) {
        this.currentLocation = currentLocation;
    }

    public AnimalType getType() { return type; }

    public void setSatiety(double satiety) {
        this.satiety = satiety;
    }

    public SimulationConfig getConfig() {
        return config;
    }

    public Island getIsland() {
        return island;
    }

    public void setAlive(boolean alive) {
        isAlive = alive;
    }
}
