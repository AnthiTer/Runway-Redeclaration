package uk.ac.soton.seg.ui;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import uk.ac.soton.seg.event.RunwayCanvasListener;
import uk.ac.soton.seg.model.Calc;
import uk.ac.soton.seg.util.UIHelper;

public abstract class RunwayCanvas extends Canvas {

    /** Whether to draw the distance markers or not */
    protected boolean drawMarkers = true;

    protected RunwayCanvasListener runwayCanvasListener;

    /** Runway Designator (Name of the runway) */
    protected String name;

    /** Whether to draw the plane images or not */
    protected boolean showAeroplane = true;

    /**
     * Height of runway in units. Size of runway does not change, objects are drawn
     * relative to the scale of the runway
     */
    protected double runwayHeight = 30;

    /**
     * Width of runway in units. Size of runway does not change, objects are drawn
     * relative to the scale of the runway
     */
    protected double runwayLength = 350;

    // Respective lengths to draw runway distance markers
    protected double LDAlength = 0;
    protected double TORAlength = 0;
    protected double TODAlength = 0;
    protected double ASDAlength = 0;
    protected double displacedThreshold = 0;
    protected double stripEndLength = 0;
    protected double RESAlength = 0;
    protected double blastLength = 0;
    protected double stopwayLength = 0;
    protected double clearwayLength = 0;

    /** Current state of model, changes UI view */
    protected Calc.FlightModes state = Calc.FlightModes.values()[0];

    // Obstacle size metrics
    protected double obstacleWidth = 0;
    protected double obstacleHeight = 0;
    protected int obstacleDistance = 0;
    protected int obstacleThres = 0;

    protected double xScale = 1;
    protected double yScale = 1;

    protected double transX = 0;
    protected double transY = 0;

    protected GraphicsContext gc;

    RunwayCanvas() {
        this.setVisible(false);
        widthProperty().addListener(evt -> draw());
        heightProperty().addListener(evt -> draw());

        this.setOnScroll((e) -> {
            UIHelper.zoom(this, e);
        });



        this.addEventHandler(
                MouseEvent.MOUSE_CLICKED,
                (MouseEvent e) -> {
                    if (runwayCanvasListener != null)
                        runwayCanvasListener.canvasClicked(e);
                });
    }

    protected void setGc() {
        GraphicsContext gc = getGraphicsContext2D();
        this.gc = gc;
    }

    protected void drawArrow(String title) {
        drawArrow(title, true, false);
    }
    protected void drawArrow(String title, boolean faceRight) {
        drawArrow(title, faceRight, false);
    }

    /**
     * Draw the arrow that shows the user the movement of the shown plane
     * 
     * @param title     Text that describes arrow
     * @param faceRight Draw arrow facing right or left
     * @param flipText Draw the text upside-down or not
     */
    protected void drawArrow(String title, boolean faceRight, boolean flipText) {
        if(!drawMarkers) {
            return;
        }

        if (faceRight) {
            var arrow = new DrawDirectionArrow(title,
                    (this.getWidth() - runwayLength) / 2,
                    ((this.getHeight() + runwayHeight) / 2 + 15),
                    (this.getWidth() - runwayLength * 0.5) / 2 - 20,
                    ((this.getHeight() + runwayHeight) / 2 + 15),
                    flipText);
            arrow.draw(gc);
        } else {
            var arrow = new DrawDirectionArrow(title,
                    (this.getWidth() - runwayLength * 0.5) / 2 - 20,
                    ((this.getHeight() + runwayHeight) / 2 + 15),
                    (this.getWidth() - runwayLength) / 2,
                    ((this.getHeight() + runwayHeight) / 2 + 15),
                    flipText);
            arrow.draw(gc);
        }
    }

    public void setXScale (double x) {xScale = x;}

    public void setYScale (double x) {yScale = x;}

    public double getXScale () {return xScale;}

    public void setTransX (double x) {transX = x;}

    public void setTransY (double x) {transY = x;}

    public double getTransX () {return transX;}

    public double getTransY () {return transY;}

    public void setRunwayLength(double x) {
        runwayLength = x;
        draw();
    }

    public void setLDAlength(double x) {
        this.LDAlength = x;
    }

    public void setTORAlength(double x) {
        this.TORAlength = x;
    }

    public void setTODAlength(double x) {
        this.TODAlength = x;
    }

    public void setASDAlength(double x) {
        this.ASDAlength = x;
    }

    public void setState(Calc.FlightModes x) {
        this.state = x;
    }

    public void setDisplacedThreshold(double x) {
        this.displacedThreshold = x;
    }

    public void setObstacleWidth(double x) {
        this.obstacleWidth = x;
    }

    public void setObstacleHeight(double x) {
        this.obstacleHeight = x;
    }

    public void setObstacleDistance(int x) {
        this.obstacleDistance = x;
    }

    public void setObstacleThres(int x) {
        this.obstacleThres = x;
    }

    public void setRESAlength(double x) {
        this.RESAlength = x;
    }

    public void setBlastLength(double x) {
        this.blastLength = x;
    }

    public void setStripEndLength(double x) {
        this.stripEndLength = x;
    }

    public void setStopwayLength(double x) {
        this.stopwayLength = x;
    }

    public void setClearwayLength(double x) {
        this.clearwayLength = x;
    }

    @Override
    public boolean isResizable() {
        return true;
    }

    @Override
    public double prefWidth(double height) {
        return getWidth();
    }

    @Override
    public double prefHeight(double width) {
        return getHeight();
    }

    public void setDistMarkers(boolean selected) {
        this.drawMarkers = selected;
    }

    public void setRunwayCanvasListener(RunwayCanvasListener listener) {
        runwayCanvasListener = listener;
    }

    public void setName(String name) {
        this.name = name;
        draw();
    }

    public void setShowAeroplane(boolean selected) {
        this.showAeroplane = selected;
    }

    public abstract void draw();

}
