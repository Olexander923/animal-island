package shadrin.dev.config;

import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class SimulationConfig {
    private final  int islandWidth;//ширина острова
    private final  int islandHeight;//высота острова
    private final  Duration cycleDurationOfSimulationMs;//Длительность одного такта симуляции в миллисекундах.
    //private final  int numberOfIndividuals;//Стартовое количество особей каждого вида
    private int maxPlantsPerCell; //максимальное кол-во растений на клетку, будет читаться из yaml
    private int initialPlantsCount;
    private final  boolean stopConditions;//Условие остановки (например, «все животные мертвы» или «прошло N тактов»).
    private final int maxTicks;//максимальное кол-во тактов, за которое происходят задачи(рост растений, цикл животных,статистика)
    private Map<AnimalType,Map<Edible,Double>> probabilityOfEating = new ConcurrentHashMap<>();//матрица вероятности добычи в %
    public static Map<AnimalType,AnimalParams> animalsMap = new ConcurrentHashMap<>(); //для хранения параметров животных
    private final Map<AnimalType,Integer> initialCounts = new ConcurrentHashMap<>();//мапа для подсчета создания необходимого количества животных
    private  AtomicInteger tickCount = new AtomicInteger(0);//счетчик тактов


    public SimulationConfig(int islandWidth,
                            int islandHeight,
                            Duration cycleDurationOfSimulationMs,
                            boolean stopConditions,
                            int maxTicks) {
        this.islandWidth = islandWidth;
        this.islandHeight = islandHeight;
        this.cycleDurationOfSimulationMs = cycleDurationOfSimulationMs;
        this.stopConditions = stopConditions;
        this.maxTicks = maxTicks;

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
        Map<String, Object> root = yaml.load(Files.newInputStream(yamlPath));
        //временный вовод для дебага
        System.out.println("Root keys: " + root.keySet());
        System.out.println("Root content: " + root);


        this.maxPlantsPerCell = (Integer) root.getOrDefault("maxPlantsPerCell", 200);
        this.initialPlantsCount = (Integer) root.getOrDefault("initialPlantsCount", 120);

        Map<String, Map<String, Object>> animalsYaml = (Map<String, Map<String, Object>>) root.get("animals");
        for (Map.Entry<String, Map<String, Object>> e : animalsYaml.entrySet()) {
            //проверка, если попадается растения, то ловим ошибку и пропускаем,т.е. она не входит в enum
            String key = e.getKey().toUpperCase();
            AnimalType type;
            try {
                type = AnimalType.valueOf(key);
            } catch (IllegalArgumentException ex) {
                continue;
            }
            Map<String, Object> m = e.getValue();
            int initial = (Integer) m.getOrDefault("initialCount", 0);

            initialCounts.put(type, initial);
            AnimalParams params = new AnimalParams(
                    ((Number) m.get("weight")).doubleValue(),
                    (Integer) m.get("maxNumberOfAnimalsPerCell"),
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

        //парсинг конфига для получения вероятности поедения
        Map<String, Map<String, Double>> probabilityYaml = (Map<String, Map<String, Double>>) root.get("probabilityOfEating");
        if (probabilityYaml != null) {
            this.probabilityOfEating = new ConcurrentHashMap<>();

            for (Map.Entry<String, Map<String, Double>> animalEntry : probabilityYaml.entrySet()) {
                try {
                    AnimalType animalType = AnimalType.valueOf(animalEntry.getKey().toUpperCase());
                    Map<Edible, Double> edibleChances = new ConcurrentHashMap<>();

                    for (Map.Entry<String, Double> edibleEntry : animalEntry.getValue().entrySet()) {
                        try {
                            Edible edible = Edible.valueOf(edibleEntry.getKey().toUpperCase());
                            edibleChances.put(edible, edibleEntry.getValue());
                        } catch (IllegalArgumentException e) {
                            System.err.println("Unknown edible type in probability matrix: " + edibleEntry.getKey());
                        }
                    }
                    this.probabilityOfEating.put(animalType, edibleChances);
                } catch (IllegalArgumentException e) {
                    System.err.println("Unknown animal type in probability matrix: " + animalEntry.getKey());
                }
            }

        } else {
            System.err.println("Warning: probabilityOfEating section not found in YAML config!");
            this.probabilityOfEating = new ConcurrentHashMap<>();
        }
        // инициализируем EcosystemRules после загрузки всех данных
        EcosystemRules.setProbabilityOfEating(this.probabilityOfEating);
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


    public int getMaxPlantsPerCell() {return maxPlantsPerCell;}

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

    public int getMaxTicks() {
        return maxTicks;
    }

    public AtomicInteger getTickCount() {
        return tickCount;
    }


}
