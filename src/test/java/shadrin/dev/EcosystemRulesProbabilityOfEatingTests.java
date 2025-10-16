package shadrin.dev;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import shadrin.dev.config.AnimalType;
import shadrin.dev.config.EcosystemRules;
import shadrin.dev.config.Edible;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class EcosystemRulesProbabilityOfEatingTests {
    @BeforeEach
    void setUp(){
        Map<Edible,Double> attackerChances = new HashMap<>();
        attackerChances.put(Edible.SHEEP,0.8);
        Map<AnimalType,Map<Edible,Double>> testData = new HashMap<>();
        testData.put(AnimalType.WOLF,attackerChances);

        //вставляю тестовые данные в статическом поле
        EcosystemRules.setProbabilityOfEating(testData);
    }

    @DisplayName("успешный расчет вероятности,добыча есть")
    @Test
    void successfulGetProbabilityOfEating(){
        AnimalType attacker = AnimalType.WOLF;
        Edible prey = Edible.SHEEP;
        double expectedProbability = 0.8;

        Map<Edible, Double> attackerChances = new HashMap<>();
        attackerChances.put(prey, expectedProbability);

        Map<AnimalType, Map<Edible, Double>> probabilityOfEating = new HashMap<>();
        probabilityOfEating.put(attacker, attackerChances);


            EcosystemRules.setProbabilityOfEating(probabilityOfEating);

            double result = EcosystemRules.getProbabilityOfEating(attacker,prey);
            assertEquals(expectedProbability,result);
    }

    @DisplayName("Хищник не найден, должен вернуть 0% вероятности")
    @Test
    void carnivoreNotFound(){
        AnimalType attacker = AnimalType.WOLF;
        Edible prey = Edible.SHEEP;
        double expectedProbability = 0.8;

        Map<Edible,Double> attackerChances = new HashMap<>();
        attackerChances.put(prey,expectedProbability);

        Map<AnimalType,Map<Edible,Double>> probabilityOfEating = new HashMap<>();
        probabilityOfEating.put(attacker,attackerChances);

        EcosystemRules.setProbabilityOfEating(probabilityOfEating);
        double result = EcosystemRules.getProbabilityOfEating(AnimalType.BEAR,prey);
        assertEquals(0,result);
    }

    @DisplayName("Хищник есть в мапе + добычи нет в мапе")
    @Test
    void herbivoreNotFound(){
        AnimalType attacker = AnimalType.WOLF;
        Edible prey = Edible.SHEEP;
        double expectedProbability = 0.8;

        Map<Edible,Double> attackerChances = new HashMap<>();
        attackerChances.put(prey,expectedProbability);

        Map<AnimalType,Map<Edible,Double>> probabilityOfEating = new HashMap<>();
        probabilityOfEating.put(attacker,attackerChances);

        EcosystemRules.setProbabilityOfEating(probabilityOfEating);
        double result = EcosystemRules.getProbabilityOfEating(attacker,Edible.HARE);
        assertEquals(0,result);

    }

    @DisplayName("Нулевая вероятность - пара есть в матрице, но chance = 0.0")
    @Test
    void ZeroProbability(){
        AnimalType attacker = AnimalType.WOLF;
        Edible prey = Edible.SHEEP;
        double expectedProbability = 0.0;

        Map<Edible,Double> attackerChances = new HashMap<>();
        attackerChances.put(prey,expectedProbability);

        Map<AnimalType,Map<Edible,Double>> probabilityOfEating = new HashMap<>();
        probabilityOfEating.put(attacker,attackerChances);

        EcosystemRules.setProbabilityOfEating(probabilityOfEating);
        double result = EcosystemRules.getProbabilityOfEating(attacker,prey);
        assertEquals(0,result);
    }

    @DisplayName("граничное значение, chance = 100%")
    @Test
    void probability100Percent(){
        AnimalType attacker = AnimalType.WOLF;
        Edible prey = Edible.SHEEP;
        double expectedProbability = 1.0;

        Map<Edible,Double> attackerChances = new HashMap<>();
        attackerChances.put(prey,expectedProbability);

        Map<AnimalType,Map<Edible,Double>> probabilityOfEating = new HashMap<>();
        probabilityOfEating.put(attacker,attackerChances);

        EcosystemRules.setProbabilityOfEating(probabilityOfEating);
        double result = EcosystemRules.getProbabilityOfEating(attacker,prey);
        assertEquals(1,result);
    }

    @DisplayName(" нижнее граничное значение, chance = 1%")
    @Test
    void probability1Percent(){
        AnimalType attacker = AnimalType.WOLF;
        Edible prey = Edible.SHEEP;
        double expectedProbability = 0.01;

        Map<Edible,Double> attackerChances = new HashMap<>();
        attackerChances.put(prey,expectedProbability);

        Map<AnimalType,Map<Edible,Double>> probabilityOfEating = new HashMap<>();
        probabilityOfEating.put(attacker,attackerChances);

        EcosystemRules.setProbabilityOfEating(probabilityOfEating);
        double result = EcosystemRules.getProbabilityOfEating(attacker,prey);
        assertEquals(0.01,result);
    }



}
