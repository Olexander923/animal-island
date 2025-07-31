import animal.Animal;
import animal.Location;
import config.*;
import field.Island;
import org.yaml.snakeyaml.Yaml;
import plant.Plants;
import statistics.StatisticsCollector;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.*;

public class Main {
    public static void main(String[] args) throws IOException {
        //чтение из yaml
        Yaml yaml = new Yaml();
        Map<String,Object> root = yaml.load(Files.newInputStream(Path.of("resources/parameter.yaml")));
        Map<String,Object> islandYaml = (Map<String, Object>) root.get("island");

        int width = (Integer) islandYaml.get("width");
        int height = (Integer) islandYaml.get("height");
        Duration cycle = Duration.ofMillis((Integer) islandYaml.get("cycleDurationOfSimulationMs"));
        boolean stop = (Boolean) islandYaml.get("stopConditions");
        int maxTicks = (Integer) islandYaml.get("maxTicks");

        //создаем конфиг для чтения параметров из yaml и инициализации и получения значений
        SimulationConfig simulationConfig = new SimulationConfig(width,height,cycle,stop,maxTicks);
        simulationConfig.initializeAnimal(Path.of("resources/parameter.yaml"));

        //создаем остров
        Island island = new Island(
                simulationConfig.getIslandWidth(),
                simulationConfig.getIslandHeight());
        //создаем всех животных
        AnimalSpawner animalSpawner = new AnimalSpawner();
        animalSpawner.spawnInitial(island,simulationConfig);

        ScheduledExecutorService executorService = Executors.newScheduledThreadPool(3);

        Runnable taskPlants = () -> { //запуск потока для растений
            Location[][] plantsLocation = island.getLocations();
            for (int x = 0; x < island.getWIDTH(); x++) {
                for (int y= 0; y < island.getHEIGHT(); y++) {
                    Plants.grow(plantsLocation[x][y],simulationConfig.getMaxPlantsPerCell());
                    System.out.println("Called grow for location: " + x + ", " + y);
                }
            }
        };


        Runnable animalTask = () -> {//запуск потока для животных
            Location[][] animalsLocation = island.getLocations();
            for (int x = 0; x < island.getWIDTH(); x++) {
                for (int y = 0; y < island.getHEIGHT(); y++) {
                    Set<Animal> animals = animalsLocation[x][y].getAnimals();
                    for (Animal animal : animals) {
                        animal.eat();
                        animal.multiply();
                        animal.chooseDirectionOfMovement();
                        System.out.println("Animals starts eat,multiply etc");

                    }
                }
            }
        };

        Runnable statisticsTask = () -> {//поток для статистики
            StatisticsCollector statisticsCollector = new StatisticsCollector();
            Map<AnimalType,Integer> animalCounts = statisticsCollector.collectAnimalCounts(island);
            System.out.println("Quantity of animals" + animalCounts);

            int plantsCount =  statisticsCollector.collectPlantsCounts(island);
            System.out.println("Quantity of plants" + plantsCount);

        };

        executorService.scheduleAtFixedRate(taskPlants,0,5,TimeUnit.SECONDS);
        executorService.scheduleAtFixedRate(animalTask,1,5,TimeUnit.SECONDS);
        executorService.scheduleAtFixedRate(statisticsTask,2,5,TimeUnit.SECONDS);

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
