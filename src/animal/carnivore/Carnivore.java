package animal.carnivore;

import animal.Animal;
import animal.Eatable;
import animal.Location;
import config.*;
import field.Island;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

import static java.lang.Math.min;

public abstract class Carnivore extends Animal {
    public Carnivore(SimulationConfig config, Island island, AnimalType type, double weight, int maxAnimalsPerCell, int speedMoving, double foodToSaturate, Set<Edible> diet) {
        super(config, island, type, weight, maxAnimalsPerCell, speedMoving, foodToSaturate, diet);
    }

    @Override
    public synchronized void eat() {
        Set<Edible> diet = getDiet();
        //получаем клетку текущего нахождения орла
        Location carnivore = this.getCurrentLocation();
        try {

            //получаем список всех кого можно съесть
            List<Eatable> candidates = new ArrayList<>();
            candidates.addAll(carnivore.getPlants());
            candidates.addAll(carnivore.getAnimals());
            //оставляем только тех, кого можем съесть согласно данным
            candidates.removeIf(c -> !diet.contains(c.getEdible()));
            //проходимся по всем кандидатам, пока не найдется подходящий
            for (Eatable candidate : candidates) {
                double chance = EcosystemRules.getProbabilityOfEating(this.getType(), candidate.getEdible());
                //todo , временный вывод для проверки вероятности поедания,удалить потом
                System.out.println("Chance for " + this.getType() + " to eat " + candidate.getEdible() + ": " + chance);
                if (ThreadLocalRandom.current().nextDouble() < chance) {
                    //съедает
                    double foodWeight = candidate.getWeight();
                    double newSatiety = min(getSatiety() + foodWeight, this.getFoodToSaturate());
                    setSatiety(newSatiety);
                    candidate.removeFrom(carnivore);
                        //за один такт хватает одного куска — выходим
                        break;
                    }
                }
        } catch (IllegalStateException ex) {
            System.err.println("Extraction was runaway");
        }
    }

    @Override
    public synchronized void multiply() {
        System.out.println("Animal " + this.getType() + " is multiplying...");
        //получаем текущее положение в клетке
        Location eagleLocation = this.getCurrentLocation();

        //получаем список всех видов животных в клетке
        List<Animal> multiplyCandidates = new ArrayList<>();
        multiplyCandidates.addAll(eagleLocation.getAnimals());

//        //оставляем только тех, с кем можно размножаться согласно данным
//        multiplyCandidates.removeIf(c -> !c.getType().equals(this.getType()));
//        if (multiplyCandidates.size() >= 2) {
//            return;
//        }

        long countOfPartners = getCurrentLocation().getAnimals().stream()
                .filter(a -> a!=this && a.getType() == this.getType()).count();
        if (countOfPartners > 1) return;

        // проверяем есть ли еще место в клетке для новых детенышей
        AnimalParams config = SimulationConfig.getAnimalsMap().get(this.getType());
        int cubsQuantity = config.getCubsPerBirth();
        if (eagleLocation.getAnimals().size() + cubsQuantity > getMaxAnimalsPerCell()) {
            return; //места нет в клетке
        }
        //теперь проверка сытости для размножения
        if (getSatiety() < 0.5 * getFoodToSaturate()) {
            return;//слишком голодны для размножения
        }
        //создание детенышей
        for (int i = 0; i < cubsQuantity; i++) {
            Animal cub = switch (this.getType()) {
                case WOLF -> new Wolf(this.getConfig(), this.getIsland(), this.getType(), this.getWeight(),
                        this.getMaxAnimalsPerCell(), this.getSpeedMoving(), this.getFoodToSaturate(), this.getDiet());

                case BEAR -> new Bear(this.getConfig(), this.getIsland(), this.getType(), this.getWeight(),
                        this.getMaxAnimalsPerCell(), this.getSpeedMoving(), getFoodToSaturate(), getDiet());

                case EAGLE -> new Eagle(getConfig(), getIsland(), getType(), getWeight(), getMaxAnimalsPerCell(),
                        getSpeedMoving(), getFoodToSaturate(), getDiet());

                case FOX -> new Fox(getConfig(), getIsland(), getType(), getWeight(), getMaxAnimalsPerCell(),
                        getSpeedMoving(), getFoodToSaturate(), getDiet());

                default -> throw new IllegalStateException("Unexpected value: " + this.getType());
            };
            eagleLocation.getAnimals().add(cub);
            //уменьшаем сытость родителя
            setSatiety(getSatiety() - 0.5 * getFoodToSaturate());
            System.out.println("Quantity carnivore cubs: " + cubsQuantity);
        }
    }


    @Override
    public synchronized void chooseDirectionOfMovement() {
        System.out.println("Animal " + this.getType() + " is moving...");
        int speed = this.getSpeedMoving();//получаем скорость из текущего объекта
        //получаем текущее положение в клетке
        Location currentLocation = this.getCurrentLocation();
        int x = currentLocation.getX();
        int y = currentLocation.getY();

        //Собираем все соседние клетки в радиусе перемещения speedMoving
        List<Location> neighborsCell = new ArrayList<>();
        for (int dx = -speed; dx <= speed; dx++) {
            for (int dy = -speed; dy <= speed; dy++) {
                if (dx == 0 && dy == 0) {
                    continue;
                }//пропускаем текущую клетку
                int newX = x + dx;
                int newY = y + dy;
                // проверяем, что (newX, newY) в пределах острова
                if (newX >= 0 && newX < this.getIsland().getWIDTH() &&
                        newY >= 0 && newY < this.getIsland().getHEIGHT()) {
                    Location neighbor = this.getIsland().getLocations()[newX][newY];// добавляем neighbor в список соседних клеток
                    if (!neighbor.isWater() && neighbor.getAnimals().size() < this.getMaxAnimalsPerCell()) {
                        neighborsCell.add(neighbor);
                    }
                }
            }
        }
        //выбор случайно клетки с проверкой
        if (!neighborsCell.isEmpty()) {
            Location newLocation = neighborsCell.get(ThreadLocalRandom.current().nextInt(neighborsCell.size()));
            this.setCurrentLocation(newLocation);
            currentLocation.getAnimals().remove(this);
            newLocation.getAnimals().add(this);
        }

    }


}
