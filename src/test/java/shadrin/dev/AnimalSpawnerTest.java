package shadrin.dev;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import shadrin.dev.animal.Animal;
import shadrin.dev.config.AnimalParams;
import shadrin.dev.config.AnimalSpawner;
import shadrin.dev.config.AnimalType;
import shadrin.dev.config.SimulationConfig;
import shadrin.dev.field.Island;
import shadrin.dev.field.Location;
import shadrin.dev.config.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

class AnimalSpawnerTest {
    private Island island;
    private AnimalSpawner spawner;
    private SimulationConfig simulationConfig;

    @BeforeEach
    void setUp(){
         island = Mockito.mock(Island.class);
         spawner = new AnimalSpawner();
         simulationConfig = Mockito.mock(SimulationConfig.class);
    }

    @DisplayName("успешная инициализация животных")
    @Test
    void successfulSpawnInit() {
        Map<AnimalType, Integer> initialCounts = new HashMap<>(Map.of(AnimalType.WOLF, 5));
        when(simulationConfig.getInitialCounts()).thenReturn(initialCounts);
        when(simulationConfig.getMaxPlantsPerCell()).thenReturn(200);

        int width = 4;
        int height = 4;
        Location[][] locations = new Location[width][height];
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                locations[x][y] = new Location(x, y);
            }
        }
        when(island.getLocations()).thenReturn(locations);
        when(island.getWIDTH()).thenReturn(width);
        when(island.getHEIGHT()).thenReturn(height);

        AnimalParams wolfParam = new AnimalParams(
                50.0,
                30,
                3,
                8.0,
                2,
                Set.of(Edible.HARE, Edible.HAMSTER) // diet
        );

        Map<AnimalType,AnimalParams> animalsMap = Map.of(AnimalType.WOLF,wolfParam);
        try (MockedStatic<SimulationConfig> mockedConfig = mockStatic(SimulationConfig.class)) {
            mockedConfig.when(SimulationConfig::getAnimalsMap).thenReturn(animalsMap);
            spawner.spawnInitial(island, simulationConfig);
        }

        //теперь надо посчитать всех волков, пройтись по всему острову
        int countWolfs = 0;
        for(int x = 0; x < island.getWIDTH(); x++){
            for(int y = 0; y < island.getHEIGHT(); y++){
                Location location = island.getLocations()[x][y];

                for (Animal animal : location.getAnimals()){
                    if (animal.getType() == AnimalType.WOLF){
                        countWolfs++;
                    }
                }
            }
        }
        assertEquals(5,countWolfs);
    }

    @DisplayName("создание растений," +
            "проверить что в каждой клетке острова появилось по 5 растений")
    @Test
    void successfulPlantInit() {
        int width = 4;
        int height = 4;
        Location[][] locations = new Location[width][height];
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                locations[x][y] = new Location(x, y);
            }
        }
        when(island.getLocations()).thenReturn(locations);
        when(island.getWIDTH()).thenReturn(width);
        when(island.getHEIGHT()).thenReturn(height);

        Map<AnimalType,Integer> emptyAnimalsMap = Collections.emptyMap();
        when(simulationConfig.getInitialCounts()).thenReturn(emptyAnimalsMap);
        when(simulationConfig.getMaxPlantsPerCell()).thenReturn(200);

        spawner.spawnInitial(island, simulationConfig);

        for (int x = 0; x < island.getWIDTH(); x++) {
            for (int y = 0; y < island.getHEIGHT(); y++) {
                Location location = island.getLocations()[x][y];
                assertEquals(5,location.getPlants().size(),
                        "В клетке [" + x + "," + y + "] должно быть 5 растений");
            }
        }
    }

    @DisplayName("'нулевые' типы животных пропускаются и не создаются, count = 0")
    @Test
    void emptyCountTypeAnimalsDoesntCreated(){
        Map<AnimalType, Integer> initialCounts = new HashMap<>(Map.of(
                AnimalType.WOLF, 5,
                AnimalType.FOX,0));
        when(simulationConfig.getInitialCounts()).thenReturn(initialCounts);
        when(simulationConfig.getMaxPlantsPerCell()).thenReturn(200);

        int width = 4;
        int height = 4;
        Location[][] locations = new Location[width][height];
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                locations[x][y] = new Location(x, y);
            }
        }
        when(island.getLocations()).thenReturn(locations);
        when(island.getWIDTH()).thenReturn(width);
        when(island.getHEIGHT()).thenReturn(height);

        AnimalParams wolfParam = new AnimalParams(
                50.0,
                30,
                3,
                8.0,
                2,
                Set.of(Edible.HARE, Edible.FOX) // diet
        );

        Map<AnimalType,AnimalParams> animalsMap = Map.of(AnimalType.WOLF,wolfParam);
        try (MockedStatic<SimulationConfig> mockedConfig = mockStatic(SimulationConfig.class)) {
            mockedConfig.when(SimulationConfig::getAnimalsMap).thenReturn(animalsMap);
            spawner.spawnInitial(island, simulationConfig);
        }


        int totalWolves = 0;
        int totalFoxes = 0;
        for(int x = 0; x < island.getWIDTH(); x++){
            for(int y = 0; y < island.getHEIGHT(); y++){
                Location location = island.getLocations()[x][y];
                for (Animal animal : location.getAnimals()){
                    if (animal.getType() == AnimalType.WOLF) totalWolves++;
                    if (animal.getType() == AnimalType.FOX) totalFoxes++;

                }
            }

        }
        assertEquals(5,totalWolves);
        assertEquals(0,totalFoxes);

    }

}
