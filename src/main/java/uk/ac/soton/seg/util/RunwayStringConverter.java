package uk.ac.soton.seg.util;

import javafx.util.StringConverter;
import uk.ac.soton.seg.model.Airport;
import uk.ac.soton.seg.model.Runway;

/**
 * Helper class to convert between Runway instances and a String (useful for UI)
 */
public class RunwayStringConverter extends StringConverter<Runway> {
    private final Airport airport;

    public RunwayStringConverter(Airport airport) {
        this.airport = airport;
    }

    @Override
    public String toString(Runway runway) {
        if (runway == null) {
            return "";
        }
        return (runway.getDesignator() + (runway.getRemarks() == "" ? "" : " " + runway.getRemarks()));
    }

    @Override
    public Runway fromString(String str) {
        var filtered = airport.getRunways().filtered((Runway r) -> str.startsWith(r.getDesignator()));
        if (filtered.size() == 1) {
            return filtered.get(0);
        }
        var searchRemarks = str.split(" ", 2).length == 1 ? "" : str.split(" ", 2)[1];
        filtered = filtered.filtered(r -> r.getRemarks().equals(searchRemarks));
        return filtered.size() > 0 ? filtered.get(0) : null;
    }
}