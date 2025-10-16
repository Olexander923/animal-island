package shadrin.dev;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import shadrin.dev.animal.Animal;
import shadrin.dev.config.AnimalType;
import shadrin.dev.config.SimulationConfig;
import shadrin.dev.field.Island;
import shadrin.dev.field.Location;
import shadrin.dev.plant.Plants;
import shadrin.dev.statistics.StatisticsCollector;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@DisplayName("юнит-тесты для проверки подсчета статистики")
public class StatisticsCollectorTests {
    private SimulationConfig simulationConfig;
    private Island island;
    private StatisticsCollector statisticsCollector;

    @BeforeEach
    void setUp(){
        simulationConfig = Mockito.mock(SimulationConfig.class);
        island = Mockito.mock(Island.class);
        statisticsCollector = new StatisticsCollector();
    }

    @DisplayName("успешный подсчет статистики животных")
    @Test
    void successfulAnimalsCount(){
        Animal mockWolf = mock(Animal.class);

        Map<AnimalType, Integer> initialCounts = new EnumMap<>(Map.of(AnimalType.WOLF, 5));
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

        when(mockWolf.getType()).thenReturn(AnimalType.WOLF);
        when(mockWolf.isAlive()).thenReturn(true);
        locations[1][1].getAnimals().add(mockWolf);
        System.out.println("Животных в клетке [1][1]: " + locations[1][1].getAnimals().size());
        System.out.println("Тип животного: " + mockWolf.getType());
        Map<AnimalType,Integer> result = statisticsCollector.collectAnimalCounts(island);

        assertEquals(1,result.get(AnimalType.WOLF));
    }

    @DisplayName("животные умерли")
    @Test
    void unsuccessfullyCountAnimalsDied(){
        Animal mockWolf = mock(Animal.class);

        Map<AnimalType, Integer> initialCounts = new EnumMap<>(Map.of(AnimalType.WOLF, 5));
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

        when(mockWolf.getType()).thenReturn(AnimalType.WOLF);
        when(mockWolf.isAlive()).thenReturn(false);
        locations[1][1].getAnimals().add(mockWolf);
        Map<AnimalType,Integer> result = statisticsCollector.collectAnimalCounts(island);

        assertEquals(0,result.getOrDefault(AnimalType.WOLF,0));
    }

    @DisplayName("несколько животных одного типа")
    @Test
    void severalAnimalsTheSameTypes(){
        Animal firstWolf = mock(Animal.class);
        Animal secondWolf = mock(Animal.class);
        Animal thirdWolf = mock(Animal.class);

        Map<AnimalType, Integer> initialCounts = new EnumMap<>(Map.of(AnimalType.WOLF, 5));
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

        when(firstWolf.getType()).thenReturn(AnimalType.WOLF);
        when(secondWolf.getType()).thenReturn(AnimalType.WOLF);
        when(thirdWolf.getType()).thenReturn(AnimalType.WOLF);

        when(firstWolf.isAlive()).thenReturn(true);
        when(secondWolf.isAlive()).thenReturn(true);
        when(thirdWolf.isAlive()).thenReturn(true);
        locations[1][1].getAnimals().add(firstWolf);
        locations[1][2].getAnimals().add(secondWolf);
        locations[1][3].getAnimals().add(thirdWolf);

        Map<AnimalType,Integer> result = statisticsCollector.collectAnimalCounts(island);
        assertEquals(3,result.get(AnimalType.WOLF));
    }

    @DisplayName("разные типы животных: лиса, заяц, волк")
    @Test
    void differentTypesOfAnimals(){
        Animal wolf = mock(Animal.class);
        Animal hare = mock(Animal.class);
        Animal fox = mock(Animal.class);

        Map<AnimalType, Integer> initialCounts = new EnumMap<>(Map.of(
                AnimalType.WOLF, 5,
                AnimalType.HARE,5,
                AnimalType.FOX,5));
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

        when(wolf.getType()).thenReturn(AnimalType.WOLF);
        when(hare.getType()).thenReturn(AnimalType.HARE);
        when(fox.getType()).thenReturn(AnimalType.FOX);

        when(wolf.isAlive()).thenReturn(true);
        when(hare.isAlive()).thenReturn(true);
        when(fox.isAlive()).thenReturn(true);
        locations[1][1].getAnimals().add(wolf);
        locations[1][1].getAnimals().add(hare);
        locations[1][1].getAnimals().add(fox);

        Map<AnimalType,Integer> result = statisticsCollector.collectAnimalCounts(island);
        assertEquals(1,result.get(AnimalType.WOLF));
        assertEquals(1,result.get(AnimalType.HARE));
        assertEquals(1,result.get(AnimalType.FOX));
    }

