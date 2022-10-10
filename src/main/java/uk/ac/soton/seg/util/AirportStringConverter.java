package uk.ac.soton.seg.util;

import javafx.collections.ObservableList;
import javafx.util.StringConverter;
import uk.ac.soton.seg.model.Airport;

/**
 * Helper class to convert between Airport instances and a string (useful for
 * UI)
 */
public class AirportStringConverter extends StringConverter<Airport> {
    private final ObservableList<Airport> airports;

    public AirportStringConverter(ObservableList<Airport> airports) {
        this.airports = airports;
    }

    @Override
    public String toString(Airport airport) {
        if (airport == null) {
            return "";
        }
        return airport.getPrettyName();
    }

    @Override
    public Airport fromString(String str) {
        var identifier = str.split(" ")[0];
        ObservableList<Airport> filtered = airports.filtered((Airport airport) -> airport.getIdentifier().equals(identifier));
        return filtered.size() > 0 ? filtered.get(0) : null;
    }
}