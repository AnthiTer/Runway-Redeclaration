package uk.ac.soton.seg.ui;

import java.io.FileNotFoundException;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.transform.Affine;
import javafx.scene.transform.Rotate;

/**
 * Helper function the draws the side plane image on the canvas
 */
public class DrawSidePlane implements DrawPlane{
    double x;
    double y;
    double rotation = 0;
    boolean faceRight = true;

    public DrawSidePlane (double x, double y) {
        this(x,y,0,true);
    }

    /**
     * Draws the plane given a point, rotation and direction it should face, doesn't draw until given a GraphicsContext
     * @param x x coord of the front of the plane
     * @param y y coord of the front of the plane
     * @param rotation degrees to rotate the plane clockwise
     * @param faceRight draw plane facing right or left
     */
    public DrawSidePlane (double x, double y, double rotation, boolean faceRight) {
        this.x = x;
        this.y = y;
        this.rotation = rotation;
        this.faceRight = faceRight;
    }

    @Override
    public void draw(GraphicsContext gc) throws FileNotFoundException {
        Image image = new Image(getClass().getResourceAsStream("side-airplane.png"),50.0,20.0,true,false);
        gc.save();
        gc.transform(new Affine(new Rotate(rotation,x,y-(image.getHeight()*0.6))));
        if (faceRight) {
            gc.drawImage((image), x, y-(image.getHeight()*0.6));
        } else {
            gc.drawImage(image, 0, 0, image.getWidth(), image.getHeight(), x,y-(image.getHeight()*0.6),-image.getWidth(),image.getHeight());
        }
        gc.restore();
    }
}
