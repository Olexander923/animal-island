package shadrin.dev;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import shadrin.dev.animal.Animal;
import shadrin.dev.emoji.EmojiProvider;
import shadrin.dev.field.Location;
import shadrin.dev.config.AnimalSpawner;
import shadrin.dev.config.AnimalType;
import shadrin.dev.config.EcosystemRules;
import shadrin.dev.config.SimulationConfig;
import shadrin.dev.field.Island;
import shadrin.dev.plant.Plants;

import shadrin.dev.reading_properties.Property;
import shadrin.dev.statistics.StatisticsCollector;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.*;

public class AnimalIslandRepairApplication {
    private static final Logger log = LogManager.getLogger(SimulationConfig.class);

    public static void main(String[] args) throws IOException {
        log.info("Application starts...");
        Property property = new Property();
        //todo убрать путь к файлу в проперти или отдельный конфиг
        String filePath = property.parametersReader();
        log.debug("Loading yaml configuration from: {}",filePath);
        DumperOptions options = new DumperOptions();
        options.setPrettyFlow(true);
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        options.setAllowUnicode(true);

        //чтение из yaml
        Yaml yaml = new Yaml(options);
        try (InputStream is = new FileInputStream(filePath)) {
            Map<String, Object> data = yaml.load(is);
            log.info("Config file loaded successfully");
            StringWriter writer = new StringWriter();
            yaml.dump(data, writer);

            // запись данных в YAML-файл с указанием кодировки UTF-8
            try (BufferedWriter bufferedWriter = Files.newBufferedWriter(Paths.get(filePath),
                    StandardCharsets.UTF_8)) {
                bufferedWriter.write(writer.toString());
                log.debug("Config file rewritten");
            } catch (IOException ex) {
                log.warn("Failed to rewrite config file, but continuing simulation", ex);
            }

            Map<String, Object> root = yaml.load(Files.newInputStream(Path.of(filePath)));
            Map<String, Object> islandYaml = (Map<String, Object>) root.get("island");

            int width = (Integer) islandYaml.get("width");
            int height = (Integer) islandYaml.get("height");
            Duration cycle = Duration.ofMillis((Integer) islandYaml.get("cycleDurationOfSimulationMs"));
            boolean stop = (Boolean) islandYaml.get("stopConditions");
            int maxTicks = (Integer) islandYaml.get("maxTicks");
            log.debug("Island configuration: {}х{}, max ticks: {}, cycle: {}ms",
                    width,height,maxTicks,cycle.toMillis());

            //создаем конфиг для чтения параметров из yaml и инициализации и получения значений
            SimulationConfig simulationConfig = new SimulationConfig(width, height, cycle, stop, maxTicks);
            log.info("Initializing config and get values...");
            simulationConfig.initializeAnimal(Path.of(filePath));

            // инициализация правила экосистемы данными из конфига
            EcosystemRules.setProbabilityOfEating(simulationConfig.getProbabilityOfEating());
            log.info("Plants config - max per cell: {}, initial: {}",
                    simulationConfig.getMaxPlantsPerCell(),
                    simulationConfig.getInitialCounts());
            System.out.println("Max plants per cell = " + simulationConfig.getMaxPlantsPerCell());
            System.out.println("Initial plants count = " + simulationConfig.getInitialCounts());

            //создаем остров
            Island island = new Island(
                    simulationConfig.getIslandWidth(),
                    simulationConfig.getIslandHeight());
            //создаем всех животных
            AnimalSpawner animalSpawner = new AnimalSpawner();
            animalSpawner.spawnInitial(island, simulationConfig);
            log.info("Island created: {}х{}, animals spawned",island.getWIDTH(),island.getHEIGHT());


            ScheduledExecutorService executorService = Executors.newScheduledThreadPool(3);
            ThreadPoolExecutor executor = (ThreadPoolExecutor) executorService;
            log.info("pool size: {}, Active threads: {}, Queued tasks: {}, Completed tasks: {}",
                    executor.getPoolSize(),
                    executor.getActiveCount(),
                    executor.getQueue().size(),
                    executor.getCompletedTaskCount());

            Runnable taskPlants = () -> { //запуск потока для растений
                Location[][] plantsLocation = island.getLocations();
                ThreadLocalRandom rand = ThreadLocalRandom.current();
                for (int x = 0; x < island.getWIDTH(); x++) {
                    for (int y = 0; y < island.getHEIGHT(); y++) {
                        if (rand.nextDouble() < 0.05) { //рост растений будет только в 5% случаев
                            Plants.grow(plantsLocation[x][y], simulationConfig.getMaxPlantsPerCell());
                            System.out.println("Called grow for location: " + x + ", " + y);
                        }
                        //дополнительное условие -
                        // естественную смерть растений(2% умирает от текущего количества каждый такт)
                        Location cell = plantsLocation[x][y];
                        Set<Plants> plants = cell.getPlants();
                        int plantsCount = plants.size();//получаем сколько растений в клетке
                        int toPlantWither = Math.max(2, plantsCount / 100);
                        //список с увядающими растениями
                        List<Plants> witheringPlants = new ArrayList<>(plants).subList(0, toPlantWither);
                        for (Plants p : witheringPlants) {
                            p.removeFrom(cell);
                        }

                    }
                }
            };

            Runnable animalTask = () -> {//запуск потока для животных
                Location[][] animalsLocation = island.getLocations();
                for (int x = 0; x < island.getWIDTH(); x++) {
                    for (int y = 0; y < island.getHEIGHT(); y++) {
                        Set<Animal> deadAnimals = new HashSet<>();
                        Set<Animal> animals = animalsLocation[x][y].getAnimals();
                        for (Animal animal : animals) {
                            animal.decreaseSatiety(); //уменьшаем сытость
                            if (animal.getSatiety() <= 0) {
                                animal.setAlive(false);
                                continue; // переход к след. животному
                            }

                            animal.eat();
                            animal.multiply();
                            animal.chooseDirectionOfMovement();

                        }
                        for (Animal deadAnimal : deadAnimals) {
                            deadAnimal.removeFrom(animalsLocation[x][y]);
                        }
                        //System.out.println("Animals starts eat,multiply etc");
                        log.debug("Processing animals on location [{}, {}]", x, y);

                    }
                }
            };

            Runnable statisticsTask = () -> {//поток для статистики
                if (simulationConfig.getTickCount().get() % 2 == 0) {//сократил вывод статистики раз в 5 такстов

                    StatisticsCollector statisticsCollector = new StatisticsCollector();
                    Map<AnimalType, Integer> animalCounts = statisticsCollector.collectAnimalCounts(island);
                    StringBuilder emojiStrings = new StringBuilder();
                    for (AnimalType type : AnimalType.values()) {
                        int count = animalCounts.get(type);
                        String emoji = EmojiProvider.getEmoji(type);
                        String resultString = emoji + " = " + count + ";";
                        emojiStrings.append(resultString);
                    }

                    System.out.println("Quantity of animals" + emojiStrings);

                    int plantsCount = statisticsCollector.collectPlantsCounts(island);
                    System.out.println(EmojiProvider.getEmojiPlant() + " Quantity of plants " + plantsCount);
                    log.info(EmojiProvider.getEmojiPlant() + " Quantity of plants " + plantsCount);
                }

            };
            log.info("Set delay between task in executor");
            executorService.scheduleAtFixedRate(taskPlants, 0, 10, TimeUnit.SECONDS);
            executorService.scheduleAtFixedRate(animalTask, 1, 5, TimeUnit.SECONDS);
            executorService.scheduleAtFixedRate(statisticsTask, 2, 5, TimeUnit.SECONDS);

            try {
                while (!EcosystemRules.conditionStopSimulation(island) &&
                        simulationConfig.getTickCount().get() < simulationConfig.getMaxTicks()) {
                    int currentTick = simulationConfig.getTickCount().incrementAndGet();
                    log.debug("Starting simulation tick: {}", currentTick);
                    try {
                        TimeUnit.SECONDS.sleep(1);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        log.error("Main simulation thread interrupted,stopping simulation", e.getMessage());
                        break;
                    }
                }
                int finalTickCount = simulationConfig.getTickCount().get();
                if (EcosystemRules.conditionStopSimulation(island)) {
                    log.info("Simulation stopped: Animals died. Final tick: {}", finalTickCount);
                } else if (finalTickCount >= simulationConfig.getMaxTicks()) {
                    log.info("Simulation stopped: Maximum tick limit reached: {}", simulationConfig.getMaxTicks());
                }

                log.info("Shutting down executor service ...");
                executorService.shutdown();

                try {
                    if (!executorService.awaitTermination(5, TimeUnit.SECONDS)) {//ждем завершения задач
                        log.warn("Executor did not terminate in the specified times,forcing shutdown");
                        executorService.shutdownNow();
                        if (!executorService.awaitTermination(5, TimeUnit.SECONDS)) {
                            log.error("Executor did not terminate after shutdownNow.");
                        }
                    }

                } catch (InterruptedException e) {
                    log.error("Interrupted while waiting for executor termination", e);
                    executorService.shutdownNow();
                    Thread.currentThread().interrupt();
                }
                log.info("Application has shut down gracefully. Final tick: {}", finalTickCount);

            } catch (Exception e) {
                log.error("Unexpected error of simulation", e);
            }
        }
    }
}


