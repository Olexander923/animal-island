package shadrin.dev.animal;

public class Eagle extends Carnivore {
    public Eagle(SimulationConfig config, Island island, AnimalType type, double weight, int maxAnimalsPerCell, int speedMoving, double foodToSaturate, Set<Edible> diet) {
        super(config,island,type,weight, maxAnimalsPerCell, speedMoving, foodToSaturate, diet);
    }

}
