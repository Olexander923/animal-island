package config;

import java.util.Set;

public class AnimalParams { //TODO установить библиотку SnakeYAML/Jackson для YAML
    private final double weight;//Вес одного животного
    private final int maxAnimalsPerCell;//Максимальное количество животных этого вида на одной клетке
    private final int speedMoving;//Скорость перемещения, не более чем, клеток за ход
    private final double foodToSaturate;//Сколько килограммов пищи нужно животному для полного насыщения
    private int cubsPerBirth;//еоличество детенышей при рождении
    protected final Set<Edible> diet;//хранит типы добычи для каждого животного

    public AnimalParams(double weight, int maxAnimalsPerCell, int speedMoving, double foodToSaturate,int cubsPerBirth, Set<Edible> diet) {
        this.weight = weight;
        this.maxAnimalsPerCell = maxAnimalsPerCell;
        this.speedMoving = speedMoving;
        this.foodToSaturate = foodToSaturate;
        this.cubsPerBirth = cubsPerBirth;
        this.diet = diet;
    }

    public double getWeight() {
        return weight;
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

    public Set<Edible> getDiet() {
        return diet;
    }

    public int getCubsPerBirth() {
        return cubsPerBirth;
    }
}
