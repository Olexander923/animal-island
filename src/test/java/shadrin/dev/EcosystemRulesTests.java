package shadrin.dev;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import shadrin.dev.config.AnimalType;
import shadrin.dev.config.EcosystemRules;
import shadrin.dev.config.Edible;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class EcosystemRulesTests {
    @BeforeEach
    void setUp(){
        Map<Edible,Double> attackerChances = new HashMap<>();
        attackerChances.put(Edible.SHEEP,0.8);
        Map<AnimalType,Map<Edible,Double>> testData = new HashMap<>();
        testData.put(AnimalType.WOLF,attackerChances);

        //вставляю тестовые данные в статическом поле
        EcosystemRules.setProbabilityOfEating(testData);
    }

    @DisplayName("успешный расчет вероятности")
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
}
