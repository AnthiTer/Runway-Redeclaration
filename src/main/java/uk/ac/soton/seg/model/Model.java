package uk.ac.soton.seg.model;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import uk.ac.soton.seg.XMLHandler;
import uk.ac.soton.seg.util.LogManager;
import uk.ac.soton.seg.util.Logger;

/**
 * Singleton class for the model for the whole program
 */
public class Model {
    private static Model instance;

    private ObservableList<Airport> airports = FXCollections.<Airport>observableArrayList();

    private ObservableList<Obstacle> obstacles = FXCollections.<Obstacle>observableArrayList();

    public static String rscDir = "resources";
    public static String cPath = System.getProperty("user.dir");
    public static String rscAbs = cPath + System.getProperty("file.separator") + rscDir;

    private static Logger log = LogManager.getLog(Model.class.getName());

    /**
     * @return the instance of Model (singleton)
     */
    public static synchronized Model getModel() {
        if (instance == null) {
            instance = new Model();
        }
        return instance;
    }

    /**
     * @return the list of all airports stored in the model
     */
    public ObservableList<Airport> getAirports() {
        return this.airports;
    }

    // public synchronized void setAirports(List<Airport> airports) {
    //     this.airports.clear();
    //     this.airports.addAll(airports);
    // }

    /**
     * @return the list of all obstacles stored in the model
     */
    public ObservableList<Obstacle> getObstacles() {
        return this.obstacles;
    }

    // public synchronized void setObstacles(List<Obstacle> obstacles) {
    //     this.obstacles.clear();
    //     this.obstacles.addAll(obstacles);
    // }

    public void loadTestData() {
        getAirports().clear();
        getObstacles().clear();
        var sotonAirport = new Airport("EGHI", "Southampton");
        var sotonAirportRunway02 = new Runway("02", 1723, 1831, 1723, 1650);
        sotonAirport.addRunway(sotonAirportRunway02);
        var sotonAirportRunway20 = new Runway("20", 1650, 1805, 1650, 1605);
        sotonAirport.addRunway(sotonAirportRunway20);
        Model.getModel().getAirports().add(sotonAirport);
        
        // Model.getModel().getObstacles().add(new Obstacle("Shipping Crate", 2.6f, 2.4f, 12.2f));
        // Model.getModel().getObstacles().add(new Obstacle("Airplane", 24, 80, 73));
        // Model.getModel().getObstacles().add(new Obstacle("Shuttle Bus", 10, 10, 7));
        // Model.getModel().getObstacles().add(new Obstacle("Felled Tree", 3, 1.5f, 52));
        // Model.getModel().getObstacles().add(new Obstacle("Car", 2.8f, 2, 4.5f));
        // Model.getModel().getObstacles().add(new Obstacle("Fire Truck", 2.5f, 2.3f, 10.5f));
        

        getObstacles().addAll(
                // new Obstacle("Bus", 4, 10, 10),
                new Obstacle("Plane", 10, 55, 80),
                new Obstacle("Car", 2, 2, 3),
                new Obstacle("Shuttle bus", 4, 3, 7),
                new Obstacle("Fire Truck", 4, 3, 6),
                new Obstacle("Shipping Crate", 3, 3, 6),
                new Obstacle("Felled tree", 2, 10, 10));
    }



    // public void loadFiles(ArrayList<Path> paths) throws IOException {
    //     for(Path p: paths){
    //         Wrapper wrap = (Wrapper) XMLHandler.loadObject(p.toString());
    //         Class type = wrap.getWrappedClass();
    //         if(type == Airport.class){
    //             ArrayList<Airport> airports = new ArrayList<Airport>();
    //             Airport[] airp = (Airport[]) wrap.getWrappedObject();
    //             Collections.addAll(airports,airp);
    //             ObservableList<Airport> air = FXCollections.observableList(airports);
    //             setAirports(air);
    //         } else if (type == Obstacle.class) {
    //             ArrayList<Obstacle> obA = new ArrayList<Obstacle>();
    //             Obstacle[] obs = (Obstacle[]) wrap.getWrappedObject();
    //             Collections.addAll(obA,obs);
    //             ObservableList<Obstacle> ob = FXCollections.observableList(obA);
    //             setObstacles(ob);
    //         } else {
    //             log.error("error");
    //         }
    //     }
    // }

    // public void loadResources() throws IOException {
    //     loadPredefined();
    //     if(!new File(rscDir).mkdirs()){
    //         String rscfolder = cPath + separator + "resources";
    //         ArrayList<Path> paths = new ArrayList<Path>();
    //         Files.walk(Paths.get(rscfolder),1).filter(FileUtils::isXML).forEach(paths::add);
    //         loadFiles(paths);
    //     }
    //     else{
    //         log.error("creating dir");
    //     }
    // }


    // public void saveToFile() {
    //     Model.getModel().getAirports().stream().forEach(XMLHandler::saveAirport);
    //     var tempAirports = new ArrayList<Airport>();
    //     tempAirports.addAll(Model.getModel().getAirports());

    //     XMLHandler.saveAirports(tempAirports);
    // }
}
