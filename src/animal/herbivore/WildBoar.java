package animal.herbivore;

import animal.Eatable;
import animal.Location;
import config.AnimalType;
import config.EcosystemRules;
import config.Edible;
import config.SimulationConfig;
import field.Island;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

import static java.lang.Math.min;

public class WildBoar extends Herbivore {
    public WildBoar(SimulationConfig config, Island island, AnimalType type, double weight, int maxAnimalsPerCell, int speedMoving, double foodToSaturate, Set<Edible> diet) {
        super(config,island, type, weight, maxAnimalsPerCell, speedMoving, foodToSaturate, diet);
    }

}
