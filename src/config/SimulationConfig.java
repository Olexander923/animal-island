package config;
import org.yaml.snakeyaml.Yaml;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class SimulationConfig {
    private final  int islandWidth;//ширина острова
    private final  int islandHeight;//высота острова
    private final  Duration cycleDurationOfSimulationMs;//Длительность одного такта симуляции в миллисекундах.
    private final  int numberOfIndividuals;//Стартовое количество особей каждого вида
    private final  boolean stopConditions;//Условие остановки (например, «все животные мертвы» или «прошло N тактов»).
    private Map<AnimalType,Map<Edible,Double>> probabilityOfEating;//матрица вероятности добычи в %
    public static Map<AnimalType,AnimalParams> animalsMap = new ConcurrentHashMap<>();
    private final Map<AnimalType,Integer> initialCounts = new ConcurrentHashMap<>();//мапа для подсчета создания необходимого количества животных


    public SimulationConfig(int islandWidth, int islandHeight, Duration cycleDurationOfSimulationMs, int numberOfIndividuals, boolean stopConditions, Map<AnimalType,AnimalParams> probabilityOfEating) {
        this.islandWidth = islandWidth;
        this.islandHeight = islandHeight;
        this.cycleDurationOfSimulationMs = cycleDurationOfSimulationMs;
        this.numberOfIndividuals = numberOfIndividuals;
        this.stopConditions = stopConditions;
        this.probabilityOfEating = new ConcurrentHashMap<>();
    }

    /**
     * метод для чтения параметров из yaml в Map<String, Object> и получение значений
     * @param yamlPath путь к файлу с параметрами
     * @throws IOException
     */
    public void initializeAnimal(Path yamlPath) throws IOException {//todo вызывается в main
        //читаем YAML-конфиг, достаем значения параметров из мапы для каждого животног
        // YAML-парсер библиотеки SnakeYAML
        Yaml yaml = new Yaml();
        Map<String,Object> root = yaml.load(Files.newInputStream(yamlPath));

        Map<String,Map<String,Object>> animalsYaml = (Map<String,Map<String,Object>>) root.get("animals");
        for (Map.Entry<String,Map<String,Object>> e : animalsYaml.entrySet()) {
            AnimalType type = AnimalType.valueOf(e.getKey().toUpperCase());
            Map<String,Object> m = e.getValue();
            int initial = (Integer) m.getOrDefault("initialCount",0);
            initialCounts.put(type,initial);
            AnimalParams params = new AnimalParams(
                    ((Number) m.get("weight")).doubleValue(),
                    (Integer) m.get("maxPerCell"),
                    (Integer) m.get("speedMoving"),
                    ((Number) m.get("foodToSaturate")).doubleValue(),
                    ((Integer) m.get("cubsPerBirth")),
                    ((List<String>) m.get("diet"))
                            .stream()
                            .map(String::toUpperCase)
                            .map(Edible::valueOf)
                            .collect(Collectors.toSet())
            );
            animalsMap.put(type,params);
        }
    }

    public int getIslandWidth() {
        return islandWidth;
    }

    public int getIslandHeight() {
        return islandHeight;
    }

    public Duration getCycleDurationOfSimulationMs() {
        return cycleDurationOfSimulationMs;
    }

    public int getNumberOfIndividuals() {
        return numberOfIndividuals;
    }

    public boolean isStopConditions() {
        return stopConditions;
    }

    public Map<AnimalType, Integer> getInitialCounts() {
        return initialCounts;
    }

    public Map<AnimalType, Map<Edible, Double>> getProbabilityOfEating() {
        return probabilityOfEating;
    }

    public static Map<AnimalType, AnimalParams> getAnimalsMap() {
        return animalsMap;
    }

}
