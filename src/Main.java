import config.AnimalParams;
import config.AnimalSpawner;
import config.SimulationConfig;
import field.Island;

import java.io.IOException;
import java.nio.file.Path;
import java.time.Duration;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {
    public static void main(String[] args) throws IOException {
        ExecutorService executorService = Executors.newScheduledThreadPool(10);


        SimulationConfig simulationConfig = new SimulationConfig(
                100,
                20,
                Duration.ofMillis(1000),
                50,
                false,
                new ConcurrentHashMap<>());
        simulationConfig.initializeAnimal(Path.of("resources/parameter.yaml"));

        //создаем остров
        Island island = new Island(
                simulationConfig.getIslandWidth(),
                simulationConfig.getIslandHeight());

        AnimalSpawner animalSpawner = new AnimalSpawner();
        animalSpawner.spawnInitial(island,simulationConfig);
    }
}