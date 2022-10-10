package uk.ac.soton.seg.ui;

import java.io.FileNotFoundException;
import java.util.ArrayList;

import javafx.event.EventHandler;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import javafx.scene.transform.Affine;
import javafx.scene.transform.Rotate;
import javafx.util.Pair;
import uk.ac.soton.seg.util.LogManager;
import uk.ac.soton.seg.util.Logger;

/**
 * Canvas for drawing the top view of the runway
 * Extends side canvas
 * Has extra functionality over sideCanvas, draws clear and graded area and rotates view to bearing on user input
 */
public class TopCanvas extends RunwayCanvas {
    //Runway that the canvas is displaying
    TopCanvasRunway canvasRunway;
    //Draw runway rotated to bearing or draw horizontally left to right
    private boolean drawRotated = true;
    //Draw graded area or not
    private boolean drawGradedArea = false;
    //Bearing to draw runway rotated if allowed
    private Double bearing = Double.valueOf(0);

    private static Logger log = LogManager.getLog(TopCanvas.class.getName());

    /**
     * Constructs and initialises runway
     * Runway starts blanks and is populated on request of mainController
     * Runway contents are drawn relative to the canvas' size and scale to mouse-wheel
     */
    public TopCanvas() {
        super();
    }

    @Override
    public void draw() {
        GraphicsContext gc = getGraphicsContext2D();
        this.gc = gc;
        gc.save();
        //clear runway on redraw
        gc.clearRect(0, 0, this.getWidth(), this.getHeight());

        //if rotated, set gc to draw at given bearing
        if (drawRotated) {
            gc.transform(new Affine(new Rotate(bearing - 90, this.getWidth()/2, this.getHeight()/2)));
        }

        gc.translate(transX,transY);
        gc.scale(xScale,yScale);


        //Draw clear and graded area
        if (drawGradedArea) {
            var cga = new ClearGradedArea(this.getWidth() / 2, this.getHeight() / 2, 500, 350, 80, 100, 60, 100);
            cga.draw(gc);
        }

        //Construct top runway
        this.canvasRunway = new TopCanvasRunway(runwayLength, runwayHeight,(this.getWidth() - runwayLength)/2,(this.getHeight() - runwayHeight)/2, showAeroplane);
        canvasRunway.drawRunway(name, gc);

        //draw distance markers
        if (drawMarkers) {
            drawMarkers();
        }

        //drawPlane
        if (showAeroplane) {
            switch(state) {
                case LAND_OVER:
                    var planeA = new DrawTopPlane(((this.getWidth() - runwayLength)/2) + runwayLength - Math.max(stopwayLength,clearwayLength) - TORAlength ,this.getHeight()/2);
                    try {
                        if (TORAlength > 0) {
                            planeA.draw(gc);
                        }
                    } catch (FileNotFoundException e) {
                        log.error("Cannot load plane image");
                    }
                    break;
                case LAND_TOWARDS:
                    var planeB = new DrawTopPlane(((this.getWidth() - runwayLength)/2) + 5 ,this.getHeight()/2);
                    try {
                        if (obstacleThres >= 50) {
                            planeB.draw(gc);
                        }
                    } catch (FileNotFoundException e) {
                        log.error("Cannot load plane image");
                    }
                    break;
                default:
                    break;
            }
        }


        drawObstacle();

        gc.restore();
    }

    public void setBearing(Double x) {
        this.bearing = x;
        draw();
    }

    public void setRunwayHeight(double y) {
        runwayHeight = y;
        draw();
    }

    /**
     * Draws the given distances markers
     */
    public void drawMarkers () {
        var flipText = false;
        if (bearing > 180) {
            flipText = true;
        }

        String[] labels = {"LDA","TORA","ASDA","TODA"};
        double[][] values= new double[][] {
                {displacedThreshold, LDAlength},
                {0, TORAlength},
                {0, ASDAlength},
                {0, TODAlength}
        };
        var rLength = runwayLength - Math.max(stopwayLength,clearwayLength);
        switch (state) {
            case LAND_OVER:
                values = new double[][] {
                        {rLength - LDAlength, rLength },
                        {rLength - TORAlength, rLength },
                        {rLength + stopwayLength - ASDAlength, rLength + stopwayLength},
                        {rLength + clearwayLength - TODAlength, rLength + clearwayLength}
                };
                break;
            case LAND_TOWARDS:
                values = new double[][] {
                        {displacedThreshold, displacedThreshold + LDAlength},
                        {displacedThreshold, displacedThreshold + TORAlength},
                        {displacedThreshold, displacedThreshold + ASDAlength},
                        {displacedThreshold, displacedThreshold + TODAlength}
                };
                break;
            default:
                break;
        }


        var markers = new ArrayList<DistanceMarker>();
        for (int i = 0; i < labels.length; i++) {
            markers.add(new DistanceMarker(labels[i],
                    (this.getWidth() - runwayLength) / 2 + values[i][0],
                    (this.getHeight() - runwayHeight) / 2 - 10 - (15*i),
                    (this.getWidth() - runwayLength) / 2 + values[i][1],
                    (this.getHeight() - runwayHeight) / 2 - 10 - (15*i),
                    flipText));
        }
        for (var marker : markers) {
            marker.draw(gc);
        }

        drawArrow("Take-off direction", true, flipText);

        var threshold = new DisplacedThreshold((this.getWidth() - runwayLength) / 2 + displacedThreshold,
                (this.getHeight() - runwayHeight) / 2,
                (this.getWidth() - runwayLength) / 2 + displacedThreshold,
                (this.getHeight() + runwayHeight) / 2);
        threshold.draw(gc);
    }

    protected void drawObstacle() {
        var obstacle = new DrawObstacle((this.getWidth() - runwayLength)/2 + obstacleThres,(this.getHeight() - obstacleHeight )/ 2+obstacleDistance,obstacleWidth,obstacleHeight);
        obstacle.draw(this.gc);
    }

    /**
     * Toggle drawing the runway rotated to its bearing or at 0
     */
    public void toggleRotate() {
        this.drawRotated = !this.drawRotated;
    }



    public void setDrawGradedArea(boolean selected) {
        this.drawGradedArea = selected;
    }
}
