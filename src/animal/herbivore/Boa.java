package animal.herbivore;

import config.AnimalType;
import config.Edible;
import config.SimulationConfig;
import field.Island;

import java.util.Set;

public class Boa extends Herbivore {
    public Boa(SimulationConfig config, Island island, AnimalType type, double weight, int maxAnimalsPerCell, int speedMoving, double foodToSaturate, Set<Edible> diet) {
        super(config, island, type, weight, maxAnimalsPerCell, speedMoving, foodToSaturate, diet);
    }
}
