package shadrin.dev;

import org.junit.jupiter.api.*;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import shadrin.dev.animal.Animal;
import shadrin.dev.field.Location;
import shadrin.dev.animal.carnivore.Wolf;
import shadrin.dev.animal.herbivore.Sheep;
import shadrin.dev.config.*;
import shadrin.dev.field.Island;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("Тестирование основной логики класса")
public class CarnivoreTests {

    //моки жертвы для волка и другие зависимости
    Sheep sheep = Mockito.mock(Sheep.class);
    SimulationConfig simulationConfig = Mockito.mock(SimulationConfig.class);
    Island island = Mockito.mock(Island.class);
    Location location = Mockito.mock(Location.class);
    private final Set<Edible> wolfDiet = Set.of(Edible.SHEEP);
    private Wolf wolf;

    @BeforeEach
    void setUp() {

        wolf = new Wolf(
                simulationConfig, island, AnimalType.WOLF, 50, 30,
                3, 8, wolfDiet);
        wolf.setCurrentLocation(location);
        wolf.setSatiety(0);//волк голодный
    }


    @Test
    @DisplayName("Успешное поедание - жертва в рационе 100%, в одной клетке")
    void wolfSuccessfullyEat() {
        //что лежит в клетке
        Set<Animal> animalsInCells = new HashSet<>(Set.of(wolf, sheep));
        when(location.getAnimals()).thenReturn(animalsInCells);
        when(location.getPlants()).thenReturn(Set.of());

        //теперь ставим параметры жертвы
        when(sheep.getEdible()).thenReturn(Edible.SHEEP);
        when(sheep.getWeight()).thenReturn(70.0);//из параметров
        doNothing().when(sheep).removeFrom(location);

        // вероятность поедания 100 %
        try (MockedStatic<EcosystemRules> mockedRules = Mockito.mockStatic(EcosystemRules.class)) {
            mockedRules.when(() -> EcosystemRules.getProbabilityOfEating(AnimalType.WOLF, Edible.SHEEP))
                    .thenReturn(1.0);

            wolf.eat();
            assertEquals(8.0, wolf.getSatiety());
            verify(sheep).removeFrom(location);
        }
    }

    @Test
    @DisplayName("Вероятность 0% → овца не удаляется.")
    void wolfUnsuccessfullyEat() {
        Set<Animal> animalInCells = new HashSet<>(Set.of(wolf, sheep));
        when(location.getAnimals()).thenReturn(animalInCells);
        when(location.getPlants()).thenReturn(Set.of());//нет растений

        when(sheep.getEdible()).thenReturn(Edible.SHEEP);
        when(sheep.getWeight()).thenReturn(70.0);

        //вероятность 0%
        try (MockedStatic<EcosystemRules> mockedRules = Mockito.mockStatic(EcosystemRules.class)) {
            mockedRules.when(() -> EcosystemRules.getProbabilityOfEating(AnimalType.WOLF, Edible.SHEEP))
                    .thenReturn(0.0);

            wolf.eat();
            assertEquals(0.0, wolf.getSatiety());
            verify(sheep, never()).removeFrom(any());
        }
    }

    @DisplayName("добыча не в diet → не атакуем")
    @Test
    void wolfHasNoDiet() {
        Set<Edible> wolfDiet = Set.of(); //нет овцы в рационе
        wolf = new Wolf(
                simulationConfig, island, AnimalType.WOLF, 50, 30,
                3, 8, wolfDiet);
        wolf.setCurrentLocation(location);
        wolf.setSatiety(0);
        Set<Animal> animalsInCells = new HashSet<>(Set.of(wolf, sheep));

        when(location.getAnimals()).thenReturn(animalsInCells);
        when(location.getPlants()).thenReturn(Set.of());
        when(sheep.getEdible()).thenReturn(Edible.SHEEP);

        wolf.eat();
        // проверяем: сытость не изменилась, овцу никто не трогал
        assertEquals(0.0, wolf.getSatiety());
        verify(sheep, never()).removeFrom(any());
    }

    @DisplayName("сытость волка после еды не превышает foodToSaturate")
    @Test
    void wolfLessSatiety() {
        Set<Animal> animalInCells = new HashSet<>(Set.of(wolf, sheep));
        when(location.getAnimals()).thenReturn(animalInCells);
        when(location.getPlants()).thenReturn(Set.of());//нет растений

        when(sheep.getEdible()).thenReturn(Edible.SHEEP);
        when(sheep.getWeight()).thenReturn(70.0);

        //вероятность 0%
        try (MockedStatic<EcosystemRules> mockedRules = Mockito.mockStatic(EcosystemRules.class)) {
            mockedRules.when(() -> EcosystemRules.getProbabilityOfEating(AnimalType.WOLF, Edible.SHEEP))
                    .thenReturn(1.0);

            wolf.eat();
            assertEquals(8.0, wolf.getSatiety());
        }
    }

