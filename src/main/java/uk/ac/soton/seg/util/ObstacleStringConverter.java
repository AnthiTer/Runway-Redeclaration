package uk.ac.soton.seg.util;

import java.util.regex.Pattern;

import javafx.collections.ObservableList;
import javafx.util.StringConverter;
import uk.ac.soton.seg.model.Obstacle;

/**
 * Helper class to convert between Airport instances and a string (useful for
 * UI)
 */
public class ObstacleStringConverter extends StringConverter<Obstacle> {

    private final ObservableList<Obstacle> obstacles;

    private static final Pattern pattern = Pattern.compile("\\([0-9]+x[0-9]+x[0-9]+\\)$");

    public ObstacleStringConverter(ObservableList<Obstacle> obstacles) {
        this.obstacles = obstacles;
    }

    @Override
    public String toString(Obstacle obstacle) {
        if (obstacle == null) {
            return "";
        }
        return String.format("%s (%dx%dx%d)",
                obstacle.getName(), obstacle.getHeight(), obstacle.getLength(), obstacle.getWidth());
    }

    @Override
    public Obstacle fromString(String str) {

        ObservableList<Obstacle> filtered = obstacles.filtered((Obstacle obstacle) -> {
            return obstacle
                    .getName()
                    .equals(pattern.matcher(str).replaceFirst(""));
        });
        return filtered.size() > 0 ? filtered.get(0) : null;
    }
}
