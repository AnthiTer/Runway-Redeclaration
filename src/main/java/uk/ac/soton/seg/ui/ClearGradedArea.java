package uk.ac.soton.seg.ui;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

/**
 * Helper class for drawing the clear and graded area in the top down canvas
 */
public class ClearGradedArea {
    //centre co-ords
    double x;
    double y;
    //width & height of entire shape
    double width;
    double height;
    //distance from edges to start drawing bevel
    double insetMin;
    double insetMax;
    //height of bevel
    double bevelMin;
    double bevelMax;

    /**
     * Colour of area around cleared and graded area
     */
    static final Paint CGA_COLOUR = Color.web("#aa71ff");

    /**
     * Colour of cleared and graded area
     */
    static final Paint OUTSIDE_CGA_COLOUR = Color.web("#62d26f");

    /**
     * Construct the class for drawing the area, does not draw until given the GraphicsContext
     * @param x centre x coord of the centre of the area
     * @param y centre y coord of the centre of the area
     * @param width width of the entire area
     * @param height height of the entire area
     * @param insetMin distance to start drawing the bevel from horizontally
     * @param insetMax distance to draw the bevel to horizontally
     * @param bevelMin distance to start drawing the bevel from vertically
     * @param bevelMax distance to draw the bevel to vertically
     */
    public ClearGradedArea(double x, double y, double width, double height, double insetMin, double insetMax, double bevelMin, double bevelMax) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.insetMin = insetMin;
        this.insetMax = insetMax;
        this.bevelMin = bevelMin;
        this.bevelMax = bevelMax;
    }

    /**
     * Draw the area onto a canvas
     * @param gc GraphicsContext
     */
    public void draw(GraphicsContext gc) {
        gc.save();
        gc.setStroke(Color.DARKGRAY);
        gc.setFill(CGA_COLOUR);
        gc.fillRect(x-(width/2),y-(height/2),width,height);
        gc.strokeRect(x-(width/2),y-(height/2),width,height);

        gc.setFill(OUTSIDE_CGA_COLOUR);
        var xs = new double[]{
                x-(width/2),
                x-(width/2)+insetMin,
                x-(width/2)+insetMax,
                x+(width/2)-insetMax,
                x+(width/2)-insetMin,
                x+(width/2),
                x+(width/2),
                x+(width/2)-insetMin,
                x+(width/2)-insetMax,
                x-(width/2)+insetMax,
                x-(width/2)+insetMin,
                x-(width/2)
        };

        var ys = new double[] {
                y+bevelMin,
                y+bevelMin,
                y+bevelMax,
                y+bevelMax,
                y+bevelMin,
                y+bevelMin,
                y-bevelMin,
                y-bevelMin,
                y-bevelMax,
                y-bevelMax,
                y-bevelMin,
                y-bevelMin
        };
        gc.fillPolygon(xs,ys,12);

        gc.beginPath();
        gc.moveTo(x-(width/2),y+bevelMin);
        gc.lineTo(x-(width/2)+insetMin,y+bevelMin);
        gc.lineTo(x-(width/2)+insetMax,y+bevelMax);
        gc.lineTo(x+(width/2)-insetMax,y+bevelMax);
        gc.lineTo(x+(width/2)-insetMin,y+bevelMin);
        gc.lineTo(x+(width/2),y+bevelMin);

        gc.lineTo(x+(width/2),y-bevelMin);
        gc.lineTo(x+(width/2)-insetMin,y-bevelMin);
        gc.lineTo(x+(width/2)-insetMax,y-bevelMax);
        gc.lineTo(x-(width/2)+insetMax,y-bevelMax);
        gc.lineTo(x-(width/2)+insetMin,y-bevelMin);
        gc.lineTo(x-(width/2),y-bevelMin);

        gc.stroke();
        gc.closePath();
        gc.restore();
    }

}