    @DisplayName("пустой остров")
    @Test
    void emptyIsland(){

        Map<AnimalType, Integer> initialCounts = new EnumMap<>(Map.of(AnimalType.WOLF, 5));
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

        Map<AnimalType,Integer> result = statisticsCollector.collectAnimalCounts(island);
        for (AnimalType type : AnimalType.values()) {
            assertEquals(0, result.getOrDefault(type,0));

        }
    }

    @DisplayName("успешный подсчет статистики по растениям")
    @Test
    void successfullyPlantsCount(){
        Plants plants = Mockito.mock(Plants.class);
        int width = 4;
        int height = 4;
        Location[][] locations = new Location[width][height];
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                locations[x][y] = new Location(x, y);
                locations[x][y].getPlants().add(plants);
            }
        }
        when(island.getLocations()).thenReturn(locations);
        when(island.getWIDTH()).thenReturn(width);
        when(island.getHEIGHT()).thenReturn(height);

        int countPlantsResult = statisticsCollector.collectPlantsCounts(island);
        assertEquals(16,countPlantsResult);

    }

    @DisplayName("растений не растут,пустой остров")
    @Test
    void emptyIslandWithoutPlants(){
        int width = 4;
        int height = 4;
        Location[][] locations = new Location[width][height];
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                locations[x][y] = new Location(x, y);
            }
        }
        int countPlantsResult = statisticsCollector.collectPlantsCounts(island);
        assertEquals(0,countPlantsResult);
    }

    @DisplayName("Растения только в некоторых клетках, считает только заполненные клетки")
    @Test
    void countOnlyFullCells(){
        Plants plants = Mockito.mock(Plants.class);
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
        locations[1][1].getPlants().add(plants);
        locations[2][2].getPlants().add(plants);
        locations[3][3].getPlants().add(plants);

        int countPlantsResult = statisticsCollector.collectPlantsCounts(island);
        assertEquals(3,countPlantsResult);
    }

    @DisplayName("несколько растений в одной клетке, проверить что size() работает правильно")
    @Test
    void multiplePlantsInOneCell(){
        Plants plant1 = Mockito.mock(Plants.class);
        Plants plant2 = Mockito.mock(Plants.class);
        int width = 4;
        int height = 4;
        Location[][] locations = new Location[width][height];

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                locations[x][y] = new Location(x,y);
            }
        }
        Location cell = locations[1][1];
        when(island.getLocations()).thenReturn(locations);
        when(island.getWIDTH()).thenReturn(width);
        when(island.getHEIGHT()).thenReturn(height);

        Set<Plants> plantsSet = cell.getPlants();
        plantsSet.add(plant1);
        plantsSet.add(plant2);

        int countPlantsResult = statisticsCollector.collectPlantsCounts(island);
        assertEquals(plantsSet.size(),countPlantsResult);
    }

    @DisplayName("разное количество растений в разных клетках")
    @Test
    void differentPlantsQuantityInCells(){
        Plants plant1 = Mockito.mock(Plants.class);
        Plants plant2 = Mockito.mock(Plants.class);
        Plants plant3 = Mockito.mock(Plants.class);
        Plants plant4 = Mockito.mock(Plants.class);
        int width = 4;
        int height = 4;
        Location[][] locations = new Location[width][height];

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                locations[x][y] = new Location(x,y);
            }
        }
        Location cell1 = locations[1][1];
        Location cell2 = locations[1][2];
        Location cell3 = locations[1][3];
        when(island.getLocations()).thenReturn(locations);
        when(island.getWIDTH()).thenReturn(width);
        when(island.getHEIGHT()).thenReturn(height);

        Set<Plants> plantsSet1 = cell1.getPlants();
        plantsSet1.add(plant1);
        plantsSet1.add(plant2);
        plantsSet1.add(plant3);

        Set<Plants> plantsSet2 = cell2.getPlants();
        plantsSet2.add(plant1);
        plantsSet2.add(plant2);

        Set<Plants> plantsSet3 = cell3.getPlants();
        plantsSet3.add(plant1);
        plantsSet3.add(plant2);
        plantsSet3.add(plant3);
        plantsSet3.add(plant4);

        int countPlantsResult = statisticsCollector.collectPlantsCounts(island);
        assertEquals(9,countPlantsResult);
    }
}
