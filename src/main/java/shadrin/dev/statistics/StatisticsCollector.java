package shadrin.dev.statistics;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import shadrin.dev.animal.Animal;
import shadrin.dev.config.SimulationConfig;
import shadrin.dev.field.Location;
import shadrin.dev.config.AnimalType;
import shadrin.dev.emoji.EmojiProvider;
import shadrin.dev.field.Island;

import java.util.EnumMap;
import java.util.Map;
import java.util.Set;
/**
 * подсчитывает количество особей каждого вида и выводит таблицу
 */
public class StatisticsCollector {
    private static final Logger log = LogManager.getLogger(StatisticsCollector.class);
    /**
     * подсчитывает сколько особей каждого вида находиться на острове
     * @return результат, заполненная мапа
     */
    public Map<AnimalType,Integer> collectAnimalCounts(Island island) {
        Map<AnimalType, Integer> animalCount = new EnumMap<>(AnimalType.class);//мапа для подсчета животных в каждой клетке
        Location[][] locations = island.getLocations();
        for (int x = 0; x < island.getWIDTH(); x++) {
            for (int y = 0; y < island.getHEIGHT(); y++) {
                Set<Animal> animals = locations[x][y].getAnimals();
                for (Animal animal : animals) {
                        if (animal.isAlive()) {
                            animalCount.merge(animal.getType(), 1, Integer::sum);
                    }
                }
            }
        }
        StringBuilder emojiStrings = new StringBuilder();
        for (AnimalType type : AnimalType.values()) {
            int count = animalCount.getOrDefault(type,0);
            String emoji = EmojiProvider.getEmoji(type);
            String resultString = emoji + " = " + count + ";";
            emojiStrings.append(resultString);
        }

        System.out.println("Animal counts: " + emojiStrings);
        log.info("Animal counts: " + emojiStrings);

        return animalCount;
    }

    /**
     * подсчитывает сколько растений находиться на острове
     * @return результат, общее число растений
     */
    public int collectPlantsCounts(Island island) {
        int plantCount = 0;
        Location[][] locations = island.getLocations();
        for (int x = 0; x < island.getWIDTH(); x++) {
            for (int y = 0; y < island.getHEIGHT(); y++) {
                plantCount += locations[x][y].getPlants().size();
            }
        }
        System.out.println(EmojiProvider.getEmojiPlant() +" counts: " + plantCount);
        log.info(EmojiProvider.getEmojiPlant() +" counts: " + plantCount);
        return plantCount;
    }
}
