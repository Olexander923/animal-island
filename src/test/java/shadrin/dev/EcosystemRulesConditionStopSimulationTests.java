package shadrin.dev;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import shadrin.dev.animal.Animal;
import shadrin.dev.animal.carnivore.Wolf;
import shadrin.dev.config.*;
import shadrin.dev.field.Island;
import shadrin.dev.field.Location;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@DisplayName("тестирование метода условий остановки симуляции")
public class EcosystemRulesConditionStopSimulationTests {
    private Island island;
    private Location location;


    @BeforeEach
    void setUp(){
        island = Mockito.mock(Island.class);
        location = Mockito.mock(Location.class);
    }

    @DisplayName("все животные умерли - возвращает true")
    @Test
    void trueConditionStopSimulation(){
        //создать остров
        int weight = 4;
        int height = 4;
        Location[][] locations = new Location[4][4];
        for (int x = 0; x < weight; x++){
            for (int y = 0; y < height; y++){
                locations[x][y] = new Location(x,y);
            }
        }
     // Для теста true - все умерли НИЧЕГО не добавлять - все клетки останутся пустыми

        when(island.getLocations()).thenReturn(locations);
        when(island.getHEIGHT()).thenReturn(height);
        when(island.getWIDTH()).thenReturn(weight);

        boolean result =  EcosystemRules.conditionStopSimulation(island);

        assertTrue(result,"method must return 'true', when all animals are dead");
        }

        @DisplayName("Есть еще живые животные - возвращает false")
        @Test
        void falseConditionStopSimulation(){
            //создать остров
            int weight = 4;
            int height = 4;
            Location[][] locations = new Location[4][4];
            for (int x = 0; x < weight; x++){
                for (int y = 0; y < height; y++){
                    locations[x][y] = new Location(x,y);
                }
            }
            when(island.getLocations()).thenReturn(locations);
            when(island.getHEIGHT()).thenReturn(height);
            when(island.getWIDTH()).thenReturn(weight);

            Animal mockAnimal = Mockito.mock(Animal.class);

                //в клетку помещаем мок животного и добавляем в множество
                Location cell = locations[1][1];
                Set<Animal> animalSet = cell.getAnimals();
                animalSet.add(mockAnimal);

            boolean result =  EcosystemRules.conditionStopSimulation(island);
            assertFalse(result,"method must return 'false', when any animals still alive");
        }


    }

