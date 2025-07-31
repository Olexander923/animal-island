package plant;

import animal.Eatable;
import animal.Location;
import config.Edible;

public class Plants implements Eatable {
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
            System.out.println("New plant was grown in location: " + location.getX() + ", "
            + location.getY());
        }
    }
}
