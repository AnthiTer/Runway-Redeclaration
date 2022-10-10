package uk.ac.soton.seg.event;

import uk.ac.soton.seg.model.Airport;
import uk.ac.soton.seg.model.Obstacle;

import java.io.File;

public interface ExportXML {

    /**
     * Exports an airport in XML form
     * @param air
     * @param f
     * @return true if exportation is successful, false otherwise
     */
    public boolean exportAirport (Airport air, File f);

    /**
     * Exports an obstacle in XML form
     * @param obs
     * @param f
     * @return true if exportation is successful, false otherwise
     */
    public boolean exportObstacle (Obstacle obs, File f);
}
