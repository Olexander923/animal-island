package shadrin.dev.animal;

import shadrin.dev.config.AnimalType;
import shadrin.dev.config.Edible;
import shadrin.dev.config.SimulationConfig;
import shadrin.dev.field.Island;
import shadrin.dev.field.Location;

import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

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
        //установил случайное значение сытости
        this.satiety = ThreadLocalRandom.current().nextDouble(foodToSaturate*0.3,foodToSaturate*0.8);
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

    /**
     * метод для уменьшения сытости в каждом такте(-10% за такт)
     */
    public void decreaseSatiety(){
        double newSatiety = getSatiety() - (getFoodToSaturate() * 0.1);
        setSatiety(Math.max(newSatiety,0));
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
