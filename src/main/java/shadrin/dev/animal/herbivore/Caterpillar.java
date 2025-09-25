package shadrin.dev.herbivore;
import shadrin.dev.config.AnimalType;
import shadrin.dev.config.Edible;
import shadrin.dev.config.SimulationConfig;
import shadrin.dev.field.Island;

import java.util.Set;

public class Caterpillar extends Herbivore {
    public Caterpillar(SimulationConfig config, Island island, AnimalType type, double weight, int maxAnimalsPerCell, int speedMoving, double foodToSaturate, Set<Edible> diet) {
        super(config,island, type, weight, maxAnimalsPerCell, speedMoving, foodToSaturate, diet);
    }


}