package uk.ac.soton.seg.util;

import javafx.geometry.Bounds;
import javafx.scene.input.ScrollEvent;
import javafx.scene.input.ZoomEvent;
import uk.ac.soton.seg.model.Runway;
import uk.ac.soton.seg.ui.RunwayCanvas;

//Class for helper functions when drawing UI in canvas
public class UIHelper {
    private UIHelper() {}

    public static void zoom(RunwayCanvas node, double factor, double x, double y) {
        if (factor <= 0.7) {factor = 0.7;}
        else if (factor >= 1.3) {factor = 1.3;}

        double oldScale = node.getXScale();
        double scale = oldScale * factor;
        if (scale < 0.2) scale = 0.2;
        if (scale > 10)  scale = 10;
        node.setXScale(scale);
        node.setYScale(scale);

        double  f = 1 - scale ;
        node.setTransX((node.getWidth() * f) / 2);
        node.setTransY((node.getHeight() * f) / 2);

        node.draw();
    }

    public static void zoom(RunwayCanvas node, ScrollEvent event) {
        zoom(node, Math.pow(1.01, event.getDeltaY()), event.getSceneX(), event.getSceneY());
    }

    public static void zoom(RunwayCanvas node, ZoomEvent event) {
        zoom(node, event.getZoomFactor(), event.getSceneX(), event.getSceneY());
    }

    public static void scale(RunwayCanvas node, double scale) {
        double oldScale = node.getXScale();
        if (scale < 0.2) scale = 0.2;
        if (scale > 10)  scale = 10;
        node.setXScale(scale);
        node.setYScale(scale);

        double  f = 1 - scale ;
        node.setTransX((node.getWidth() * f) / 2);
        node.setTransY((node.getHeight() * f) / 2);

        node.draw();
    }
}
