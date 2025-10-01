package shadrin.dev.config;

import shadrin.dev.animal.Animal;
import shadrin.dev.field.Location;
import shadrin.dev.field.Island;
import shadrin.dev.plant.Plants;

import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

/**
 * вспомогательный сервис для создания животных и растений
 */
public class AnimalSpawner {
    public void spawnInitial(Island island, SimulationConfig simulationConfig) {
        Map<AnimalType, Integer> initialCounts = simulationConfig.getInitialCounts();
        for (AnimalType type : AnimalType.values()) {
            int count = initialCounts.getOrDefault(type, 0); //сколько особей нужно создать
            if (count <= 0) {
                continue;
            } // если для этого вида не нужно создавать, пропускаем

            AnimalParams params = SimulationConfig.getAnimalsMap().get(type);
            for (int i = 0; i < count; i++) {
                Animal animal = AnimalFactory.create(type, params, simulationConfig, island);

                //делаем случайные клетки
                int x = ThreadLocalRandom.current().nextInt(island.getWIDTH());
                int y = ThreadLocalRandom.current().nextInt(island.getHEIGHT());
                Location location = island.getLocations()[x][y];

                //сообщаем животному где оно
                animal.setCurrentLocation(location);
                location.getAnimals().add(animal); //кладем животное в клетку
            }
        }

        //инициализация растений
        for (int x = 0; x < island.getWIDTH(); x++) {
            for (int y = 0; y < island.getHEIGHT(); y++) {
                Location location = island.getLocations()[x][y];
                int initialPlants = 5;// кол-во растений в клетке для старта
                for (int i = 0; i < initialPlants; i++) {
                    Plants.grow(location, simulationConfig.getMaxPlantsPerCell());
                }

            }
        }
    }
}
