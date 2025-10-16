package shadrin.dev.field;

/**
 * создание матрицы острова, вводим координаты x(ширина),y(высота)
 */
public class Island {
    private final int WIDTH;
    private final int HEIGHT;
    private final Location[][] locations;

    public Island(int WIDTH, int HEIGHT) {
        this.WIDTH = WIDTH;
        this.HEIGHT = HEIGHT;
        this.locations = new Location[WIDTH][HEIGHT];


        for (int x = 0; x < WIDTH; x++) {
            for (int y = 0; y < HEIGHT; y++) {
                locations[x][y] = new Location(x,y);
            }
        }
    }

    public Location[][] getLocations() {
        return locations;
    }

    public int getWIDTH() {
        return WIDTH;
    }

    public int getHEIGHT() {
        return HEIGHT;
    }
}
