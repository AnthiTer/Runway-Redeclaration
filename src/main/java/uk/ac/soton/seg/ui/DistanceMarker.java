package uk.ac.soton.seg.ui;

import javafx.geometry.VPos;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;

/**
 * Helper function for drawing a marker to display a scale measurement in the canvas
 */
public class DistanceMarker {
    static double width = 5;
    static double textGap = 25;

    double x1;
    double y1;
    double x2;
    double y2;
    String title;

    //Draw text upside down
    //Useful for when the canvas will be rotated so text can still be read
    boolean flipText = false;

    /**
     * Constructs a distance market, a marked length with a given title between 2 points, doesn't draw until given a GraphicsContext
     * @param title Label for the marker
     * @param x1 x co-ord of the first point
     * @param y1 y co-ord of the first point
     * @param x2 x co-ord of the second point
     * @param y2 y co-ord of the second point
     * @param flipText draw text upside-down (Useful when showing rotated runways)
     */
    public DistanceMarker(String title,double x1, double y1, double x2, double y2, boolean flipText) {
        this.title = title;
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
        this.flipText = flipText;
    }

    public DistanceMarker(String title,double x1, double y1, double x2, double y2) {
        this(title,x1,y1,x2,y2,false);
    }

    /**
     * Draws the distance marker
     * @param gc GraphicsContext
     */
    public void draw(GraphicsContext gc) {
        if (x1 == x2) {
            return;
        }
        gc.save();
        //line
        gc.setLineDashes(0);
        gc.setStroke(Color.BLACK);
        gc.strokeLine(x1,y1+width,x1,y1-width);
        gc.strokeLine(x2,y2+width,x2,y2-width);
        //If gap for text is larger than the actual line
        if ((Math.abs(x1 - x2) <= textGap * 2) || (title == "")) {
            //Do not make space for text
            gc.strokeLine(x1,y1,(x1+x2)/2,y2);
            gc.strokeLine((x1+x2)/2,y2,x2,y2);
            //text
            gc.translate(x2,(y1+y2)/2);
            if (flipText) {
                gc.rotate(180);
            }
            gc.setTextBaseline(VPos.CENTER);
            gc.strokeText(title,10,0);
        } else {
            //make space for text
            gc.strokeLine(x1,y1,(x1+x2)/2 - textGap,y2);
            gc.strokeLine((x1+x2)/2 + textGap,y2,x2,y2);
            //text
            gc.translate((x1+x2)/2,(y1+y2)/2);
            if (flipText) {
                gc.rotate(180);
            }
            gc.setTextAlign(TextAlignment.CENTER);
            gc.setTextBaseline(VPos.CENTER);
            gc.strokeText(title,0,0);
        }

        gc.restore();
    }
}
