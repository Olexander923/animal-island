package shadrin.dev;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import shadrin.dev.field.Location;
import shadrin.dev.plant.Plants;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class PlantsTests {
    @DisplayName("успешный рост растений: есть доступные клетки")
    @Test
    void successfulGrow() {
        Location location = new Location(1,1);
        int maxPlantsPerCell = 10;
        //проверка начального состояние - растений нет
        assertEquals(0,location.getPlants().size());
        Plants.grow(location,maxPlantsPerCell);
        assertEquals(1.0,location.getPlants().size());
    }

    @DisplayName("не растет когда достигнут лимит")
    @Test
    void growingLimitHasReached(){
        Location location = new Location(1,1);
        int maxPlantsPerCell = 10;
        for (int i = 0; i < 30; i++ ){
            Plants.grow(location,maxPlantsPerCell);
        }
        assertEquals(10,location.getPlants().size());
        Plants.grow(location,maxPlantsPerCell);
        assertEquals(10,location.getPlants().size());
    }

    @DisplayName("не растет когда maxPlantsPerCell = 0")
    @Test
    void maxPlantsPerCellIsNull(){
        Location location = new Location(1,1);
        int maxPlantsPerCell = 0;
        assertEquals(0,location.getPlants().size());
        Plants.grow(location,maxPlantsPerCell);
        assertEquals(0,location.getPlants().size());
    }

}
