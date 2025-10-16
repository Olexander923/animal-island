package shadrin.dev.config;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import shadrin.dev.animal.carnivore.Carnivore;
import shadrin.dev.field.Island;

import java.util.Map;

/**
 * матрица вероятностей добычи
 */
public class EcosystemRules {
    private static final Logger log = LogManager.getLogger(EcosystemRules.class);
    private static Map<AnimalType,Map<Edible,Double>> probabilityOfEating;//матрица вероятности добычи в %,
    //заполняется из yaml

    /**
     * метод для получения вероятности быть съеденным
     * @param attacker хищник
     * @param prey добыча
     * @return
     */
    public static double getProbabilityOfEating(AnimalType attacker, Edible prey) {
        Map<Edible,Double> attackerChances = probabilityOfEating.get(attacker);
        if (attackerChances == null){return 0.0;} //хищник не найден в матрице
        Double chance = attackerChances.get(prey);
        return chance != null ? chance : 0.0; // вернуть 0.0 если конкретная добыча не найдена
    }

    /**
     * для получения детенышей
     * @param type
     * @return
     */
    public static int getCubsPerBirth(AnimalType type) {//todo проверить нужен или нет и убрать!
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
        log.info("все умерли, симулиция останавливается");
        return true;
    }

    public static void setProbabilityOfEating(Map<AnimalType, Map<Edible, Double>> probabilityOfEating) {
        EcosystemRules.probabilityOfEating = probabilityOfEating;
    }
}
