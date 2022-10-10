package uk.ac.soton.seg.ui;

import javafx.scene.canvas.GraphicsContext;

/**
 * Interface for drawing the 2 views on each canvas
 */
public interface CanvasRunway {
    public void drawRunway(String name, GraphicsContext gc);
}
