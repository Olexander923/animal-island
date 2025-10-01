package shadrin.dev.config;

import shadrin.dev.animal.Animal;
import shadrin.dev.animal.carnivore.Bear;
import shadrin.dev.animal.carnivore.Eagle;
import shadrin.dev.animal.carnivore.Fox;
import shadrin.dev.animal.carnivore.Wolf;
import shadrin.dev.animal.herbivore.*;
import shadrin.dev.field.Island;

/**
 * фабрика животных для создания конкретных объектов-животных по типу
 */
public class AnimalFactory {

    public static Animal create(AnimalType type, AnimalParams params, SimulationConfig config, Island island) {
        return switch (type) {
            case BEAR -> new Bear(config,island,type,
                    params.getWeight(),
                    params.getMaxAnimalsPerCell(),
                    params.getSpeedMoving(),
                    params.getFoodToSaturate(),
                    params.getDiet());

            case EAGLE -> new Eagle(config,island,type,
                    params.getWeight(),
                    params.getMaxAnimalsPerCell(),
                    params.getSpeedMoving(),
                    params.getFoodToSaturate(),
                    params.getDiet());

            case FOX -> new Fox(config,island,type,
                    params.getWeight(),
                    params.getSpeedMoving(),
                    params.getMaxAnimalsPerCell(),
                    params.getFoodToSaturate(),
                    params.getDiet()
            );

            case WOLF -> new Wolf(config,island,type,
                    params.getWeight(),
                    params.getSpeedMoving(),
                    params.getMaxAnimalsPerCell(),
                    params.getFoodToSaturate(),
                    params.getDiet()
            );
            case BUFFALO -> new Buffalo(
                    config,island,type,
                    params.getWeight(),
                    params.getSpeedMoving(),
                    params.getMaxAnimalsPerCell(),
                    params.getFoodToSaturate(),
                    params.getDiet()
            );

            case CATERPILLAR -> new Caterpillar(
                    config,island,type,
                    params.getWeight(),
                    params.getSpeedMoving(),
                    params.getMaxAnimalsPerCell(),
                    params.getFoodToSaturate(),
                    params.getDiet()
            );

            case COW -> new Cow(config,island,type,
                    params.getWeight(),
                    params.getSpeedMoving(),
                    params.getMaxAnimalsPerCell(),
                    params.getFoodToSaturate(),
                    params.getDiet());

            case DEER -> new Deer(config,island,type,
                    params.getWeight(),
                    params.getSpeedMoving(),
                    params.getMaxAnimalsPerCell(),
                    params.getFoodToSaturate(),
                    params.getDiet()
            );

            case DUCK -> new Duck(config,island,type,
                    params.getWeight(),
                    params.getSpeedMoving(),
                    params.getMaxAnimalsPerCell(),
                    params.getFoodToSaturate(),
                    params.getDiet()
            );

            case GOAT -> new Goat(config,island,type,
                    params.getWeight(),
                    params.getSpeedMoving(),
                    params.getMaxAnimalsPerCell(),
                    params.getFoodToSaturate(),
                    params.getDiet()
            );

            case HAMSTER -> new Hamster(config,island,type,
                    params.getWeight(),
                    params.getSpeedMoving(),
                    params.getMaxAnimalsPerCell(),
                    params.getFoodToSaturate(),
                    params.getDiet()
            );

            case HARE -> new Hare(config,island,type,
                    params.getWeight(),
                    params.getSpeedMoving(),
                    params.getMaxAnimalsPerCell(),
                    params.getFoodToSaturate(),
                    params.getDiet()
            );

            case HORSE -> new Horse(config,island,type,
                    params.getWeight(),
                    params.getSpeedMoving(),
                    params.getMaxAnimalsPerCell(),
                    params.getFoodToSaturate(),
                    params.getDiet()
            );

            case KANGAROO -> new Kangaroo(config,island,type,
                    params.getWeight(),
                    params.getSpeedMoving(),
                    params.getMaxAnimalsPerCell(),
                    params.getFoodToSaturate(),
                    params.getDiet()
            );

            case SHEEP -> new Sheep(config,island,type,
                    params.getWeight(),
                    params.getSpeedMoving(),
                    params.getMaxAnimalsPerCell(),
                    params.getFoodToSaturate(),
                    params.getDiet()
            );

            case WILD_BOAR -> new WildBoar(config,island,type,
                    params.getWeight(),
                    params.getSpeedMoving(),
                    params.getMaxAnimalsPerCell(),
                    params.getFoodToSaturate(),
                    params.getDiet()
            );

            case BOA -> new Boa(config,island,type,
                    params.getWeight(),
                    params.getSpeedMoving(),
                    params.getMaxAnimalsPerCell(),
                    params.getFoodToSaturate(),
                    params.getDiet()
            );

            default -> throw new IllegalStateException("Unexpected value: " + type);
        };

    }
}