    @DisplayName("хватает партнеров для размножения, достаточно места в клетке для детенышей" +
            "животные сыты")
    @Test
        //todo попробовать сделать параметризированный тест для всех животных
    void wolfSuccessfullyMultiply() {
        var wolfPartner = new Wolf(
                simulationConfig, island, AnimalType.WOLF, 50, 30,
                3, 8, wolfDiet
        );
        // пока в списке только wolf – partner НЕ участвует
        Set<Animal> realAnimals = new HashSet<>(Set.of(wolf));
        realAnimals.add(wolfPartner);
        realAnimals.add(wolf);
        System.out.println(">>> animals in cell before multiply: " + realAnimals);
        when(location.getAnimals()).thenReturn(realAnimals);

        //заглушка для мапы животных
        AnimalParams params = new AnimalParams(
                50, 30, 3, 8, 2, wolfDiet);
        try (MockedStatic<SimulationConfig> mockedConfig = mockStatic(SimulationConfig.class)) {
            mockedConfig.when(SimulationConfig::getAnimalsMap).thenReturn(Map.of(AnimalType.WOLF, params));

            wolf.setSatiety(8);
            wolf.multiply();

            //теперь добавим partner, чтобы проверить итоговый размер
            realAnimals.add(wolfPartner);
            assertEquals(4, realAnimals.size()); // 2 родителя + 2 детёныша
            assertEquals(4.0, wolf.getSatiety(), 0.01); // 8 - 0.5*8 = 4
        }
    }

    @DisplayName("нет партнеров для размножения")
    @Test
    void notEnoughPartnersForCarnivore() {

        Set<Animal> singleWolf = new HashSet<>(Set.of(wolf));
        when(location.getAnimals()).thenReturn(singleWolf);

        AnimalParams params = new AnimalParams(
                50, 30, 3, 8, 2, wolfDiet);
        try (MockedStatic<SimulationConfig> mockedConfig = mockStatic(SimulationConfig.class)) {
            mockedConfig.when(SimulationConfig::getAnimalsMap).thenReturn(Map.of(AnimalType.WOLF, params));

            wolf.setSatiety(8.0);
            wolf.multiply();

            assertEquals(1, singleWolf.size());
            assertEquals(8.0,wolf.getSatiety(),0.01);
        }

    }

    @DisplayName("нет места в клетке для партнера")
    @Test
    void notEnoughPlaceInCellForPartner() {
        Set<Animal> crowded = new HashSet<>(Set.of(wolf));
        for (int i = 0; i < 28; i++) {
            crowded.add(new Wolf(simulationConfig, island, AnimalType.WOLF, 50,
                    30, 3, 8, wolfDiet));
        }
        var wolfPartner = new Wolf(
                simulationConfig, island, AnimalType.WOLF, 50, 30,
                3, 8, wolfDiet
        );
        crowded.add(wolf);
        crowded.add(wolfPartner);
        when(location.getAnimals()).thenReturn(crowded);

        AnimalParams params = new AnimalParams(
                50, 30, 3, 8, 2, wolfDiet);
        try (MockedStatic<SimulationConfig> mockedConfig = mockStatic(SimulationConfig.class)) {
            mockedConfig.when(SimulationConfig::getAnimalsMap).thenReturn(Map.of(AnimalType.WOLF, params));

            wolf.setSatiety(8);
            int sizeBefore = crowded.size();
            wolf.multiply();
            int sizeAfter = crowded.size();

            assertEquals(sizeBefore,sizeAfter);//детеными не добавлены
            assertEquals(8,wolf.getSatiety(),0.01);//сытость не умньшается
        }
    }

    @DisplayName("слишком голодный для размножения")
    @Test
    void tooHungryForMultiplying(){
        var wolfPartner = new Wolf(
                simulationConfig, island, AnimalType.WOLF, 50, 30,
                3, 8, wolfDiet
        );
        // пока в списке только wolf – partner НЕ участвует
        Set<Animal> realAnimals = new HashSet<>(Set.of(wolf));
        realAnimals.add(wolfPartner);
        realAnimals.add(wolf);
        when(location.getAnimals()).thenReturn(realAnimals);

        //заглушка для мапы животных
        AnimalParams params = new AnimalParams(
                50, 30, 3, 8, 2, wolfDiet);
        try (MockedStatic<SimulationConfig> mockedConfig = mockStatic(SimulationConfig.class)) {
            mockedConfig.when(SimulationConfig::getAnimalsMap).thenReturn(Map.of(AnimalType.WOLF, params));

            wolf.setSatiety(0); // < 4 кг – голоден
            int sizeBefore = realAnimals.size(); //2
            wolf.multiply();
            int sizeAfter = realAnimals.size(); // также 2 и осталось

            assertEquals(sizeBefore,sizeAfter);// дети не добавлены
            assertEquals(0.0, wolf.getSatiety(), 0.01); // сытость прежняя
        }
    }

