package uk.ac.soton.seg.ui;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 * Helper class to draw the displaced threshold onto the canvas
 */
public class DisplacedThreshold {
    double x1;
    double y1;
    double x2;
    double y2;

    /**
     * Construct the class for drawing the DisplacedThreshold, does not draw until given the GraphicsContext
     * @param x1 x coord of first point
     * @param y1 y coord of first point
     * @param x2 x coord of second point
     * @param y2 y coord of second point
     */
    public DisplacedThreshold(double x1, double y1, double x2, double y2) {
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
    }

    public void draw(GraphicsContext gc) {
        gc.setLineDashes(0);
        gc.setStroke(Color.BLACK);
        gc.strokeLine(x1,y1,x2,y2);
    }
}
