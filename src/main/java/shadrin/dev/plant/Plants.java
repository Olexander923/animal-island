package shadrin.dev.plant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import shadrin.dev.animal.Eatable;
import shadrin.dev.field.Location;
import shadrin.dev.config.Edible;


public class Plants implements Eatable {
    private static final Logger log = LoggerFactory.getLogger(Plants.class);
    private double weight;

    public Plants(double weight) {
        this.weight = weight;
    }

    @Override
    public Edible getEdible() {
        return Edible.valueOf(this.getClass().getSimpleName().toUpperCase());
    }

    @Override
    public double getWeight() {
        return weight;
    }

    @Override
    public void removeFrom(Location location) {
        location.getPlants().remove(this);
    }

    /**
     * рост растительности
     */
    public static void grow(Location location,int maxPlantsPerCell){
        int current = location.getPlants().size();
        if(current < maxPlantsPerCell && maxPlantsPerCell > 0) {
            location.getPlants().add(new Plants(1.0));
            log.info("New plant was grown in location: " + location.getX() + ", "
                    + location.getY());
            System.out.println("New plant was grown in location: " + location.getX() + ", "
                    + location.getY());
        }
    }

}