    @DisplayName("успешное перемещение по локации")
    @Test
    void successfullyDirectionOfMovement(){
        //создаю реальные локиции с координатами
        int width = 4;
        int height = 4;

        //заполняем массив острова,все соседи – вода, кроме (1,1 и 1,2)
        Location[][] locations = new Location[width][height];
        for (int x = 0; x < width; x++) {
            for (int y = 0; y <height; y++) {
                    locations[x][y] = new Location(x, y);
                    //все клетки будут водой кроме нужных
                if (x == 1 && y == 1) {
                    locations[x][y].setWater(false);//текущая позиция волка
                } else if(x == 1 && y == 2) {
                    locations[x][y].setWater(false);//возможное перемещение
                } else {
                    locations[x][y].setWater(true);  // все остальные - вода
                }
            }
        }

        when(island.getLocations()).thenReturn(locations);
        when(island.getWIDTH()).thenReturn(width);
        when(island.getHEIGHT()).thenReturn(height);

        // ссылка на конкретные локации
        Location currentWolfCell = locations[1][1];
        Location rightNeighbor = locations[1][2];

        wolf.setCurrentLocation(currentWolfCell); //поместили волка в конкретную клетку
        currentWolfCell.getAnimals().add(wolf);

        //волк должен окзаться в соседеней клетке
        wolf.chooseDirectionOfMovement();
        assertEquals(rightNeighbor,wolf.getCurrentLocation());
        assertTrue(rightNeighbor.getAnimals().contains(wolf));
        assertFalse(currentWolfCell.getAnimals().contains(wolf));  // старая клетка пуста

    }

    @DisplayName("безуспешное перемещение, вокруг вода. некуда переместиться")
    @Test
    void unsuccessfullyDirection(){
        int width = 4;
        int height = 4;
        Location[][] locations = new Location[width][height];
        for (int x = 0; x < width; x++){
            for (int y = 0; y < height; y++){
                locations[x][y] = new Location(x,y);
                if (x == 1 && y == 1) {
                    locations[x][y].setWater(false);//текущая позиция волка
                } else if(x == 1 && y == 2) {
                    locations[x][y].setWater(false);//возможное перемещение
                } else {
                    locations[x][y].setWater(true);  // все остальные - вода
                }
            }

        }
        when(island.getLocations()).thenReturn(locations);
        when(island.getHEIGHT()).thenReturn(height);
        when(island.getWIDTH()).thenReturn(width);

        // ссылка на конкретные локации
        Location currentWolfCell = locations[1][1];

        wolf.setCurrentLocation(currentWolfCell); //поместили волка в конкретную клетку
        currentWolfCell.getAnimals().add(wolf);
        wolf.chooseDirectionOfMovement();
        assertEquals(currentWolfCell,wolf.getCurrentLocation());
        assertTrue(currentWolfCell.getAnimals().contains(wolf));

    }

    @DisplayName("перемещение ограничено скоростью животного")
    @Test
    void speedLimitedDirections(){
         //создаю реальные локиции с координатами
        int width = 10;
        int height = 10;

        Location[][] locations = new Location[width][height];
        for (int x = 0; x < width; x++) {
            for (int y = 0; y <height; y++) {
                locations[x][y] = new Location(x, y);
                //все клетки доступны(не вода)
                    locations[x][y].setWater(false);  // все остальные - вода

            }
        }

        when(island.getLocations()).thenReturn(locations);
        when(island.getWIDTH()).thenReturn(width);
        when(island.getHEIGHT()).thenReturn(height);

        // помещаем волка в центр
        Location startCell = locations[5][5];
        wolf.setCurrentLocation(startCell); //поместили волка в конкретную клетку
        startCell.getAnimals().add(wolf);

        //новая позиция волка должна быть в пределах скорости 3 клеток
        Location newLocation = wolf.getCurrentLocation();
        int distanceX = Math.abs(newLocation.getX() - 5);
        int distanceY = Math.abs(newLocation.getY() - 5);
        //макс расстояние не должно превыщать 3
        assertTrue(distanceX <= 3,"Волк не должен перемещаться дальше 3 клеток по X");
        assertTrue(distanceY <= 3,"Волк не должен перемещаться дальше 3 клеток по Y");
        assertEquals(startCell,newLocation);
    }

    @DisplayName("Учет максимальной вместимости клетки")
    @Test
    void checkMaxAnimalsPerCell(){

        int width = 4;
        int height = 4;
        Location[][] locations = new Location[width][height];
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                locations[x][y] = new Location(x, y);
                locations[x][y].setWater(false);  //все клетки доступны

            }
        }
        // текущая клетка волка
        Location cellA = locations[1][1];
        cellA.getAnimals().add(wolf);

        //переполненная клетка
        Location cellB = locations[1][2];
        for (int i = 0; i < 30; i++){
            Animal mockWolf = Mockito.mock(Animal.class);
            when(mockWolf.getType()).thenReturn(AnimalType.WOLF);
            when(mockWolf.isAlive()).thenReturn(true);
            cellB.getAnimals().add(mockWolf);
        }

        assertEquals(30,cellB.getAnimals().size());
        assertEquals(30,wolf.getMaxAnimalsPerCell());//лимит волков
    }
}
