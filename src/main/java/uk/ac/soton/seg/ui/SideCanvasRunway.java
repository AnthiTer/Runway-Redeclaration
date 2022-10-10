package uk.ac.soton.seg.ui;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;

/**
 * Helper function for drawing the side runway
 * Draws the base runway with designator
 */
public class SideCanvasRunway implements CanvasRunway {
    //Threshold designator
    String name;
    double x;
    double y;
    double width;
    double height;

    /**
     * Draws the runway, doesn't draw without graphicsContext
     * Draws from a given point at the bottom left of the runway
     * @param length length of the runway
     * @param height height of the drawn runway
     * @param x x coord of the left of the runway
     * @param y y coord of the left of the runway
     */
    public SideCanvasRunway(double length, double height, double x, double y) {
        this.height = height; //Currently unused
        this.width = length;
        this.x = x;
        this.y = y;
    }

    public void drawRunway(String name, GraphicsContext gc) {
        gc.setStroke(Color.BLACK);
        gc.strokeLine(x,y,x+width,y);
        drawDesignator(x,y,name,gc);
    }

    /**
     * Draw the name of a runway at a given pos, rotated 90 clockwise
     * @param x x co-ord
     * @param y y co-ord
     * @param text threshold designator
     */
    public void drawDesignator(double x, double y, String text, GraphicsContext gc) {
        gc.save();
        gc.translate(x, y);
        gc.rotate(90);
        gc.setTextAlign(TextAlignment.LEFT);
        gc.fillText(text, 5, 0);
        gc.restore();
    }
}
