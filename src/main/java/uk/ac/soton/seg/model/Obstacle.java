package uk.ac.soton.seg.model;

/**
 * Represents a single obstacle stored for later use
 */
public class Obstacle {
    public static final int MIN_DIMENSION = 1;
    public static final int MAX_DIMENSION = 1000;

    private String name;
    private int height;
    private int width;
    private int length;

    /**
     * Create a obstacle with no values.
     */
    public Obstacle() {
    }

    /**
     * Create a new obstacle with given parameters.
     * 
     * @param name   name of the obstacle
     * @param height height of the obstacle
     * @param width  width of the obstacle
     * @param length length of the obstacle
     */
    public Obstacle(String name, int height, int width, int length) {
        this.name = name;
        this.height = height;
        this.width = width;
        this.length = length;
    }

    /**
     * @return name of the obstacle.
     */
    public String getName() {
        return name;
    }

    /**
     * Get the height of the obstacle (perpendicular to runway plane).
     * @return size in meters
     */
    public int getHeight() {
        return height;
    }

    /**
     * Get the width of the obstacle. (perpendicular to runway direction, on runway plane).
     * @return size in meters
     */
    public int getWidth() {
        return width;
    }

    /**
     * Get the length of the obstacle (parallel to runway direction).
     * @return size in meters
     */
    public int getLength() {
        return length;
    }

    /**
     * Set the name of the obstacle.
     * 
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Set the height of the obstacle.
     * 
     * @param height height in meters (must be MIN_DIMENSION <= x <= MAX_DIMENSION)
     */
    public void setHeight(int height) {
        if (height < MIN_DIMENSION || height > MAX_DIMENSION)
            throw new IllegalArgumentException();

        this.height = height;
    }

    /**
     * Set the width of the obstacle.
     * 
     * @param width width in meters (must be MIN_DIMENSION <= x <= MAX_DIMENSION)
     */
    public void setWidth(int width) {
        if (width < MIN_DIMENSION || width > MAX_DIMENSION)
            throw new IllegalArgumentException();

        this.width = width;
    }

    /**
     * Set the length of the obstacle.
     * 
     * @param length length in meters (must be MIN_DIMENSION <= x <= MAX_DIMENSION)
     */
    public void setLength(int length) {
        if (length < MIN_DIMENSION || length > MAX_DIMENSION)
            throw new IllegalArgumentException();

        this.length = length;
    }

}
