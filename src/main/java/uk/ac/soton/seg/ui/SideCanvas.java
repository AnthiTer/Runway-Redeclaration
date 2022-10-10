package uk.ac.soton.seg.ui;

import java.io.FileNotFoundException;
import java.util.ArrayList;

import javafx.scene.canvas.GraphicsContext;
import uk.ac.soton.seg.util.LogManager;
import uk.ac.soton.seg.util.Logger;

/**
 * Canvas for drawing the side view of the runway
 * Listens to MainController class for size of UI elements and the current take off/landing state
 */
public class SideCanvas extends RunwayCanvas {
    private static Logger log = LogManager.getLog(SideCanvas.class.getName());
    /** Runway that the canvas is displaying */
    SideCanvasRunway runway;

    /**
     * Constructs and initialises runway
     * Runway starts blanks and is populated on request of mainController
     * Runway contents are drawn relative to the canvas' size and scale to mouse-wheel
     */
    public SideCanvas() {
        super();
    }

    public void draw() {
        GraphicsContext gc = getGraphicsContext2D();
        this.gc = gc;
        gc.save();
        //On draw request, clear canvas
        gc.clearRect(0, 0, this.getWidth(), this.getHeight());

        gc.translate(transX,transY);
        gc.scale(xScale,yScale);

        //Construct and draw side runway
        this.runway = new SideCanvasRunway(runwayLength, runwayHeight,(this.getWidth() - runwayLength)/2,this.getHeight()/2);
        runway.drawRunway(name, gc);

        String[] labels = {"LDA","TORA","ASDA","TODA"};
        double[][] values = {};
        var rLength = runwayLength - Math.max(stopwayLength,clearwayLength);
        switch (state) {
            case LAND_OVER:
                values = new double[][] {
                        {rLength - LDAlength, rLength },
                        {rLength - TORAlength, rLength },
                        {rLength + stopwayLength - ASDAlength, rLength + stopwayLength},
                        {rLength + clearwayLength - TODAlength, rLength + clearwayLength}
                };
                drawArrow("Landing Direction",true);

                //Check if there is space to draw the TOCS line and the plane could land over obstacle
                if ((obstacleThres + obstacleWidth < rLength - LDAlength )&&(LDAlength > 0)) {
                    drawTOCS(obstacleThres + obstacleWidth,-obstacleHeight,rLength - LDAlength,0);
                }

                drawObstacle(obstacleThres);
                if (showAeroplane) {
                    var planeA = new DrawSidePlane(((this.getWidth() - runwayLength)/2) + obstacleThres,this.getHeight()/2 - 50,15,true);
                    var planeB = new DrawSidePlane(((this.getWidth() - runwayLength)/2) + rLength + clearwayLength - TODAlength ,this.getHeight()/2,0,true);
                    try {
                        //Draw landing plane if there is an area to land
                        if (LDAlength > 0) {
                            planeA.draw(gc);
                        }
                        //Draw taking off plane if there is distance for take-off
                        if (TODAlength > 0 || ASDAlength > 0) {
                            planeB.draw(gc);
                        }
                    } catch (FileNotFoundException e) {
                        log.error("Cannot load plane image");
                    }
                }
                break;
            case LAND_TOWARDS:
                values = new double[][] {
                        {displacedThreshold, displacedThreshold + LDAlength},
                        {0, TORAlength},
                        {0, ASDAlength},
                        {0, TODAlength}
                };
                drawArrow("Landing Direction",true);

                //Check if there is space to draw the TOCS line and the plane could take off over obstacle
                if ((TORAlength + stripEndLength < obstacleThres)&&(TORAlength > 0)) {
                    drawTOCS(TORAlength + stripEndLength,0,obstacleThres,-obstacleHeight);
                }

                drawObstacle(obstacleThres);

                if (showAeroplane) {
                    var plane = new DrawSidePlane(((this.getWidth() - runwayLength)/2) + 5 ,this.getHeight()/2);
                    try {
                        if ((obstacleThres >= 50)&&((TORAlength > 0)||(LDAlength > 0))) {
                            plane.draw(gc);
                        }
                    } catch (FileNotFoundException e) {
                        log.error("Cannot load plane image");
                    }
                }
                break;
            default:
                break;
        }

        //Draw distance markers plus direction arrow depending on runway state
        if (drawMarkers) {
            var markers = new ArrayList<DistanceMarker>();
            for (int i = 0; i < labels.length; i++) {
                markers.add(new DistanceMarker(labels[i],
                        (this.getWidth() - runwayLength) / 2 + values[i][0],
                        (this.getHeight() - 80) / 2 - 30 - (15 * i),
                        (this.getWidth() - runwayLength) / 2 + values[i][1],
                        (this.getHeight() - 80) / 2 - 30 - (15 * i)));
                for (var marker : markers) {
                    marker.draw(gc);
                }
            }
        }

        gc.restore();
    }

    private void drawObstacle(double x) {
        gc.strokeRect((this.getWidth() - runwayLength ) / 2 + x,
                (this.getHeight())/2 - obstacleHeight,
                   obstacleWidth,
                   obstacleHeight);
    }

    /**
     * Draw TOCS line from a given point to another
     * Draws the dotted gradient that shows the slope that the plane takes when going over the obstacle
     * @param x1 x co ord of first point
     * @param y1 y co ord of first point
     * @param x2 x co ord of second point
     * @param y2 y co ord of second point
     */
    private void drawTOCS(double x1, double y1, double x2, double y2) {
        gc.save();
        gc.setLineDashes(5);
        gc.strokeLine((this.getWidth() - runwayLength ) / 2 + x1,
                (this.getHeight()) / 2 + y1,
                (this.getWidth() - runwayLength ) / 2 + x2,
                (this.getHeight()) / 2 + y2);
        gc.restore();
    }

}
