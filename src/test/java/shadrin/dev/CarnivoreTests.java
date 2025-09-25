package shadrin.dev;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import shadrin.dev.animal.Eatable;
import shadrin.dev.animal.Location;
import shadrin.dev.animal.carnivore.Wolf;
import shadrin.dev.animal.herbivore.Sheep;
import shadrin.dev.config.AnimalType;
import shadrin.dev.config.Edible;
import shadrin.dev.config.SimulationConfig;
import shadrin.dev.field.Island;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@DisplayName("Тестирование основной логики класса")
public class CarnivoreTests  {
    @Mock
    Set<Edible> diet = new HashSet<>();
    List<Eatable> candidates = new ArrayList<>();
    Island island = Mockito.mock(Island.class);
    SimulationConfig simulationConfig = Mockito.mock(SimulationConfig.class);

    Location location = Mockito.mock(Location.class);


    @Test
    @DisplayName("Успешное поедание - жертва в рационе, в одной клетке")
    void eatTest(){
        //создаю реальную жертву
        Sheep sheep = new Sheep(simulationConfig,island, AnimalType.SHEEP,70,140,3,15,diet);
        Mockito.when(location.getAnimals()).thenReturn(Set.of(sheep));//мок возвращает жертву
        //с помощью мока острова, возвращаем мок локации
        Mockito.when(island.getLocations()).thenReturn(new Location[][]{{location}});
        Wolf wolf = new Wolf(simulationConfig,island,AnimalType.WOLF,50,30,3,8,diet);
        wolf.setCurrentLocation(location);


    }
}
