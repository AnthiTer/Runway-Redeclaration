package uk.ac.soton.seg.event;

import java.io.File;
import java.util.List;

import uk.ac.soton.seg.model.Airport;
import uk.ac.soton.seg.model.Obstacle;

public interface ImportXML {

    /**
     * Import an airport from an XML file
     * @param f
     * @return airport parsed
     */
    public Airport importAirport (File f);

    /**
     * Import obstacles from an XML file
     * @param f
     * @return list of obstacles
     */
    public List<Obstacle> importObstacle (File f);
}

