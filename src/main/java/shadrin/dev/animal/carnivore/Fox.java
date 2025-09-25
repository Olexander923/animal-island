package shadrin.dev.animal;

public class Fox extends Carnivore {
    public Fox(SimulationConfig config, Island island, AnimalType type, double weight, int maxAnimalsPerCell, int speedMoving, double foodToSaturate, Set<Edible> diet) {
        super(config,island, type, weight, maxAnimalsPerCell, speedMoving, foodToSaturate, diet);
    }


}
