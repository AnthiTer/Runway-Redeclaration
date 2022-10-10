package uk.ac.soton.seg.util;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.io.IOException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javafx.collections.FXCollections;
import uk.ac.soton.seg.model.Model;
import uk.ac.soton.seg.model.Obstacle;

public class ObstacleStringConverterTest {
    private ObstacleStringConverter converter;
    private Obstacle obstacle = new Obstacle("Bus", 3, 2, 10);

    @BeforeEach
    void setup() throws IOException {
        var model = Model.getModel();
        model.loadTestData();
        // TODO fix loading sample obstacles then uncomment all commented lines 
        // model.loadResources();
        // obstacle = model.getObstacles().stream().filter(a -> a.getName().equalsIgnoreCase("bus")).findAny().get();
        // converter = new ObstacleStringConverter(model.getObstacles());
        converter = new ObstacleStringConverter(FXCollections.observableArrayList(obstacle));
    }

    @Test
    void testToString() {
        assertAll(
                () -> assertEquals("Bus (3x10x2)", converter.toString(obstacle)),
                () -> assertEquals("", converter.toString(null)));

    }

    @Test
    void testFromString() {
        assertAll(
                () -> assertSame(obstacle, converter.fromString("Bus")),
                () -> assertSame(null, converter.fromString("Big Rock Obstacle that doesn't exist")),
                () -> assertSame(null, converter.fromString("")));
    }
}
