package shadrin.dev;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import shadrin.dev.animal.Animal;
import shadrin.dev.animal.Eatable;
import shadrin.dev.animal.Location;
import shadrin.dev.animal.carnivore.Carnivore;
import shadrin.dev.animal.carnivore.Wolf;
import shadrin.dev.animal.herbivore.Sheep;
import shadrin.dev.config.AnimalType;
import shadrin.dev.config.EcosystemRules;
import shadrin.dev.config.Edible;
import shadrin.dev.config.SimulationConfig;
import shadrin.dev.field.Island;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

import static java.lang.Math.min;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@DisplayName("Тестирование основной логики класса")
public class CarnivoreTests  {

    //создаем и мокаем жертву для волка
    Sheep sheep = Mockito.mock(Sheep.class);
    SimulationConfig simulationConfig = Mockito.mock(SimulationConfig.class);
    Island island = Mockito.mock(Island.class);
    Location location = Mockito.mock(Location.class);

    private Wolf wolf;

    @BeforeEach
    void setUp(){
        Set<Edible> wolfDiet = Set.of(Edible.SHEEP);
        wolf = new Wolf(
                simulationConfig,island,AnimalType.WOLF,50,30,
                3,8,wolfDiet);
        wolf.setCurrentLocation(location);
        wolf.setSatiety(0);//волк голодный
    }

    @Test
    @DisplayName("Успешное поедание - жертва в рационе 100%, в одной клетке")
    void wolfSuccessfullyEat(){

        //что лежит в клетке
       Set<Animal> animalsInCells = new HashSet<>(Set.of(wolf,sheep));
       when(location.getAnimals()).thenReturn(animalsInCells);
       when(location.getPlants()).thenReturn(Set.of());

       //теперь ставим параметры жертвы
        when(sheep.getEdible()).thenReturn(Edible.SHEEP);
        when(sheep.getWeight()).thenReturn(70.0);//из параметров
        doNothing().when(sheep).removeFrom(location);

        // вероятность поедания 100 %
        try(MockedStatic<EcosystemRules> mockedRules = Mockito.mockStatic(EcosystemRules.class)){
            mockedRules.when(()-> EcosystemRules.getProbabilityOfEating(AnimalType.WOLF,Edible.SHEEP))
                    .thenReturn(1.0);

            wolf.eat();
            assertEquals(8.0,wolf.getSatiety());
            verify(sheep).removeFrom(location);



        }
    }

    @Test
    @DisplayName("Вероятность 0% → овца не удаляется.")
    void wolfUnsuccessfullyEat(){
        Set<Animal> animalInCells = new HashSet<>(Set.of(wolf,sheep));
        when(location.getAnimals()).thenReturn(animalInCells);
        when(location.getPlants()).thenReturn(Set.of());//нет растений

        when(sheep.getEdible()).thenReturn(Edible.SHEEP);
        when(sheep.getWeight()).thenReturn(70.0);

        //вероятность 0%
        try(MockedStatic<EcosystemRules> mockedRules = Mockito.mockStatic(EcosystemRules.class)) {
            mockedRules.when(() -> EcosystemRules.getProbabilityOfEating(AnimalType.WOLF, Edible.SHEEP))
                    .thenReturn(0.0);

            wolf.eat();
            assertEquals(0.0,wolf.getSatiety());
            verify(sheep,never()).removeFrom(any());
        }
    }
}
