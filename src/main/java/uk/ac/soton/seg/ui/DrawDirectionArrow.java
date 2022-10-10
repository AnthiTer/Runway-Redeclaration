package uk.ac.soton.seg.ui;

import javafx.geometry.VPos;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;

/**
 * Helper function for drawing the arrow that draws an arrow
 * Extends DistanceMarker
 */
public class DrawDirectionArrow extends DistanceMarker {
    private static final double arrowHeight = 20;
    private static final double arrowLength = 20;
    private static final double textGap = 15;

    /**
     * Draws an arrow given 2 points, doesn't draw until given a GraphicsContext
     * @param title Label for the marker
     * @param x1 x co-ord of the first point
     * @param y1 y co-ord of the first point
     * @param x2 x co-ord of the second point
     * @param y2 y co-ord of the second point
     * @param flipText draw text upside-down (Useful when showing rotated runways)
     */
    public DrawDirectionArrow(String title, double x1, double y1, double x2, double y2, boolean flipText) {
        super(title, x1, y1, x2, y2, flipText);
    }

    public DrawDirectionArrow(String title, double x1, double y1, double x2, double y2) {
        super(title, x1, y1, x2, y2);
    }

    @Override
    public void draw(GraphicsContext gc) {
        gc.save();

        gc.setLineDashes(0);
        gc.setStroke(Color.BLACK);

        var arrowLength = DrawDirectionArrow.arrowLength;
        if (x1 > x2) {
            arrowLength = -arrowLength;
        }

        //draw arrow
        gc.strokeLine(x1,y1,x2 - arrowLength,y2);
        gc.strokeLine(x2 - arrowLength,y2 + (arrowHeight/2),x2 - arrowLength,y2 - (arrowHeight/2));
        gc.strokeLine(x2 - arrowLength,y2 + (arrowHeight/2),x2,y2);
        gc.strokeLine(x2 - arrowLength,y2 - (arrowHeight/2),x2,y2);

        //text
        gc.translate((x1+x2)/2,(y1+y2)/2 + textGap);
        var textOffset = 0;
        if (flipText) {
            gc.rotate(180);
            textOffset = -5;
        }
        gc.setTextAlign(TextAlignment.CENTER);
        gc.setTextBaseline(VPos.CENTER);
        gc.strokeText(title,0,textOffset);

        gc.restore();
    }
}
