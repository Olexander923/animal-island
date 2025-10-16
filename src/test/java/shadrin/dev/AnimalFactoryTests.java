package shadrin.dev;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import shadrin.dev.animal.Animal;
import shadrin.dev.animal.carnivore.Bear;
import shadrin.dev.animal.herbivore.Duck;
import shadrin.dev.config.*;
import shadrin.dev.field.Island;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("смоук-тесты для проверки что метод не возвращает null объекты")
public class AnimalFactoryTests {
    SimulationConfig simulationConfig = Mockito.mock(SimulationConfig.class);
    Island island = Mockito.mock(Island.class);
    AnimalParams params = new AnimalParams(
            500,
            5,
            2,
            80,
            2,
            Set.of(Edible.HARE,Edible.HORSE,Edible.DEER,Edible.HAMSTER,Edible.GOAT,Edible.SHEEP,Edible.DUCK)
    );

    @DisplayName("тест что фабрика просто работает и не возвращает null для конкретного животного")
    @Test
    void createBear(){
        Animal animal = AnimalFactory.create(
                AnimalType.BEAR,params,simulationConfig,island
        );
        assertNotNull(animal);
        assertTrue(animal instanceof Bear);
    }

    @Test
    void createDuck(){
        Animal animal = AnimalFactory.create(
                AnimalType.DUCK,params,simulationConfig,island
        );
        assertNotNull(animal);
        assertTrue(animal instanceof Duck);
    }
}
