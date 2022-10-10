package uk.ac.soton.seg.ui;

import javafx.scene.canvas.GraphicsContext;

import java.io.FileNotFoundException;

/**
 * Interface for the helper functions that draw plane images on the canvas
 */
public interface DrawPlane {
    public void draw(GraphicsContext gc) throws FileNotFoundException;
}
