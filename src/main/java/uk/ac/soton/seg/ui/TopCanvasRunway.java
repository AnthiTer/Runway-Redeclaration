package uk.ac.soton.seg.ui;

import java.io.FileNotFoundException;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;
import uk.ac.soton.seg.util.LogManager;
import uk.ac.soton.seg.util.Logger;

/**
 * Helper function for drawing the top runway
 * Draws the base runway with designator
 */
public class TopCanvasRunway implements CanvasRunway {
    /**
     * Runway markings colour
     */
    private static final Color RUNWAY_MARKINGS_COLOUR = Color.web("#ffffff");

    /**
     * Colour of the runway
     */
    private static final Color RUNWAY_COLOUR = Color.web("#878787");

    private static Logger log = LogManager.getLog(TopCanvasRunway.class.getName());

    //Threshold designator
    String name;
    double x;
    double y;
    double width;
    double height;
    //bearing to draw runway
    double rotation = 0;
    //draw plane grpahic or not
    boolean drawAeroplane;

    public TopCanvasRunway(double length, double width, double x, double y, boolean drawAeroplane) {
        this(length,width,x,y,0,drawAeroplane);
    }

    /**
     * Draw base top view runway, doesn't draw without graphicsContext
     * Draws from a given point
     * @param length length of the runway
     * @param width hieght of the runway
     * @param x x coord of top left point
     * @param y y coord of top left point
     * @param rotation bearing to draw runway
     * @param drawAeroplane whether to draw plane image or not
     */
    public TopCanvasRunway(double length, double width, double x, double y, double rotation, boolean drawAeroplane) {
        this.height = length;
        this.width = width;
        this.x = x;
        this.y = y;
        this.rotation = rotation;
        this.drawAeroplane = drawAeroplane;
    }

    public double getLength() {
        return height;
    }

    /**
     * Draw the basic graphic runway with no distance markers
     * @param name Threshold designator of runway
     * @param gc GraphicsContext2D
     */
    public void drawRunway(String name, GraphicsContext gc) {
        gc.save();
        gc.setStroke(RUNWAY_COLOUR);
        gc.setFill(RUNWAY_COLOUR);
        gc.clearRect(x,y,height,width);
        gc.fillRect(x, y, height, width);
        gc.setLineDashes(5);

        gc.setStroke(RUNWAY_MARKINGS_COLOUR);
        gc.setFill(RUNWAY_MARKINGS_COLOUR);
        gc.strokeLine((x+height*0.1),(y+width/2),(x+height*0.9),(y+width/2));
        drawDesignator(x+ 5,y,name,gc);

        gc.restore();
    }

    /**
     * Draw the name of a runway at a given pos, rotated 90 clockwise
     * @param x x co-ord
     * @param y y co-ord
     * @param text threshold designator
     */
    public void drawDesignator(double x, double y, String text, GraphicsContext gc) {
        gc.save();
        gc.setFill(RUNWAY_MARKINGS_COLOUR);
        gc.setStroke(RUNWAY_COLOUR);
        gc.translate(x, y);
        gc.rotate(90);
        gc.setTextAlign(TextAlignment.LEFT);
        gc.fillText(text, 1, -5,width-1);
        gc.restore();
    }
}
