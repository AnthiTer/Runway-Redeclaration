package uk.ac.soton.seg.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * An individual aiport
 */
public class Airport {
    private ObservableList<Runway> runways;
    private String name;
    private String identifier;

    /**
     * Create an Airport with no name or identifier
     */
    public Airport() {
        this(null, null);
    }

    /**
     * Create an Airport with given name and identifier
     * 
     * @param identifier ICAO location identifier
     * @param name       airport name
     */
    public Airport(String identifier, String name) {
        this.runways = FXCollections.<Runway>observableArrayList();
        this.name = name;
        this.identifier = identifier;
    }

    /**
     * @return name of the airport.
     */
    public String getName() {
        return name;
    }

    /**
     * Set the name of the airport.
     * 
     * @param name airport name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Get the airports identifier code.
     * 
     * @return airport ICAO location identifier
     */
    public String getIdentifier() {
        return identifier;
    }

    /**
     * Get the airports identifier code.
     * 
     * @param identifier airport ICAO location identifier
     */
    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    /**
     * @return human-readable string that represents the airport in the format
     *         "{identifier} - {name}"
     */
    public String getPrettyName() {
        return getIdentifier() + " - " + getName();
    }

    /**
     * Add a runway to the airport
     * 
     * @param runway
     */
    public void addRunway(Runway runway) {
        this.runways.add(runway);
    }

    /**
     * @return a list of runways this airport has
     */
    public ObservableList<Runway> getRunways() {
        return runways;
    }

    // /**
    // * Clear this airport's runways, and add new runways to replace them
    // *
    // * @param runways new runways to add
    // */
    // public void setRunways(Collection<Runway> runways) {
    // this.runways.clear();
    // this.runways.addAll(runways);
    // }

    /**
     * Find a runway this airport has by its designator
     * 
     * @param designator ICAO location identifier to search by
     * @return the runwaty with that identifier, or null if it can't be found
     */
    public Runway getRunwayByDesignator(String designator) {
        var filtered = this.runways.filtered((Runway r) -> r.getDesignator() == designator);
        return filtered.size() > 0 ? filtered.get(0) : null;
    }
}
