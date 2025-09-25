package shadrin.dev.animal.herbivore;
import shadrin.dev.config.AnimalType;
import shadrin.dev.config.Edible;
import shadrin.dev.field.Island;
import shadrin.dev.config.SimulationConfig;

import java.util.Set;

public class WildBoar extends Herbivore {
    public WildBoar(SimulationConfig config, Island island, AnimalType type, double weight, int maxAnimalsPerCell, int speedMoving, double foodToSaturate, Set<Edible> diet) {
        super(config,island, type, weight, maxAnimalsPerCell, speedMoving, foodToSaturate, diet);
    }

}