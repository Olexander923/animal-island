package config;

import field.Island;

import java.util.Map;

/**
 * матрица вероятностей добычи
 */
public class EcosystemRules {
    private static Map<AnimalType,Map<Edible,Double>> probabilityOfEating;//матрица вероятности добычи в %,
    //заполняется из yaml

    /**
     * метод для получения вероятности быть съеденным
     * @param attacker хищник
     * @param prey добыча
     * @return
     */
    public static double getProbabilityOfEating(AnimalType attacker, Edible prey) {
        return probabilityOfEating.get(attacker).get(prey);
    }

    /**
     * для получения детенышей
     * @param type
     * @return
     */
    public static int getCubsPerBirth(AnimalType type) {
        return SimulationConfig.getAnimalsMap().get(type).getCubsPerBirth();
    }

    public static boolean conditionStopSimulation(Island island) {
        // условие что все животные умерли
        for (int x = 0; x < island.getWIDTH(); x++) {
            for (int y = 0; y < island.getHEIGHT(); y++) {
                if (!island.getLocations()[x][y].getAnimals().isEmpty()){
                    return false;//животные еще есть, продолжается симуляция
                }

            }
        }
        return true;//все умерли, симулиция останавливается
    }
}
