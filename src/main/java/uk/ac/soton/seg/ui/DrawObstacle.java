package uk.ac.soton.seg.ui;

import javafx.scene.canvas.GraphicsContext;

/**
 * Helper function for drawing an obstacles on a runway
 * Draws to scale of the canvas, so distances using real world measurement must be converted
 */
public class DrawObstacle {
    double x;
    double y;
    double width;
    double height;

    /**
     * Draws an obstacle from a given point at the top left, doesnt draw until given a GraphicsContext
     * @param x x coord of the top left of the object
     * @param y y coord of the top left of the object
     * @param width width of the shape
     * @param height height of the shape
     */
    public DrawObstacle(double x, double y, double width, double height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public void draw(GraphicsContext gc) {
        gc.clearRect(x,y,width,height);
        gc.strokeRect(x,y,width,height);
    }
}
