package animal.carnivore;

import animal.Animal;
import animal.Eatable;
import animal.Location;
import config.*;
import field.Island;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

import static java.lang.Math.min;

public class Eagle extends Carnivore {
    public Eagle(SimulationConfig config, Island island, AnimalType type, double weight, int maxAnimalsPerCell, int speedMoving, double foodToSaturate, Set<Edible> diet) {
        super(config,island,type,weight, maxAnimalsPerCell, speedMoving, foodToSaturate, diet);
    }



}
