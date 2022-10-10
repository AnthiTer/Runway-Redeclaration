package uk.ac.soton.seg.ui;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

import java.io.FileNotFoundException;

/**
 * Helper function for drawing plane in the top canvas view
 */
class DrawTopPlane implements DrawPlane{
    double x;
    double y;

    /**
     * Draws the top view plane, doesn't draw until given GraphicsContext
     * @param x coord of the centre of the plane
     * @param y coord od the centre of the plane
     */
    public DrawTopPlane (double x, double y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public void draw(GraphicsContext gc) throws FileNotFoundException {
        Image image = new Image(getClass().getResourceAsStream("top-airplane.png"));
        gc.drawImage((image), x, y - image.getHeight()/2);
    }
}
