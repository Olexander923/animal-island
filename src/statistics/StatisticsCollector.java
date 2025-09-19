package statistics;

import animal.Animal;
import animal.Location;
import config.AnimalType;
import field.Island;

import java.util.EnumMap;
import java.util.Map;
import java.util.Set;


/**
 * подсчитывает количество особей каждого вида и выводит таблицу
 */
public class StatisticsCollector {

    /**
     * подсчитывает сколько особей каждого вида находиться на острове
     * @return результат, заполненная мапа
     */
    public Map<AnimalType,Integer> collectAnimalCounts(Island island) {
        Map<AnimalType, Integer> animalCount = new EnumMap<>(AnimalType.class);  //мапа для подсчета животных в каждой клетке
        for (AnimalType type : AnimalType.values()) {
            animalCount.put(type, 0);
        }
        Location[][] locations = island.getLocations();
        for (int x = 0; x < island.getWIDTH(); x++) {
            for (int y = 0; y < island.getHEIGHT(); y++) {
                Set<Animal> animals = locations[x][y].getAnimals();
                for (Animal animal : animals) {
                    if (animal.isAlive()) {
                        animalCount.merge(animal.getType(), 1, Integer::sum);
                    }
                }
            }
            System.out.println("Animal counts: " + animalCount);

        }
        return animalCount;
    }

    /**
     * подсчитывает сколько растений находиться на острове
     * @return результат, общее число растений
     */
    public int collectPlantsCounts(Island island) {
        int plantCount = 0;
        Location[][] locations = island.getLocations();
        for (int x = 0; x < island.getWIDTH(); x++) {
            for (int y = 0; y < island.getHEIGHT(); y++) {
                 plantCount += locations[x][y].getPlants().size();
            }
        }
        System.out.println("Plants counts: " + plantCount);
        return plantCount;
    }
}
