package config;

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
}
