import animal.Animal;
import animal.Location;
import config.*;
import field.Island;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import plant.Plants;
import statistics.StatisticsCollector;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.*;

public class Main {
    public static void main(String[] args) throws IOException {
        String filePath = "resources/parameter.yaml";
        DumperOptions options = new DumperOptions();
        options.setPrettyFlow(true);
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        options.setAllowUnicode(true);

        //чтение из yaml
        Yaml yaml = new Yaml(options);
        try (InputStream is = new FileInputStream(filePath)) {
            Map<String, Object> data = yaml.load(is);
            StringWriter writer = new StringWriter();
            yaml.dump(data, writer);

            // запись данных в YAML-файл с указанием кодировки UTF-8
            try (BufferedWriter bufferedWriter = Files.newBufferedWriter(Paths.get(filePath),
                    StandardCharsets.UTF_8)) {
                bufferedWriter.write(writer.toString());
            } catch (IOException ex) {
                ex.printStackTrace();
            }

            Map<String, Object> root = yaml.load(Files.newInputStream(Path.of("resources/parameter.yaml")));
            Map<String, Object> islandYaml = (Map<String, Object>) root.get("island");

            int width = (Integer) islandYaml.get("width");
            int height = (Integer) islandYaml.get("height");
            Duration cycle = Duration.ofMillis((Integer) islandYaml.get("cycleDurationOfSimulationMs"));
            boolean stop = (Boolean) islandYaml.get("stopConditions");
            int maxTicks = (Integer) islandYaml.get("maxTicks");

            //создаем конфиг для чтения параметров из yaml и инициализации и получения значений
            SimulationConfig simulationConfig = new SimulationConfig(width, height, cycle, stop, maxTicks);
            simulationConfig.initializeAnimal(Path.of("resources/parameter.yaml"));
            //simulationConfig.initializeAnimal(Files.newBufferedWriter(Paths.get("resources/parameter.yaml")));

            // инициализация правила экосистемы данными из конфига
            EcosystemRules.setProbabilityOfEating(simulationConfig.getProbabilityOfEating());

            System.out.println("Max plants per cell = " + simulationConfig.getMaxPlantsPerCell());
            System.out.println("Initial plants count = " + simulationConfig.getInitialCounts());

            //создаем остров
            Island island = new Island(
                    simulationConfig.getIslandWidth(),
                    simulationConfig.getIslandHeight());
            //создаем всех животных
            AnimalSpawner animalSpawner = new AnimalSpawner();
            animalSpawner.spawnInitial(island, simulationConfig);


            ScheduledExecutorService executorService = Executors.newScheduledThreadPool(3);

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
                        // "естественную смерть растений"(2% умирает от текущего количества каждый такт)
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
                            if (animal.getSatiety() <= 0){
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
                        System.out.println("Animals starts eat,multiply etc");

                    }
                }
            };

            Runnable statisticsTask = () -> {//поток для статистики
                if (simulationConfig.getTickCount().get() % 2 == 0) {//сократил вывод статистика раз в 5 такстов

                    StatisticsCollector statisticsCollector = new StatisticsCollector();
                    Map<AnimalType, Integer> animalCounts = statisticsCollector.collectAnimalCounts(island);
                    System.out.println("Quantity of animals" + animalCounts);

                    int plantsCount = statisticsCollector.collectPlantsCounts(island);
                    System.out.println("Quantity of plants" + plantsCount);
                }

            };

            executorService.scheduleAtFixedRate(taskPlants, 0, 10, TimeUnit.SECONDS);
            executorService.scheduleAtFixedRate(animalTask, 1, 5, TimeUnit.SECONDS);
            executorService.scheduleAtFixedRate(statisticsTask, 2, 5, TimeUnit.SECONDS);

            //AtomicInteger tickCount = new AtomicInteger(0);//счетчик тактов
            while (!EcosystemRules.conditionStopSimulation(island) ||
                    simulationConfig.getTickCount().get() >= simulationConfig.getMaxTicks()) {
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                simulationConfig.getTickCount().incrementAndGet();
            }
            executorService.shutdown();
        }
    }
}
